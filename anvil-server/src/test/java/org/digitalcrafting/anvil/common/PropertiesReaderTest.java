package org.digitalcrafting.anvil.common;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesReaderTest {
    @Test
    public void should_readApplicationProperties() {
        Properties properties = PropertiesReader.read();

        assertNotNull(properties);
        assertEquals("hello properties", properties.getProperty("test.property"));
    }
}