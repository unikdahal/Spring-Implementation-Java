package org.implementation;

import org.implementation.annotations.Component;
import org.implementation.interfaces.AInterface;

@Component
public class A implements AInterface {
    public void abc() {
        System.out.println("A");
    }
}

