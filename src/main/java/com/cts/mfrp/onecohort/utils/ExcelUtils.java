package com.cts.mfrp.onecohort.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private ExcelUtils() {}

    /**
     * Reads an xlsx sheet and returns each data row as a map of header -> cell value.
     * The first row is treated as the header row.
     */
    public static List<Map<String, String>> readSheet(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName);

            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getLastCellNum();

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int c = 0; c < colCount; c++) {
                    String header = getCellValue(headerRow.getCell(c));
                    String value  = getCellValue(row.getCell(c));
                    rowMap.put(header, value);
                }
                data.add(rowMap);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel file: " + filePath + " — " + e.getMessage());
        }
        return data;
    }

    /**
     * Returns a 2D Object array suitable for TestNG @DataProvider.
     * Columns: each map in the list becomes one row; columns are map values in insertion order.
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        List<Map<String, String>> rows = readSheet(filePath, sheetName);
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i);
        }
        return data;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }
}
