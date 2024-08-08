package org.implementation;

import org.implementation.annotations.*;
import org.implementation.interfaces.AspectConfigInterface;

import java.lang.reflect.Method;

@AspectConfiguration(A.class)

@Component
class AspectConfig  implements AspectConfigInterface {
    @Before("abc")
    @Override
    public void beforeMethod() {
        System.out.println("Before method execution");
    }

    @After("abc")
    @Override
    public void afterMethod() {
        System.out.println("After method execution");
    }

    @Around("abc")
    @Override
    public void aroundMethod(){
        System.out.println("Around method execution");

    }
}