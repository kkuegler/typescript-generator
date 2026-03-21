package cz.habarta.typescript.generator;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomTypeConversionTest {
    @Test
    public void testCustomTypeConversion() {
        final Settings settings = TestUtils.settings();
        // suppose we want to override how A is parsed
        settings.customTypeProcessor = new TypeProcessor() {
            @Override
            public TypeProcessor.Result processType(Type javaType, TypeProcessor.Context context) {
                if (javaType.equals(B.class)) {
                    return new Result(TsType.Number.optional());
                }
                return null;
            }
        };

        assertEquals("A", TestUtils.compileType(settings, A.class).toString());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new TypeScriptGenerator(settings).generateTypeScript(Input.from(A.class), Output.to(out));
        assertTrue(new String(out.toByteArray()).trim().contains("x?: number;"));
    }

    public static class A {
        public B getX() {
            return null;
        }
    }

    public static class B {
        public B getX() {
            return null;
        }
    }

    @Test
    public void testCustomOptional() throws Exception {
        final Settings settings = TestUtils.settings();
        settings.mapDate = DateMapping.asString;
        settings.customTypeProcessor = new TypeProcessor() {
            @Override
            public TypeProcessor.Result processType(Type javaType, TypeProcessor.Context context) {
                final Type[] typeArguments = tryGetParameterizedTypeArguments(javaType, CustomOptional.class);
                if (typeArguments != null) {
                    final TypeProcessor.Result result = context.processType(typeArguments[0]);
                    return new Result(result.getTsType().optional(), result.getDiscoveredClasses());
                }
                return null;
            }
        };
        final TypeProcessor typeProcessor = new TypeScriptGenerator(settings).getCommonTypeProcessor();
        final TypeProcessor.Context context = DefaultTypeProcessorTest.getTestContext(typeProcessor);
        {
            final Type maybeObjectFieldType = CustomOptionalUsage.class.getField("maybeObject").getGenericType();
            final TypeProcessor.Result result = typeProcessor.processType(maybeObjectFieldType, context);
            assertEquals(List.of(SomeObject.class), result.getDiscoveredClasses());
        }
        {
            final String dts = new TypeScriptGenerator(settings)
                    .generateTypeScript(Input.from(CustomOptionalUsage.class));
            assertTrue(dts.contains("maybeObject?: SomeObject;"));
            assertTrue(dts.contains("maybeDate?: DateAsString;"));
        }
    }

    private static Type[] tryGetParameterizedTypeArguments(Type javaType, Class<?> requiredRawType) {
        if (javaType instanceof ParameterizedType parameterizedType) {
            if (parameterizedType.getRawType() instanceof Class<?> javaClass) {
                if (requiredRawType.isAssignableFrom(javaClass)) {
                    return parameterizedType.getActualTypeArguments();
                }
            }
        }
        return null;
    }

    public static class CustomOptionalUsage {
        public CustomOptional<SomeObject> maybeObject;
        public CustomOptional<Date> maybeDate;
    }

    public static class SomeObject {
    }

    public static class CustomOptional<T> {
    }

}
