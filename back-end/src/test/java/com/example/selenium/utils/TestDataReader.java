package com.example.selenium.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class để đọc test data từ CSV files
 */
public class TestDataReader {

    /**
     * Đọc CSV file và trả về list of maps
     */
    public static List<Map<String, String>> readCSV(String filePath) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = null;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    headers = line.split(",");
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(",");
                Map<String, String> row = new HashMap<>();

                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }

                data.add(row);
            }
        }

        return data;
    }

    /**
     * Đọc CSV file và trả về Object[][] cho TestNG DataProvider
     */
    public static Object[][] readCSVAsDataProvider(String filePath) throws IOException {
        List<Map<String, String>> data = readCSV(filePath);
        Object[][] result = new Object[data.size()][1];

        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }

        return result;
    }

    /**
     * Lấy giá trị từ map, trả về empty string nếu null
     */
    public static String getValue(Map<String, String> data, String key) {
        return data.getOrDefault(key, "");
    }

    /**
     * Lấy giá trị từ map, trả về default value nếu null
     */
    public static String getValue(Map<String, String> data, String key, String defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }
}
