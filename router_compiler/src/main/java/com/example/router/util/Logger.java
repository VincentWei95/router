package com.example.router.util;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class Logger {
    private final Messager mMessager;

    public Logger(Messager messager) {
        this.mMessager = messager;
    }

    public void i(CharSequence info) {
        if (StringUtils.isNotEmpty(info)) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, RouterProcessorConst.LOG_TAG + info);
        }
    }

    public void e(CharSequence error) {
        if (StringUtils.isNotEmpty(error)) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, RouterProcessorConst.LOG_TAG + "An exception is encountered, [" + error + "]");
        }
    }

    public void e(Throwable throwable) {
        if (throwable != null) {
            mMessager.printMessage(Diagnostic.Kind.ERROR, RouterProcessorConst.LOG_TAG + "An exception is encountered, [" + throwable.getMessage() + "]" + "\n" + formatStackTrace(throwable.getStackTrace()));
        }
    }

    public void w(CharSequence warning) {
        if (StringUtils.isNotEmpty(warning)) {
            mMessager.printMessage(Diagnostic.Kind.WARNING, RouterProcessorConst.LOG_TAG + warning);
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
