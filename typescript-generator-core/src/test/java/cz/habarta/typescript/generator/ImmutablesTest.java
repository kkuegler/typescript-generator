
package cz.habarta.typescript.generator;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImmutablesTest {

    @Test
    public void testImmutables() {
        final Settings settings = TestUtils.settings();
        final String output = new TypeScriptGenerator(settings).generateTypeScript(Input.from(Shape.class));
        final String expected = (
                """
                        interface Shape {
                            kind: 'square' | 'rectangle' | 'circle';
                        }
                        
                        interface Square extends Shape {
                            kind: 'square';
                            size: number;
                        }
                        
                        interface Rectangle extends Shape {
                            kind: 'rectangle';
                            width: number;
                            height: number;
                        }
                        
                        interface Circle extends Shape {
                            kind: 'circle';
                            radius: number;
                        }
                        
                        type ShapeUnion = Square | Rectangle | Circle;
                """
                ).replace('\'', '"');
        Assertions.assertEquals(expected, output);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Square.class, name = "square"),
            @JsonSubTypes.Type(value = Rectangle.class, name = "rectangle"),
            @JsonSubTypes.Type(value = Circle.class, name = "circle"),
    })
    public interface Shape {
    }

    public static class Square implements Shape {
        public double size;
    }

    @Value.Immutable
    @JsonSerialize(as = ImmutableRectangle.class)
    @JsonPropertyOrder({ "width", "height" })
    @JsonDeserialize(as = ImmutableRectangle.class)
    public static abstract class Rectangle implements Shape {
        public abstract double width();
        public abstract double height();

        public static Rectangle.Builder builder() {
            return new Rectangle.Builder();
        }

        public static final class Builder extends ImmutableRectangle.Builder {
        }
    }

    @Value.Immutable
    @JsonSerialize(as = ImmutableCircle.class)
    @JsonDeserialize(as = ImmutableCircle.class)
    public interface Circle extends Shape {
        double radius();

        final class Builder extends ImmutableCircle.Builder {
        }
    }

}
