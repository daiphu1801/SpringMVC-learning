package com.examp.springmvc.user.infrastructure.excel;

import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import com.examp.springmvc.user.domain.ports.output.UserExcelExporterPort;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ApachePoiUserExcelExporter implements UserExcelExporterPort {

    @Override
    public void export(List<UserDTO> users, OutputStream outputStream, ProgressListener progressListener) {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Users");

            // Viết Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Username", "Họ Tên", "Email", "Số Điện Thoại", "Trạng Thái", "Vai Trò"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int totalRows = users.size();
            for (int i = 0; i < totalRows; i++) {
                UserDTO user = users.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(user.getUsername());
                row.createCell(1).setCellValue(user.getFullName());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(4).setCellValue(user.getStatus());
                row.createCell(5).setCellValue(user.getRole());

                if (progressListener != null && ((i + 1) % 200 == 0 || (i + 1) == totalRows)) {
                    progressListener.onProgress(i + 1);
                    // Giả lập xử lý ngầm mượt mà
                    Thread.sleep(10);
                }
            }

            workbook.write(outputStream);
            workbook.dispose();
        } catch (java.io.IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Lỗi xuất file Excel: " + e.getMessage(), e);
        }
    }
}
