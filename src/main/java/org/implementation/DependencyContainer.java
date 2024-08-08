package org.implementation;


import org.implementation.annotations.AspectConfiguration;
import org.implementation.annotations.Autowired;
import org.implementation.interfaces.AspectConfigInterface;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.*;

public class DependencyContainer {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, List<Class<?>>> dependencyGraph = new HashMap<>();
    public void scanAndInject(String packageName) throws Exception {

        Set <Class<?>> classes = PackageScanner.getClassesWithAnnotatedFields(packageName, org.implementation.annotations.Component.class);
        for (Class<?> clazz : classes) {
            scanClass(clazz);
        }
        List<Class<?>> sortedClasses = topologicalSort();
        for (Class<?> clazz : sortedClasses) {
            createInstance(clazz,packageName);
        }
    }
    private void scanClass(Class<?> clazz) {
        if (!dependencyGraph.containsKey(clazz)) {
            dependencyGraph.put(clazz, new ArrayList<>());
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Class<?> dependency = field.getType();
                dependencyGraph.get(clazz).add(dependency);
                scanClass(dependency);
            }
        }
    }
    private List<Class<?>> topologicalSort() {
        List<Class<?>> sorted = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>();
        Set<Class<?>> visiting = new HashSet<>();

        for (Class<?> clazz : dependencyGraph.keySet()) {
            if (!visited.contains(clazz)) {
                topologicalSortUtil(clazz, visited, visiting, sorted);
            }
        }

        return sorted;
    }

    private void topologicalSortUtil(Class<?> clazz, Set<Class<?>> visited, Set<Class<?>> visiting, List<Class<?>> sorted) {
        if (visiting.contains(clazz)) {
            throw new RuntimeException("Cyclic dependency detected");
        }
        if (!visited.contains(clazz)) {
            visiting.add(clazz);
            for (Class<?> dependency : dependencyGraph.get(clazz)) {
                topologicalSortUtil(dependency, visited, visiting, sorted);
            }
            visiting.remove(clazz);
            visited.add(clazz);
            sorted.add(clazz);
        }
    }
    private void createInstance(Class<?> clazz,String packageName) throws Exception {
        if (!instances.containsKey(clazz)) {

            Object instance = clazz.getDeclaredConstructor().newInstance();
//            System.out.println(instance.getClass());
//            System.out.println(instance);
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                // Create a proxy instance if the class implements interfaces
//                AspectConfig aspectConfig = new AspectConfig();
                AspectConfigInterface aspectConfig = findAspectConfig(clazz,packageName);
                if(aspectConfig != null) {
                    Object proxyInstance = Proxy.newProxyInstance(
                            clazz.getClassLoader(),
                            interfaces,
                            new AOPHandler(instance, aspectConfig)
                    );
                    instances.put(clazz, proxyInstance);

                    // Also store the proxy instance with the interface type
                    for (Class<?> interfaceOne : interfaces) {
                        instances.put(interfaceOne, proxyInstance);
                    }
                }else{
                    instances.put(clazz, instance);
                }

            } else {
                // If no interfaces, store the actual instance
                instances.put(clazz, instance);
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Class<?> dependency = field.getType();
                    field.set(instance, instances.get(dependency));
                }
            }
        }
    }

    private AspectConfigInterface findAspectConfig(Class<?> clazz, String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<Class<?>> classes = PackageScanner.getClassesWithAnnotatedFields(packageName, org.implementation.annotations.AspectConfiguration.class);
        for (Class<?> configClass : classes) {
            if (configClass.isAnnotationPresent(AspectConfiguration.class)) {
                AspectConfiguration annotation = configClass.getAnnotation(AspectConfiguration.class);
                if (annotation != null && annotation.value().equals(clazz)) {
                        return (AspectConfigInterface) configClass.getDeclaredConstructor().newInstance();
                }
            }
        }
        return null;
    }

    public Object getInstance(Class<?> clazz) {
        return instances.get(clazz);
    }
}






