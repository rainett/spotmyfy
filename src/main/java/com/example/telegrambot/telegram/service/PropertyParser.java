package com.example.telegrambot.telegram.service;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class PropertyParser {

    private static final Properties PROPERTIES_FILE = new Properties();
    private static final String PATH = "src/main/resources/application.properties";

    static {
        try {
            PROPERTIES_FILE.load(new FileInputStream(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getList(String propertyPath) {
        refresh();
        List<String> properties = new ArrayList<>();
        String value;
        for (int i = 0; (value = PROPERTIES_FILE.getProperty(String.format("%s.%d", propertyPath, i+1))) != null; i++) {
            properties.add(value);
        }
        return properties;
    }

    public static String getProperty(String propertyPath) {
        refresh();
        return PROPERTIES_FILE.getProperty(propertyPath);
    }

    public static String getRandom(String propertyPath) {
        refresh();
        List<String> list = getList(propertyPath);
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private static void refresh() {
        try {
            PROPERTIES_FILE.load(new FileInputStream(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
