package com.example.router.util;

import com.example.router.visitor.ScanClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanUtils {
    // 扫描文件查找到实现 IRouteRoot 接口的文件
    // com/example/router/root/loginRouteRoot
    public static final List<String> routeRootClasses = new ArrayList<>();

    // 扫描文件查找到实现 IInterceptorGroup 接口的文件
    // com/example/router/interceptor/loginInterceptorGroup
    public static final List<String> interceptorGroupClasses = new ArrayList<>();

    // 扫描文件查找到 ROUTER_FILE_NAME 文件
    // com/example/router/core/Router.class
    public static File routerFile;

    private ScanUtils() {
        throw new AssertionError();
    }

    public static void scanJar(File srcFile, File destFile) throws IOException {
        JarFile file = new JarFile(srcFile);
        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
//            System.out.println("scanJar, name = " + name);
            if (shouldProcessScanClass(name)) {
                System.out.println("scanJar, shouldProcessScanClass name = " + name);
                InputStream inputStream = file.getInputStream(jarEntry);
                scanClass(inputStream);
                inputStream.close();
            } else if (RouterSettings.ROUTER_MANAGER_CLASS_FILE_NAME.equals(name)) {
                System.out.println("scanJar, found " + RouterSettings.ROUTER_MANAGER_CLASS_FILE_NAME);
                routerFile = destFile;
            }
        }
        file.close();
    }

    public static boolean shouldProcessScanClass(String name) {
        return name != null && (name.startsWith(RouterSettings.GENERATE_ROUTE_ROOT_PACKAGE)
                || name.startsWith(RouterSettings.GENERATE_INTERCEPTOR_PACKAGE));
    }

    public static void scanClass(File classFile, String filename) throws IOException {
        if (classFile.isDirectory()) {
            File[] files = classFile.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    scanClass(file, filename);
                }
            }
        } else {
            String filepath = classFile.getAbsolutePath();
            if (!filepath.endsWith(".class")) return;
            if (filepath.contains("R$")
                    || filepath.contains("R.class")
                    || filepath.contains("BuildConfig.class")) return;

//            System.out.println("scanClass, filepath = " + filepath + ", filename = " + filename);

            // 将文件路径的目录移除 path = com/example/router/TestActivity.class
            String path = filepath.replace(filename + File.separator, "");
            // 如果是 windows 的路径需要做处理
            path = path.replaceAll("\\\\", "/");
//            System.out.println("scanClass, path = " + path);
            if (shouldProcessScanClass(path)) {
                scanClass(new FileInputStream(filepath));
            }
        }
    }

    private static void scanClass(InputStream inputStream) throws IOException {
        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(cr, 0);
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM9, cw, routeRootClasses, interceptorGroupClasses);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        inputStream.close();
    }
}
