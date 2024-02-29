package cf.cplace.javamodulestest.util;

import com.google.gson.Gson;

import java.util.Map;

public class Utilities {
    public static String sayHello() {
        return "Hello World";
    }

    public static String goodBye() {
        return new Gson().toJson(Map.of("good", "bye"));
    }
}
