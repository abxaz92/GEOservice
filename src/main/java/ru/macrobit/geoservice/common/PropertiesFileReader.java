package ru.macrobit.geoservice.common;

import java.io.InputStream;
import java.util.Properties;

/**
 * Creator david on 18.03.16.
 */
public class PropertiesFileReader {

    private static final Properties properties;

    /** Use a static initializer to read from file. */
    static {
        properties = new Properties();
        try (InputStream inputStream = PropertiesFileReader.class.getResourceAsStream("/router.propperties")) {
            properties.load(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read properties file", e);
        }
    }

    /**
     * Hide default constructor.
     */
    private PropertiesFileReader() {
    }

    /**
     * Gets the Git SHA-1.
     *
     * @return A {@code String} with the Git SHA-1.
     */
    public static String getOsmFilePath() {
        return properties.getProperty("osm-path");
    }

    public static String getGraphFolder() {
        return properties.getProperty("graph_folder");
    }

}