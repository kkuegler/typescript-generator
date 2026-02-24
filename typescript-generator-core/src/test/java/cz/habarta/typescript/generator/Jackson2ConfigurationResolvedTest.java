package cz.habarta.typescript.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Jackson2ConfigurationResolvedTest {
    @Test
    public void test() {
        final Jackson2Configuration configuration = new Jackson2Configuration();
        configuration.serializerTypeMappings = List
                .of(Jackson2ParserTest.IdSerializer.class.getName() + ":" + "string");
        final Jackson2ConfigurationResolved resolved = Jackson2ConfigurationResolved.from(configuration,
                Thread.currentThread().getContextClassLoader());
        Assertions.assertEquals("string", resolved.serializerTypeMappings.get(Jackson2ParserTest.IdSerializer.class));
    }

}
