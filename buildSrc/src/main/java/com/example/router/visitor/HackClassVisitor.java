package com.example.router.visitor;

import com.example.router.util.RouterSettings;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class HackClassVisitor extends ClassVisitor {
    private final List<String> routeRootClasses;
    private final List<String> interceptorGroupClasses;

    public HackClassVisitor(int api,
                            ClassVisitor classVisitor,
                            List<String> routeRootClasses,
                            List<String> interceptorGroupClasses) {
        super(api, classVisitor);
        this.routeRootClasses = routeRootClasses;
        this.interceptorGroupClasses = interceptorGroupClasses;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (RouterSettings.HACK_REGISTER_METHOD_NAME.equals(name)) {
            System.out.println("HackClassVisitor, found method " + RouterSettings.HACK_REGISTER_METHOD_NAME);
            mv = new HackRegisterMethodVisitor(Opcodes.ASM9, mv, routeRootClasses, interceptorGroupClasses);
        }
        return mv;
    }
}
