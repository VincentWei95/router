package com.example.router.plugin;

import com.example.router.util.RouterSettings;
import com.example.router.visitor.HackClassVisitor;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

public class RouterCodeGenerator {

    static void insertInitCodeToJarFile(File routerJarFile,
                                        List<String> routeRootClasses,
                                        List<String> interceptorGroupClasses) throws IOException {
        File optJar = new File(routerJarFile.getParent(), routerJarFile.getName() + ".opt");
        if (optJar.exists()) {
            optJar.delete();
        }
        JarFile jarFile = new JarFile(routerJarFile);
        Enumeration<JarEntry> entries = jarFile.entries();
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(optJar));
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            ZipEntry zipEntry = new ZipEntry(name);
            InputStream inputStream = jarFile.getInputStream(jarEntry);
            jos.putNextEntry(zipEntry);

            if (RouterSettings.ROUTER_MANAGER_CLASS_FILE_NAME.equals(name)) {
                System.out.println("insertInitCodeToJarFile, found " + RouterSettings.ROUTER_MANAGER_CLASS_FILE_NAME);
                byte[] bytes = hack(inputStream, routeRootClasses, interceptorGroupClasses);
                jos.write(bytes);
            } else {
                jos.write(IOUtils.toByteArray(inputStream));
            }

            inputStream.close();
            jos.closeEntry();
        }
        jos.close();
        jarFile.close();
        if (routerJarFile.exists()) {
            routerJarFile.delete();
        }
        optJar.renameTo(routerJarFile);
    }

    private static byte[] hack(InputStream inputStream,
                               List<String> routeRootClasses,
                               List<String> interceptorGroupClasses) throws IOException {
        System.out.println("hack start");
        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        HackClassVisitor cv = new HackClassVisitor(Opcodes.ASM9, cw, routeRootClasses, interceptorGroupClasses);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        System.out.println("hack end");
        return cw.toByteArray();
    }
}
