package com.examp.springmvc.shared.infrastructure.config;

import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

public class ExcelGeneratorTest {

    @org.junit.jupiter.api.Disabled
    @Test
    public void generateTestData() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Users");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Username");
            header.createCell(1).setCellValue("Họ Tên");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Số Điện Thoại");
            header.createCell(4).setCellValue("Mật Khẩu");
            header.createCell(5).setCellValue("Vai Trò");
            header.createCell(6).setCellValue("Trạng Thái");

            // Tạo 10,000 users
            for (int i = 1; i <= 10000; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue("testuser_" + i);
                row.createCell(1).setCellValue("Test User " + i);
                row.createCell(2).setCellValue("user_" + i + "@example.com");
                row.createCell(3).setCellValue("090000" + String.format("%04d", i));
                row.createCell(4).setCellValue("Password@" + i);
                row.createCell(5).setCellValue(i % 50 == 0 ? "ADMIN" : "USER");
                row.createCell(6).setCellValue("ACTIVE");
            }

            try (FileOutputStream fos = new FileOutputStream("target/users_10000.xlsx")) {
                workbook.write(fos);
            }
            System.out.println("Generated target/users_10000.xlsx successfully!");
        }
    }
}
