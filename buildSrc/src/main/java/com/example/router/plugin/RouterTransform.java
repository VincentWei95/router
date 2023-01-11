package com.example.router.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.example.router.util.RouterSettings;
import com.example.router.util.ScanUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class RouterTransform extends Transform {
    // /build/intermediate/transforms/ 下的目录名
    @Override
    public String getName() {
        return RouterSettings.PLUGIN_NAME;
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        // ASM 注入前都将缓存清除，避免重复注入
        ScanUtils.routeRootClasses.clear();
        ScanUtils.interceptorGroupClasses.clear();
        ScanUtils.routerFile = null;
        if (!isIncremental()) {
            outputProvider.deleteAll();
        }

        for (TransformInput input : inputs) {
            // 拷贝 Jar 包，包含 framework 的类
            for (JarInput jarInput : input.getJarInputs()) {
                File contentLocation = outputProvider.getContentLocation(
                        jarInput.getName(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
//                System.out.println("jarInput, name = " + jarInput.getName() + ", path = " + jarInput.getFile().getAbsolutePath() + ", contentLocation = " + contentLocation);

                ScanUtils.scanJar(jarInput.getFile(), contentLocation);

                FileUtils.copyFile(jarInput.getFile(), contentLocation);
            }

            // 拷贝 class 文件
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                File contentLocation = outputProvider.getContentLocation(
                        directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
//                System.out.println("directoryInput, name = " + directoryInput.getName() + ", path = " + directoryInput.getFile().getAbsolutePath() + ", contentLocation = " + contentLocation);

                ScanUtils.scanClass(directoryInput.getFile(), directoryInput.getFile().getAbsolutePath());

                FileUtils.copyDirectory(directoryInput.getFile(), contentLocation);
            }
        }

        if (ScanUtils.routerFile == null) {
            throw new IllegalArgumentException("not found " + RouterSettings.ROUTER_MANAGER_CLASS_FILE_NAME);
        }
        if (!ScanUtils.routerFile.getName().endsWith(".jar")) {
            throw new IllegalArgumentException("routerFile is not a jar file");
        }

        System.out.println("routeRootClasses = " + ScanUtils.routeRootClasses);
        System.out.println("interceptorGroupClasses = " + ScanUtils.interceptorGroupClasses);

        RouterCodeGenerator.insertInitCodeToJarFile(
                ScanUtils.routerFile, ScanUtils.routeRootClasses, ScanUtils.interceptorGroupClasses);
    }
}
