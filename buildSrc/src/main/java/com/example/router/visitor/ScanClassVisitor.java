package com.example.router.visitor;

import com.example.router.util.RouterSettings;

import org.objectweb.asm.ClassVisitor;

import java.util.List;

public class ScanClassVisitor extends ClassVisitor {
    private final List<String> routeRootClasses;
    private final List<String> interceptorGroupClasses;

    public ScanClassVisitor(int api,
                            ClassVisitor classVisitor,
                            List<String> routeRouteClasses,
                            List<String> interceptorGroupClasses) {
        super(api, classVisitor);
        this.routeRootClasses = routeRouteClasses;
        this.interceptorGroupClasses = interceptorGroupClasses;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        for (String interfaceName : interfaces) {
            System.out.println("ScanClassVisitor, interfaceName = " + interfaceName);
            if (RouterSettings.IROUTE_ROOT_INTERFACE_NAME.equals(interfaceName)
                    && !routeRootClasses.contains(interfaceName)) {
                System.out.println("ScanClassVisitor, find routeRootClassName = " + name);
                routeRootClasses.add(name);
            } else if (RouterSettings.IINTERCEPTOR_GROUP_INTERFACE_NAME.equals(interfaceName)
                    && !interceptorGroupClasses.contains(interfaceName)) {
                System.out.println("ScanClassVisitor, find interceptorGroupClasses = " + name);
                interceptorGroupClasses.add(name);
            }
        }
    }
}
