package org.implementation.interfaces;

import java.lang.reflect.Method;

public interface AOPHandlerInterface {
    Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
