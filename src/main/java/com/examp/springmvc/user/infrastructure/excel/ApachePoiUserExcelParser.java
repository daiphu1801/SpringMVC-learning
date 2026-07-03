package com.examp.springmvc.user.infrastructure.excel;

import com.examp.springmvc.user.application.usermanagement.command.UserImportRow;
import com.examp.springmvc.user.domain.model.UserRole;
import com.examp.springmvc.user.domain.model.UserStatus;
import com.examp.springmvc.user.domain.ports.output.UserExcelParserPort;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class ApachePoiUserExcelParser implements UserExcelParserPort {

    @Override
    public List<UserImportRow> parse(InputStream inputStream) {
        List<UserImportRow> rows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            for (int r = 1; r <= totalRows; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }
                rows.add(parseRow(row, r));
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi phân tích file Excel: " + e.getMessage(), e);
        }
        return rows;
    }

    private UserImportRow parseRow(Row row, int rowNum) {
        String username = getCellValueAsString(row.getCell(0));
        String fullName = getCellValueAsString(row.getCell(1));
        String email = getCellValueAsString(row.getCell(2));
        String phone = getCellValueAsString(row.getCell(3));
        String password = getCellValueAsString(row.getCell(4));
        String role = getCellValueAsString(row.getCell(5));
        String status = getCellValueAsString(row.getCell(6));

        UserImportRow importRow = new UserImportRow(rowNum, username, fullName, email, phone, password, role, status);

        if (username.isEmpty()) {
            importRow.addError("Username không được để trống");
        }
        if (fullName.isEmpty()) {
            importRow.addError("Họ tên không được để trống");
        }
        if (email.isEmpty()) {
            importRow.addError("Email không được để trống");
        } else if (!email.contains("@")) {
            importRow.addError("Định dạng email không hợp lệ");
        }
        if (password.isEmpty()) {
            importRow.addError("Mật khẩu không được để trống");
        }

        if (role.isEmpty()) {
            importRow.setRole("USER");
        } else {
            try {
                UserRole.valueOf(role.toUpperCase());
                importRow.setRole(role.toUpperCase());
            } catch (Exception e) {
                importRow.addError("Vai trò không hợp lệ (Chỉ chấp nhận: USER, ADMIN)");
            }
        }

        if (status.isEmpty()) {
            importRow.setStatus("ACTIVE");
        } else {
            try {
                UserStatus.valueOf(status.toUpperCase());
                importRow.setStatus(status.toUpperCase());
            } catch (Exception e) {
                importRow.addError("Trạng thái không hợp lệ (Chỉ chấp nhận: ACTIVE, INACTIVE)");
            }
        }

        return importRow;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
