
package cz.habarta.typescript.generator;

import org.junit.jupiter.api.Test;

import java.io.File;

public class PersonTest {
    @Test
    public void test() {
        new TypeScriptGenerator(TestUtils.settings()).generateTypeScript(Input.from(Person.class),
                Output.to(new File("target/person.d.ts")));
    }

}
