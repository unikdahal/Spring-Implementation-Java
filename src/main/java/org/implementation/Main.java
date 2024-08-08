package org.implementation;

import org.implementation.interfaces.AInterface;
import org.implementation.interfaces.AspectConfigInterface;
import org.implementation.interfaces.BInterface;

public class Main {
    public static void main(String[] args) throws Exception {
        DependencyContainer injector = new DependencyContainer();
        injector.scanAndInject("org.implementation");
        AInterface ab = (AInterface) injector.getInstance(A.class);
        AspectConfigInterface aspectConfig = (AspectConfigInterface) injector.getInstance(AspectConfig.class);
        ab.abc();
        BInterface bc = (BInterface) injector.getInstance(B.class);
        bc.abc();
    }
}

