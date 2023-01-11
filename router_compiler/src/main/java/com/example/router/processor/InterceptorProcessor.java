package com.example.router.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.example.router.annotation.Interceptor;
import com.example.router.util.RouterProcessorConst;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * 拦截器类生成器
 */
@AutoService(Processor.class)
public class InterceptorProcessor extends BaseProcessor {
    private static final Map<String, TypeName> interceptorMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(1) {{
            add(Interceptor.class.getCanonicalName());
        }};
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Interceptor.class);
            try {
                logger.i(">>> parseInterceptors start <<<");
                parseInterceptors(elements);
                logger.i(">>> parseInterceptors end <<<");
            } catch (Exception e) {
                logger.e(e);
            }
            return true;
        }
        return false;
    }

    private void parseInterceptors(Set<? extends Element> elements) throws IOException {
        if (CollectionUtils.isEmpty(elements)) return;

        TypeMirror iInterceptorTypeMirror = elementUtils.getTypeElement(RouterProcessorConst.TYPE_IINTERCEPTOR).asType();

        for (Element element : elements) {
            Interceptor interceptor = element.getAnnotation(Interceptor.class);
            checkGenerateParamValid(element, iInterceptorTypeMirror, interceptor);

            if (interceptorMap.containsKey(interceptor.name())) {
                throw new IllegalArgumentException("interceptor name = \"" + interceptor.name() + "\" already exist");
            }
            interceptorMap.put(interceptor.name(), ClassName.get((TypeElement) element));
        }

        generateInterceptorGroup(iInterceptorTypeMirror);
    }

    private void checkGenerateParamValid(Element element, TypeMirror iInterceptorTypeMirror, Interceptor interceptor) {
        TypeMirror elementTypeMirror = element.asType();
        if (!typeUtils.isSubtype(elementTypeMirror, iInterceptorTypeMirror)) {
            throw new UnsupportedOperationException("@Interceptor must annotate to class implements IInterceptor");
        }
        if (StringUtils.isEmpty(interceptor.name())) {
            throw new IllegalArgumentException(element.getSimpleName() + " @Interceptor name is empty");
        }
    }

    // 生成拦截器组
    private void generateInterceptorGroup(TypeMirror iInterceptorTypeMirror) throws IOException {
        /**
         * public class appInterceptorGroup implements IInterceptorGroup {
         *      @Override
         *      public void loadInto(Map<String, Class<? extends IInterceptor> interceptorClassMap) {
         *          interceptorClassMap.put("name", appInterceptor.class);
         *          ...
         *      }
         * }
         */
        TypeElement iInterceptorGroupTypeElement = elementUtils.getTypeElement(RouterProcessorConst.TYPE_IINTERCEPTOR_GROUP);

        ParameterizedTypeName interceptorParameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iInterceptorTypeMirror))
                )
        );
        ParameterSpec interceptorParameterSpec = ParameterSpec.builder(
                interceptorParameterizedTypeName, RouterProcessorConst.INTERCEPTOR_PARAMETER_NAME).build();

        MethodSpec.Builder interceptorMethodSpecBuilder = MethodSpec.methodBuilder(RouterProcessorConst.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(interceptorParameterSpec);

        for (Map.Entry<String, TypeName> entry : interceptorMap.entrySet()) {
            // interceptorClassMap.put("name", appInterceptor.class);
            String name = entry.getKey();
            TypeName interceptorClassName = entry.getValue();
            interceptorMethodSpecBuilder
                    .addStatement("interceptorClassMap.put($S, $T.class)", name, interceptorClassName);
        }

        // module/build/intermediates/javac/debug/classes/com/example/router/interceptor/moduleNameInterceptorGroup
        String interceptorGroupClassName = MessageFormat.format(RouterProcessorConst.INTERCEPTOR_GROUP_CLASS_NAME, moduleName);
        logger.i(">>> start generate interceptorGroup, class name = " + interceptorGroupClassName + " <<<");
        JavaFile.builder(RouterProcessorConst.GENERATE_INTERCEPTOR_GROUP_PACKAGE,
                TypeSpec.classBuilder(interceptorGroupClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addSuperinterface(ClassName.get(iInterceptorGroupTypeElement))
                        .addMethod(interceptorMethodSpecBuilder.build())
                        .build()
        ).build().writeTo(filer);
        logger.i(">>> end generate interceptorGroup, class name = " + interceptorGroupClassName + " <<<");
    }
}
