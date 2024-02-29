package cf.cplace.javamodulestest.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class UtilitiesTest {

    @Test
    public void sayHello() {
        assertThat(Utilities.sayHello(), is("Hello World"));
    }    @Test
    public void goodBye() {
        assertThat(Utilities.goodBye(), containsString("good"));
    }
}
