
package cz.habarta.typescript.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CovariantPropertiesTest {
    @Test
    public void test() {
        final Settings settings = TestUtils.settings();
        settings.sortDeclarations = true;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(Dog.class));
        final String expected =
                """
                        interface Animal {
                            allFood: Food[];
                            todaysFood: Food;
                        }
                        
                        interface Dog extends Animal {
                            allFood: DogFood[];
                            todaysFood: DogFood;
                        }
                        
                        interface DogFood extends Food {
                        }
                        
                        interface Food {
                        }""";
        Assertions.assertEquals(expected.replace('\'', '"'), output.trim());
    }

    private static abstract class Animal {
        public abstract Food getTodaysFood();

        public abstract List<? extends Food> getAllFood();
    }

    private static abstract class Dog extends Animal {
        @Override
        public abstract DogFood getTodaysFood();

        @Override
        public abstract List<? extends DogFood> getAllFood();
    }

    private static abstract class Food {
    }

    private static abstract class DogFood extends Food {
    }

}
