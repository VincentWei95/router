package com.example.router.visitor;

import com.example.router.util.RouterSettings;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class HackRegisterMethodVisitor extends MethodVisitor {
    private final List<String> routeRootClasses;
    private final List<String> interceptorGroupClasses;

    protected HackRegisterMethodVisitor(int api,
                                        MethodVisitor methodVisitor,
                                        List<String> routeRootClasses,
                                        List<String> interceptorGroupClasses) {
        super(api, methodVisitor);
        this.routeRootClasses = routeRootClasses;
        this.interceptorGroupClasses = interceptorGroupClasses;
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
            System.out.println("HackMethodVisitor, visitInsn start");
            /**
             * private static final Map<String, Class<? extends IRouteRoot>> routeGroupClassMap = new HashMap<>();
             *
             * private static void register() {
             *  registerRouteRoot(new loginRouteRoot());
             *  registerRouteRoot(new memberRouteRoot());
             *  registerInterceptorGroup(new loginInterceptorGroup);
             * }
             *
             * private static registerRouteRoot(IRouteRoot routeRoot) {
             *  routeRoot.loadInto(routeGroupClassMap);
             * }
             *
             * private static registerInterceptor(IInterceptorGroup interceptorGroup) {
             *  interceptorGroup.loadInto(interceptorMap);
             * }
             */
            for (String routeRootClass : routeRootClasses) {
                insertRegisterRouteRootBytecodes(routeRootClass);
            }

            for (String interceptorGroupClass : interceptorGroupClasses) {
                insertRegisterInterceptorGroupBytecodes(interceptorGroupClass);
            }
            System.out.println("HackMethodVisitor, visitInsn end");
        }
        super.visitInsn(opcode);
    }

    private void insertRegisterRouteRootBytecodes(String routeRootClass) {
        visitTypeInsn(Opcodes.NEW, routeRootClass);
        visitInsn(Opcodes.DUP);
        visitMethodInsn(Opcodes.INVOKESPECIAL, routeRootClass, "<init>", "()V", false);
        visitMethodInsn(
                Opcodes.INVOKESTATIC,
                RouterSettings.ROUTER_MANAGER_FILE_NAME,
                RouterSettings.HACK_REGISTER_ROUTE_ROOT_METHOD_NAME,
                RouterSettings.IROUTE_ROOT_INTERFACE_DESCRIPTOR,
                false);
    }

    private void insertRegisterInterceptorGroupBytecodes(String interceptorGroupClass) {
        visitTypeInsn(Opcodes.NEW, interceptorGroupClass);
        visitInsn(Opcodes.DUP);
        visitMethodInsn(Opcodes.INVOKESPECIAL, interceptorGroupClass, "<init>", "()V", false);
        visitMethodInsn(
                Opcodes.INVOKESTATIC,
                RouterSettings.ROUTER_MANAGER_FILE_NAME,
                RouterSettings.HACK_REGISTER_INTERCEPTOR_METHOD_NAME,
                RouterSettings.IINTERCEPTOR_INTERFACE_DESCRIPTOR,
                false);
    }
}
