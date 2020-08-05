package com.mactavish.ephemeral.annotation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public enum Method {
    GET, POST, PUT, DELETE;

    public static boolean isHttpRequestAnnotation(Class<? extends Annotation> annotation) {
        return RequestMapping.class.equals(annotation) || PostMapping.class.equals(annotation) || GetMapping.class.equals(annotation) || PutMapping.class.equals(annotation) || DeleteMapping.class.equals(annotation);
    }
}
