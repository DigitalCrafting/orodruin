package org.digitalcrafting.anvil.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesReaderTest {
    @Test
    public void should_readApplicationProperties() {
        assertEquals("hello properties", PropertiesReader.get("test.property"));
    }
}