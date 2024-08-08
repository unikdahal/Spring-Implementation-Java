package org.implementation;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Set;

public class PackageScanner {

    public static Set<Class<?>> getClassesWithAnnotatedFields(String packageName, Class<? extends java.lang.annotation.Annotation> annotation) {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .setScanners(Scanners.TypesAnnotated)
        );

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotation);
//        System.out.println("Found " + annotatedClasses.size() + " classes with annotated fields");
        return annotatedClasses;
    }
}

