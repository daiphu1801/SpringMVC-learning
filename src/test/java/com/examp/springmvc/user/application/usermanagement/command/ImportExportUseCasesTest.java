package com.examp.springmvc.user.application.usermanagement.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ExcelTaskStatus;
import com.examp.springmvc.shared.domain.task.ExcelTaskType;
import com.examp.springmvc.user.application.usermanagement.query.ExportUsersCommand;
import com.examp.springmvc.user.application.usermanagement.query.ExportUsersUseCase;
import com.examp.springmvc.user.application.usermanagement.query.UserDTO;
import com.examp.springmvc.user.application.usermanagement.query.UserQueryPort;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Executor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class ImportExportUseCasesTest {

    @Mock
    private ExcelTaskRepository excelTaskRepository;

    @Mock
    private UserPersistencePort userPersistencePort;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private PlatformTransactionManager transactionManager;

    private ImportUsersUseCase importUsersUseCase;
    private ExportUsersUseCase exportUsersUseCase;

    private final Executor sameThreadExecutor = Runnable::run;

    @BeforeEach
    void setUp() {
        importUsersUseCase = new ImportUsersUseCase(
                excelTaskRepository, userPersistencePort, passwordHasher, sameThreadExecutor, transactionManager);

        exportUsersUseCase = new ExportUsersUseCase(excelTaskRepository, userQueryPort);
    }

    @Test
    @DisplayName("Should parse and import users successfully")
    void shouldImportUsersSuccessfully() throws Exception {
        String taskId = "test-import-task";
        ExcelTask mockTask = new ExcelTask(
                taskId, ExcelTaskType.IMPORT, ExcelTaskStatus.PENDING, 0, 0, 0, 0, null, null, null, null);

        when(excelTaskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
        when(userPersistencePort.findByUsername(any())).thenReturn(Optional.empty());
        when(passwordHasher.hash(any())).thenReturn("hashed_pass");

        TransactionStatus mockTxStatus = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any())).thenReturn(mockTxStatus);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Username");
            header.createCell(1).setCellValue("Họ Tên");
            header.createCell(2).setCellValue("Email");
            header.createCell(3).setCellValue("Số Điện Thoại");
            header.createCell(4).setCellValue("Mật Khẩu");
            header.createCell(5).setCellValue("Vai Trò");
            header.createCell(6).setCellValue("Trạng Thái");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("imported_user");
            row.createCell(1).setCellValue("Imported Name");
            row.createCell(2).setCellValue("imported@example.com");
            row.createCell(3).setCellValue("0901234567");
            row.createCell(4).setCellValue("RawPassword123!");
            row.createCell(5).setCellValue("USER");
            row.createCell(6).setCellValue("ACTIVE");

            workbook.write(bos);
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ImportUsersCommand command = new ImportUsersCommand(taskId, bis);

        importUsersUseCase.execute(command);

        verify(userPersistencePort).save(any());
        verify(excelTaskRepository, atLeastOnce()).save(any(ExcelTask.class));
        assertEquals(1, mockTask.getSuccessRows());
        assertEquals(0, mockTask.getFailedRows());
        assertEquals(ExcelTaskStatus.COMPLETED, mockTask.getStatus());
    }

    @Test
    @DisplayName("Should export users successfully")
    void shouldExportUsersSuccessfully() {
        String taskId = "test-export-task";
        ExcelTask mockTask = new ExcelTask(
                taskId, ExcelTaskType.EXPORT, ExcelTaskStatus.PENDING, 0, 0, 0, 0, null, null, null, null);

        when(excelTaskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));

        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setUsername("export_user");
        user.setFullName("Export Name");
        user.setEmail("export@example.com");
        user.setPhone("0987654321");
        user.setStatus("ACTIVE");
        user.setRole("USER");

        when(userQueryPort.findAll()).thenReturn(Collections.singletonList(user));

        ExportUsersCommand command = new ExportUsersCommand(taskId);
        exportUsersUseCase.execute(command);

        verify(excelTaskRepository, atLeastOnce()).save(any(ExcelTask.class));
        assertEquals(1, mockTask.getSuccessRows());
        assertEquals(ExcelTaskStatus.COMPLETED, mockTask.getStatus());
    }
}
