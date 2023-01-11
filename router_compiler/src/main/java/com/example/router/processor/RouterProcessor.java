package com.example.router.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;
import com.example.router.annotation.Route;
import com.example.router.enums.RouteType;
import com.example.router.model.RouteMeta;
import com.example.router.util.ExtractRouteGroupUtils;
import com.example.router.util.RouterProcessorConst;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * 路由类生成器
 */
@AutoService(Processor.class)
public class RouterProcessor extends BaseProcessor {
    // 每个 module 会创建不同的 processor，编译时期只能校验单个 module 下生成的是唯一的
    private static final Map<String, List<RouteMeta>> routeGroupMap = new HashMap<>();
    private static final Map<String, String> generatedRouteGroupMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(1) {{
            add(Route.class.getCanonicalName());
        }};
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
            try {
                logger.i(">>> parseRoutes start <<<");
                parseRoutes(elements);
                logger.i(">>> parseRoutes end <<<");
            } catch (Exception e) {
                logger.e(e);
            }
            return true;
        }

        return false;
    }

    private void parseRoutes(Set<? extends Element> elements) throws IOException {
        if (CollectionUtils.isEmpty(elements)) return;

        for (Element element : elements) {
            Route route = element.getAnnotation(Route.class);
            checkGenerateParamsValid(element, route);

            RouteMeta routeMeta = new RouteMeta(RouteType.ACTIVITY, null, route.path(), route.group());
            routeMeta.setRawType(element);

            collectGenerateRouteGroupParams(routeMeta);
        }

        generateRouteGroups();

        generateRouteRoot();
    }

    private void checkGenerateParamsValid(Element element, Route route) {
        TypeMirror elementTypeMirror = element.asType();
        TypeMirror activityTypeMirror = elementUtils.getTypeElement(RouterProcessorConst.TYPE_ACTIVITY).asType();
        // 目前只支持注解在 Activity 设置
        if (!typeUtils.isSubtype(elementTypeMirror, activityTypeMirror)) {
            throw new UnsupportedOperationException("@Route(path = \"" + route.path() + "\") must annotate to Activity");
        }
        if (StringUtils.isEmpty(route.path())) {
            throw new IllegalArgumentException(element.getSimpleName() + " @Route path is empty");
        }
        if (!route.path().startsWith("/")) {
            throw new IllegalArgumentException(element.getSimpleName() + " @Route(path = \"" + route.path() + "\") path start must be '/' and contain more than 2 '/'");
        }
    }

    // 一个 module 可能会定义了多个 RouteGroup
    private void collectGenerateRouteGroupParams(RouteMeta routeMeta) {
        if (StringUtils.isEmpty(routeMeta.getGroup())) {
            // 如果没有设置路由组，截取路由路径字符作为 group
            String group = ExtractRouteGroupUtils.extract(routeMeta.getPath());
            routeMeta.setGroup(group);
        }

        List<RouteMeta> routeMetas = routeGroupMap.get(routeMeta.getGroup());
        if (CollectionUtils.isEmpty(routeMetas)) {
            routeMetas = new ArrayList<>();
            routeMetas.add(routeMeta);

            routeGroupMap.put(routeMeta.getGroup(), routeMetas);
        } else {
            routeMetas.add(routeMeta);
        }
    }

    // 生成路由组
    private void generateRouteGroups() throws IOException {
        /**
         * public class appRouteGroup implements IRouteGroup {
         *    @Override
         *    public void loadInto(Map<String, RouteMeta> routeMap) {
         * 		routeMap.put("path", routeMeta);
         * 		...
         *    }
         * }
         */
        ParameterizedTypeName routeGroupParameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );
        ParameterSpec routeGroupParameterSpec = ParameterSpec.builder(
                routeGroupParameterizedTypeName, RouterProcessorConst.ROUTE_GROUP_PARAMETER_NAME).build();

        ClassName routeMetaClassName = ClassName.get(RouteMeta.class);
        ClassName routeTypeClassName = ClassName.get(RouteType.class);

        for (Map.Entry<String, List<RouteMeta>> entry : routeGroupMap.entrySet()) {
            String groupName = entry.getKey();

            MethodSpec.Builder routeGroupMethodSpecBuilder = MethodSpec.methodBuilder(RouterProcessorConst.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(routeGroupParameterSpec);

            for (RouteMeta routeMeta : entry.getValue()) {
                // routeMap.put("path", RouteMeta.build("type", LoginActivity.class, "path", "group"));
                ClassName activityClassName = ClassName.get((TypeElement) routeMeta.getRawType());
                routeGroupMethodSpecBuilder.addStatement(
                        "routeMap.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S))",
                        routeMeta.getPath(),
                        routeMetaClassName,
                        routeTypeClassName,
                        activityClassName,
                        routeMeta.getPath(),
                        routeMeta.getGroup());
            }

            // module/build/intermediates/javac/debug/classes/com/example/router/group/groupNameRouteGroup
            TypeElement iRouteGroupTypeElement = elementUtils.getTypeElement(RouterProcessorConst.TYPE_IROUTE_GROUP);
            String routeGroupClassName = MessageFormat.format(RouterProcessorConst.ROUTE_GROUP_CLASS_NAME, groupName);
            logger.i(">>> start generate routeGroup, class name = " + routeGroupClassName + " <<<");
            JavaFile.builder(RouterProcessorConst.GENERATE_ROUTE_GROUP_PACKAGE,
                    TypeSpec.classBuilder(routeGroupClassName)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addSuperinterface(ClassName.get(iRouteGroupTypeElement))
                            .addMethod(routeGroupMethodSpecBuilder.build())
                            .build()
            ).build().writeTo(filer);
            logger.i(">>> end generate routeGroup, class name = " + routeGroupClassName + " <<<");

            generatedRouteGroupMap.put(groupName, routeGroupClassName);
        }
    }

    // 生成路由根节点
    private void generateRouteRoot() throws IOException {
        /**
         * public class appRouteRoot implements IRouteRoot {
         *    @Override
         *    public void loadInto(Map<String, Class<? extends IRouteGroup> routeGroupClassMap) {
         * 		routeGroupClassMap.put("group", appRouteGroup.class);
         * 		...
         *    }
         * }
         */
        TypeMirror routeGroupTypeMirror = elementUtils.getTypeElement(RouterProcessorConst.TYPE_IROUTE_GROUP).asType();
        ParameterizedTypeName routeRootParameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(routeGroupTypeMirror))
                )
        );
        ParameterSpec routeRootParameterSpec = ParameterSpec.builder(
                routeRootParameterizedTypeName, RouterProcessorConst.ROUTE_ROOT_PARAMETER_NAME).build();

        MethodSpec.Builder routeRootMethodSpecBuilder = MethodSpec.methodBuilder(RouterProcessorConst.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(routeRootParameterSpec);

        if (MapUtils.isNotEmpty(generatedRouteGroupMap)) {
            for (Map.Entry<String, String> entry : generatedRouteGroupMap.entrySet()) {
                // routeGroupClassMap.put("group", appRouteGroup.class);
                routeRootMethodSpecBuilder.addStatement(
                        "routeGroupClassMap.put($S, $T.class)", entry.getKey(), ClassName.get(RouterProcessorConst.GENERATE_ROUTE_GROUP_PACKAGE, entry.getValue()));
            }
        }

        // module/build/intermediates/javac/debug/classes/com/example/router/group/moduleNameRouteRoot
        TypeElement iRouteRootTypeElement = elementUtils.getTypeElement(RouterProcessorConst.TYPE_IROUTE_ROOT);
        String routeRootClassName = MessageFormat.format(RouterProcessorConst.ROUTE_ROOT_CLASS_NAME, moduleName);
        logger.i(">>> start generate routeRoot, class name = " + routeRootClassName + " <<<");
        JavaFile.builder(RouterProcessorConst.GENERATE_ROUTE_ROOT_PACKAGE,
                TypeSpec.classBuilder(routeRootClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addSuperinterface(ClassName.get(iRouteRootTypeElement))
                        .addMethod(routeRootMethodSpecBuilder.build())
                        .build()
        ).build().writeTo(filer);
        logger.i(">>> end generate routeRoot, class name = " + routeRootClassName + " <<<");
    }
}
