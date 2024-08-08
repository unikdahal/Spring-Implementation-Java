package org.implementation;

import org.implementation.annotations.*;
import org.implementation.interfaces.AOPHandlerInterface;
import org.implementation.interfaces.AspectConfigInterface;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Component
public class AOPHandler implements InvocationHandler, AOPHandlerInterface {
    private Object target;
    private AspectConfigInterface aspectConfig;

    public  AOPHandler(){}
    public AOPHandler(Object target, AspectConfigInterface aspectConfig) {
        this.target = target;
        this.aspectConfig = aspectConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method[] aspectMethods = aspectConfig.getClass().getMethods();

        for (Method aspectMethod : aspectMethods) {
            if (aspectMethod.isAnnotationPresent(Before.class)) {
                Before before = aspectMethod.getAnnotation(Before.class);
                if (before.value().equals(method.getName())) {
                    aspectMethod.invoke(aspectConfig);
                }
            }
        }

//        Object result = null;
        for (Method aspectMethod : aspectMethods) {
            if (aspectMethod.isAnnotationPresent(Around.class)) {
                Around around = aspectMethod.getAnnotation(Around.class);
                if (around.value().equals(method.getName())) {
                    aspectMethod.invoke(aspectConfig);
                    method.invoke(target, args);
                    aspectMethod.invoke(aspectConfig);
                    break;
                }
            }
        }

        for (Method aspectMethod : aspectMethods) {
            if (aspectMethod.isAnnotationPresent(After.class)) {
                After after = aspectMethod.getAnnotation(After.class);
                if (after.value().equals(method.getName())) {
                    aspectMethod.invoke(aspectConfig);
                }
            }
        }

        return proxy;
    }
}