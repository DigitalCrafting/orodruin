package org.digitalcrafting.anvil.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class PropertiesReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesReader.class);

    public static Properties read() {
        return read("application.properties");
    }

    public static Properties read(String fileName) {
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
