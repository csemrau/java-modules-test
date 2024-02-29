package cf.cplace.javamodulestest.util;

import com.google.gson.Gson;
import jakarta.annotation.Nonnull;

import java.util.Map;

public class Utilities {
    @Nonnull
    public static String sayHello() {
        return "Hello World";
    }

    @Nonnull
    public static String goodBye() {
        return new Gson().toJson(Map.of("good", "bye"));
    }
}
