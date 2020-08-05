package com.mactavish.ephemeral;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

public class Bootstrap {
    private static final Log log = LogFactory.getLog(Bootstrap.class);
    private static final String routerAnnotation = "com.mactavish.ephemeral.annotation.Router";

    public void run(Class<?> bootClass) {

    }

    Set<Class<?>> findRouterClasses(String packageName) {
        Set<Class<?>> routerClasses=new HashSet<>();
        try (ScanResult scanResult = new ClassGraph()
                // .verbose()                // Log to stderr
                .enableClassInfo()           // Scan classes, methods, fields, annotations
                .acceptPackages(packageName) // Scan com.xyz and subpackages (omit to scan all packages)
                .scan()) {                   // Start the scan

            for (var routeClassInfo : scanResult.getClassesWithAnnotation(routerAnnotation)) {
                log.info("Router class found: "+routeClassInfo.getName());
                routerClasses.add(routeClassInfo.loadClass());
            }
        }
        return routerClasses;
    }
}
