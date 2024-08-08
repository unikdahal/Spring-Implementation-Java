package org.implementation;

import org.implementation.annotations.Component;
import org.implementation.interfaces.BInterface;

@Component
public class B implements BInterface {

    @Override
    public void abc() {
        System.out.println("B");
    }
}
