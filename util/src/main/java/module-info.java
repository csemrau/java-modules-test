module cf.cplace.javamodulestest.util.main {
    // implementation
    requires com.google.gson;
    // api
    requires transitive jakarta.annotation;

    exports cf.cplace.javamodulestest.util;
}