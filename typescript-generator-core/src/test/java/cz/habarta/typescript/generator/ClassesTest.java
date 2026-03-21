
package cz.habarta.typescript.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ClassesTest {
    @Test
    public void testInvalidSettings() {
        final Settings settings = TestUtils.settings();
        settings.mapClasses = ClassMapping.asClasses;
        Assertions.assertThrows(Exception.class, () -> new TypeScriptGenerator(settings).generateTypeScript(Input.from()));
    }

    @Test
    public void testClass() {
        testOutput(A.class,
                   """
                           class A {
                               a: string;
                           }"""
        );
    }

    @Test
    public void testInheritedClass() {
        // A and B order is important
        testOutput(B.class,
                   """
                           class A {
                               a: string;
                           }
                           
                           class B extends A {
                               b: string;
                           }"""
        );
    }

    @Test
    public void testClassImplementsInterface() {
        testOutput(E.class,
                   """
                           class E implements D {
                               c: string;
                               d: string;
                               e: string;
                           }
                           
                           interface D extends C {
                               d: string;
                           }
                           
                           interface C {
                               c: string;
                           }"""
        );
    }

    @Test
    public void testComplexHierarchy() {
        // Q3 and Q5 order is important
        testOutput(Q5.class,
                   """
                           class Q3 implements Q2 {
                               q1: string;
                               q2: string;
                               q3: string;
                           }
                           
                           class Q5 extends Q3 implements Q2, Q4 {
                               q4: string;
                               q5: string;
                           }
                           
                           interface Q2 extends Q1 {
                               q2: string;
                           }
                           
                           interface Q4 {
                               q4: string;
                           }
                           
                           interface Q1 {
                               q1: string;
                           }"""
        );
    }

    private static void testOutput(Class<?> inputClass, String expected) {
        final Settings settings = TestUtils.settings();
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(inputClass));
        Assertions.assertEquals(expected.replace('\'', '"'), output.trim());
    }

    private static abstract class A {
        public abstract String getA();
    }

    private static abstract class B extends A {
        public abstract String getB();
    }

    private interface C {
        String getC();
    }

    private interface D extends C {
        String getD();
    }

    private static abstract class E implements D {
        public abstract String getE();
    }

    private interface Q1 {
        String getQ1();
    }

    private interface Q2 extends Q1 {
        String getQ2();
    }

    private static abstract class Q3 implements Q2 {
        public abstract String getQ3();
    }

    private interface Q4 {
        String getQ4();
    }

    private static abstract class Q5 extends Q3 implements Q2, Q4 {
        public abstract String getQ5();
    }

    @Test
    public void testClassPatterns1() {
        testClassPatterns(
                Arrays.asList(
                        "**Bc",
                        "**Bi",
                        "**Derived1",
                        "**Derived2"
                ),
                """
                        \
                        class Bc {
                            x: string;
                        }
                        
                        interface Bi {
                            y: string;
                        }
                        
                        class Derived1 extends Bc implements Bi {
                            y: string;
                        }
                        
                        class Derived2 extends Derived1 {
                        }"""
        );
    }

    @Test
    public void testClassPatterns2() {
        testClassPatterns(
                Arrays.asList(
                        "**Derived1",
                        "**Derived2"
                ),
                """
                        \
                        interface Bc {
                            x: string;
                        }
                        
                        interface Bi {
                            y: string;
                        }
                        
                        class Derived1 implements Bc, Bi {
                            x: string;
                            y: string;
                        }
                        
                        class Derived2 extends Derived1 {
                        }"""
        );
    }

    @Test
    public void testClassPatterns3() {
        testClassPatterns(
                Arrays.asList(
                        "**Bc",
                        "**Derived2"
                ),
                """
                        \
                        class Bc {
                            x: string;
                        }
                        
                        interface Bi {
                            y: string;
                        }
                        
                        interface Derived1 extends Bc, Bi {
                        }
                        
                        class Derived2 implements Derived1 {
                            x: string;
                            y: string;
                        }"""
        );
    }

    @Test
    public void testClassPatterns4() {
        testClassPatterns(
                Arrays.asList(
                        "**Bc",
                        "**Derived1"
                ),
                """
                        \
                        class Bc {
                            x: string;
                        }
                        
                        interface Bi {
                            y: string;
                        }
                        
                        class Derived1 extends Bc implements Bi {
                            y: string;
                        }
                        
                        interface Derived2 extends Derived1 {
                        }"""
        );
    }

    private static void testClassPatterns(List<String> mapClassesAsClassesPatterns, String expected) {
        final Settings settings = TestUtils.settings();
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.mapClassesAsClassesPatterns = mapClassesAsClassesPatterns;
        final String output = new TypeScriptGenerator(settings)
                .generateTypeScript(Input.from(Bc.class, Bi.class, Derived1.class, Derived2.class));
        Assertions.assertEquals(expected.replace('\'', '"').trim(), output.trim());
    }

    private static abstract class Bc {
        public abstract String getX();
    }

    private interface Bi {
        String getY();
    }

    private static abstract class Derived1 extends Bc implements Bi {
    }

    private static abstract class Derived2 extends Derived1 {
    }

    @Test
    public void testConstructor() {
        final Settings settings = TestUtils.settings();
        settings.optionalAnnotations = List.of(Nullable.class);
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.generateConstructors = true;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(FooBar.class));
        Assertions.assertTrue(output.contains("constructor(data: FooBar)"));
    }

    @Test
    public void testSortedConstructor() {
        final Settings settings = TestUtils.settings();
        settings.optionalAnnotations = List.of(Nullable.class);
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.generateConstructors = true;
        settings.sortDeclarations = true;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(FooBar.class));
        String sortedPropertyAssignments = "" +
                "        this.bar = data.bar;" + settings.newline +
                "        this.foo = data.foo;";
        Assertions.assertTrue(output.contains(sortedPropertyAssignments));
    }

    @Test
    public void testUnsortedConstructor() {
        final Settings settings = TestUtils.settings();
        settings.optionalAnnotations = List.of(Nullable.class);
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.generateConstructors = true;
        settings.sortDeclarations = false;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(FooBar.class));
        String unsortedPropertyAssignments = "" +
                "        this.foo = data.foo;" + settings.newline +
                "        this.bar = data.bar;";
        Assertions.assertTrue(output.contains(unsortedPropertyAssignments));
    }

    private static class FooBar {
        @Nullable
        public String foo;
        public int bar;
    }

    @Test
    public void testConstructorOnInterface() {
        final Settings settings = TestUtils.settings();
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.generateConstructors = true;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(FooBarInterface.class));
        Assertions.assertFalse(output.contains("constructor"));
    }

    private interface FooBarInterface {
    }

    @Test
    public void testConstructorWithGenericsAndInheritance() {
        final Settings settings = TestUtils.settings();
        settings.outputFileType = TypeScriptFileType.implementationFile;
        settings.mapClasses = ClassMapping.asClasses;
        settings.generateConstructors = true;
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(ClassB.class));
        Assertions.assertTrue(output.contains("constructor(data: ClassA<T>)"));
        Assertions.assertTrue(output.contains("constructor(data: ClassB)"));
        Assertions.assertTrue(output.contains("super(data);"));
    }

    private static class ClassA<T> {
        public String a;
    }

    private static class ClassB extends ClassA<String> {
        public String b;
    }

}
