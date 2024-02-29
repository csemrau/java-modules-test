package cf.cplace.javamodulestest.service;

import cf.cplace.javamodulestest.util.Utilities;
import jakarta.annotation.Nonnull;

public class Service {

    @Nonnull
    public static String sayHello() {
        return Utilities.sayHello();
    }
}