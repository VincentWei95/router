package com.example.router.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.example.router.annotation.Autowired;
import com.example.router.enums.TypeKind;
import com.example.router.util.ElementTypeExchanger;
import com.example.router.util.RouterProcessorConst;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * 界面 Intent 依赖注入变量类生成器
 */
@AutoService(Processor.class)
public class AutowiredProcessor extends BaseProcessor {
    private ElementTypeExchanger typeExchanger;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeExchanger = new ElementTypeExchanger(typeUtils, elementUtils);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(1) {{
            add(Autowired.class.getCanonicalName());
        }};
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Autowired.class);
            try {
                logger.i(">>> collect autowired fields start <<<");
                Map<TypeElement, List<Element>> autowiredFieldsMap = collectAutowiredFieldsMap(elements);
                logger.i(">>> collect autowired fields end <<<");

                logger.i(">>> parseAutowired start <<<");
                parseAutowired(autowiredFieldsMap);
                logger.i(">>> parseAutowired end <<<");
            } catch (Exception e) {
                logger.e(e);
            }
        }
        return false;
    }

    private Map<TypeElement, List<Element>> collectAutowiredFieldsMap(Set<? extends Element> elements) throws IllegalAccessException {
        if (CollectionUtils.isEmpty(elements)) return Collections.emptyMap();

        // key：Activity/Fragment element
        // value：field elements，添加了 @Autowired 注解的变量 element 列表
        Map<TypeElement, List<Element>> autowiredFieldsMap = new HashMap<>();
        for (Element element : elements) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

            if (element.getModifiers().contains(Modifier.PRIVATE)) {
                throw new IllegalAccessException("field cannot be private, " +
                        "field name [" + element.getSimpleName() + "] in class [" + enclosingElement.getQualifiedName() + "]");
            }

            if (autowiredFieldsMap.containsKey(enclosingElement)) {
                autowiredFieldsMap.get(enclosingElement).add(element);
            } else {
                List<Element> fields = new ArrayList<>();
                fields.add(element);
                autowiredFieldsMap.put(enclosingElement, fields);
            }
        }

        logger.i("autowiredFieldsMap = " + autowiredFieldsMap);

        return autowiredFieldsMap;
    }

    private void parseAutowired(Map<TypeElement, List<Element>> autowiredFieldsMap)
            throws IOException, IllegalAccessException {
        if (autowiredFieldsMap.isEmpty()) return;

        for (Map.Entry<TypeElement, List<Element>> entry : autowiredFieldsMap.entrySet()) {
            generateAutowired(entry.getKey(), entry.getValue());
        }
    }

    private void generateAutowired(TypeElement classElement, List<Element> fieldElements)
            throws IOException, IllegalAccessException {
        /**
         * public class MainActivityAutowired implements IAutowired {
         *      @Override
         *      public void inject(Object target) {
         *          MainActivity injector = (MainActivity) target;
         *          injector.age = injector.getIntent().getIntExtra(key, injector.age);
         *          ...
         *      }
         * }
         */
        TypeElement iAutowiredTypeElement = elementUtils.getTypeElement(RouterProcessorConst.TYPE_IAUTOWIRED);
        TypeMirror activityTypeMirror = elementUtils.getTypeElement(RouterProcessorConst.TYPE_ACTIVITY).asType();
        TypeMirror fragmentTypeMirror = elementUtils.getTypeElement(RouterProcessorConst.TYPE_FRAGMENT).asType();

        ParameterSpec autowiredParameterSpec = ParameterSpec.builder(
                TypeName.OBJECT, RouterProcessorConst.AUTOWIRED_PARAMETER_NAME).build();

        MethodSpec.Builder autowiredMethodSpecBuilder = MethodSpec.methodBuilder(RouterProcessorConst.METHOD_INJECT)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(autowiredParameterSpec);

        autowiredMethodSpecBuilder.addStatement("$T injector = ($T) target",
                ClassName.get(classElement), ClassName.get(classElement));

        for (Element fieldElement : fieldElements) {
            Autowired fieldConfig = fieldElement.getAnnotation(Autowired.class);
            String autowiredKey = fieldConfig.name();
            boolean isRequired = fieldConfig.required();

            boolean isActivity;
            String injectorName = "injector";
            // 注解了 @Autowired 属性名
            String fieldName = fieldElement.getSimpleName().toString();
            // 变量可能有自己设置默认值
            String defaultFieldValue = injectorName + "." + fieldName;
            if (typeUtils.isSubtype(classElement.asType(), activityTypeMirror)) {
                isActivity = true;
            } else if (typeUtils.isSubtype(classElement.asType(), fragmentTypeMirror)) {
                isActivity = false;
            } else {
                throw new IllegalAccessException("The field [" + fieldName + "] need autowired from intent, class must be " + RouterProcessorConst.TYPE_ACTIVITY + " or " + RouterProcessorConst.TYPE_FRAGMENT);
            }

            String field = injectorName + "." + fieldName;
            String statement = buildStatement(injectorName, field, fieldElement.asType(),
                    defaultFieldValue, typeExchanger.exchange(fieldElement), isActivity);
            autowiredMethodSpecBuilder.addStatement(statement,
                    StringUtils.isEmpty(autowiredKey) ? fieldName : autowiredKey);

            // isPrimitive：基础数据类型
            // 只检查对象类型
            if (isRequired && !fieldElement.asType().getKind().isPrimitive()) {
                autowiredMethodSpecBuilder.beginControlFlow("if (" + field + " == null)")
                        .addStatement("throw new IllegalArgumentException($S)", "[" + fieldName + "] is required field so value cannot be null, field in class [" + classElement.getQualifiedName().toString() + "]")
                        .endControlFlow();
            }
        }

        // module/build/intermediates/javac/debug/classes/modulePackageName/NameAutowired（Name 是 Activity 或 Fragment 名称）
        String classQualifiedName = classElement.getQualifiedName().toString();
        String classPackageName = classQualifiedName.substring(0, classQualifiedName.lastIndexOf("."));
        String className = classElement.getSimpleName().toString();
        String autowiredClassName = MessageFormat.format(RouterProcessorConst.AUTOWIRED_CLASS_NAME, className);
        logger.i(">>> start generate autowired, class name = " + autowiredClassName + " <<<");
        JavaFile.builder(classPackageName,
                TypeSpec.classBuilder(autowiredClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addSuperinterface(ClassName.get(iAutowiredTypeElement))
                        .addMethod(autowiredMethodSpecBuilder.build())
                        .build()
                ).build().writeTo(filer);
        logger.i(">>> end generate autowired, class name = " + autowiredClassName + " <<<");
    }

    private String buildStatement(String injectorName,
                                  String field,
                                  TypeMirror fieldTypeMirror,
                                  String defaultFieldValue,
                                  int type,
                                  boolean isActivity) {
        StringBuilder sb = new StringBuilder();
        // injector.age = injector.getIntent().getIntExtra(key, injector.age);
        TypeKind typeKind = TypeKind.values()[type];
        sb.append(field).append(" = ");
        if (typeKind == TypeKind.SERIALIZABLE) {
            sb.append("(").append(ClassName.get(fieldTypeMirror)).append(")");
        }
        sb.append(injectorName).append(".").append(isActivity ? "getIntent()." : "getArguments().");
        switch (typeKind) {
            case BOOLEAN:
                sb.append("getBoolean").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case BYTE:
                sb.append("getByte").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case SHORT:
                sb.append("getShort").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case INT:
                sb.append("getInt").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case LONG:
                sb.append("getLong").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case CHAR:
                sb.append("getChar").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case FLOAT:
                sb.append("getFloat").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case DOUBLE:
                sb.append("getDouble").append(isActivity ? "Extra" : "").append("($S, ").append(defaultFieldValue).append(")");
                break;
            case STRING:
                sb.append("getString").append(isActivity ? "Extra" : "").append("($S)");
                break;
            case SERIALIZABLE:
                sb.append("getSerializable").append(isActivity ? "Extra" : "").append("($S)");
                break;
            case PARCELABLE:
                sb.append("getParcelable").append(isActivity ? "Extra" : "").append("($S)");
                break;
        }
        return sb.toString();
    }
}
