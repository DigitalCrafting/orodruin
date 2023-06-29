package org.digitalcrafting.anvil.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesReader.class);
    private static final Properties PROPERTIES;

    static {
        PROPERTIES = read();
    }

    public static String get(String propertyName) {
        return (String) PROPERTIES.get(propertyName);
    }

    private static Properties read() {
        return read("application.properties");
    }

    private static Properties read(String fileName) {
        Properties properties = new Properties();
        try {
            InputStream is = PropertiesReader.class.getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                LOGGER.error("Properties file {} not found.", fileName);
            } else {
                properties.load(is);
            }
        } catch (IOException e) {
            LOGGER.error("Could not read properties from {} file.", fileName);
        }
        return properties;
    }
}
