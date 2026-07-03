# Báo cáo kết quả Refactor tính năng Excel (Clean Architecture, DDD, Crash Recovery & Active Threads Tracker)

Tài liệu này báo cáo chi tiết quá trình và kết quả tái cấu trúc (refactor) tính năng Nhập/Xuất người dùng bằng Excel nhằm tách biệt mối quan tâm (Separation of Concerns), tuân thủ Clean Architecture (Ports & Adapters), nguyên lý DDD và bổ sung cơ chế chịu lỗi nâng cao & giám sát chi tiết hoạt động của các luồng (Active Threads Tracker).

---

## 1. Bối cảnh & Lý do Refactor

Trước khi refactor, tính năng Excel vi phạm các nguyên tắc thiết kế quan trọng:
* **Sự phụ thuộc chặt chẽ (Tight Coupling)**: Tầng Application (Use Cases) nhập trực tiếp thư viện Apache POI (`org.apache.poi.*`) để thao tác với Cell, Row, Sheet.
* **Controller vi phạm Clean Architecture**: `UserExcelController` tiêm trực tiếp Spring `ThreadPoolTaskExecutor` (framework hạ tầng) để đọc chỉ số đo lường hiệu năng của thread pool và truy cập trực tiếp `ExcelTaskRepository` để lấy danh sách task gần đây.
* **Thiếu khả năng giám sát chi tiết**: Người dùng không biết chính xác từng luồng (ví dụ: `ExcelReader-1` hay `ExcelDBWriter-3`) đang thực thi công việc gì, xử lý dòng bao nhiêu, gây khó khăn trong việc chuẩn đoán nghẽn cổ chai.

---

## 2. Mô hình Kiến trúc Mới (Ports & Adapters & CQRS)

Chúng ta tách biệt hoàn toàn logic nghiệp vụ Excel và các thư viện hạ tầng bằng các cổng giao tiếp (Ports):

```mermaid
graph TD
    subgraph Tầng Presentation [Presentation Layer]
        Controller[UserExcelController]
    end

    subgraph Tầng Application/Domain [Core Layer]
        UC_Dashboard[GetExcelDashboardUseCase] -->|gọi| Port_Metrics[ExcelThreadPoolMetricsPort]
        UC_Import[ImportUsersUseCase] -->|gọi| Port_Parser[UserExcelParserPort]
        UC_Export[ExportUsersUseCase] -->|gọi| Port_Exporter[UserExcelExporterPort]
        
        Tracker[ExcelThreadTracker]
    end

    subgraph Tầng Infrastructure [Infrastructure Layer]
        Adapt_Metrics[SpringExcelThreadPoolMetricsAdapter] ...|> thực thi ...| Port_Metrics
        Adapt_Parser[ApachePoiUserExcelParser] ...|> thực thi ...| Port_Parser
        Adapt_Exporter[ApachePoiUserExcelExporter] ...|> thực thi ...| Port_Exporter
        
        Adapt_Metrics -->|đọc trạng thái| Exec_Spring[Spring TaskExecutors]
        Adapt_Parser -->|sử dụng| POI[Apache POI Library]
    end

    Controller -->|gọi thông qua Input Port| UC_Dashboard
    Controller -->|gọi thông qua Input Port| UC_Import
    Controller -->|gọi thông qua Input Port| UC_Export
```

---

## 3. Các thay đổi cấu trúc tệp tin mới

### 3.1. Các tệp tin được tạo mới (Tầng Core)
1. **[UserImportRow.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/user/application/usermanagement/command/UserImportRow.java)**: DTO độc lập chứa thông tin dòng Excel.
2. **[UserExcelParserPort.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/user/domain/ports/output/UserExcelParserPort.java)**: Cổng đọc file Excel.
3. **[UserExcelExporterPort.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/user/domain/ports/output/UserExcelExporterPort.java)**: Cổng ghi file Excel.
4. **[ThreadPoolMetrics.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/domain/task/ThreadPoolMetrics.java)**: Value Object lưu trữ số liệu hiệu năng của Thread Pool.
5. **[ExcelThreadPoolMetricsPort.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/domain/task/ExcelThreadPoolMetricsPort.java)**: Cổng truy xuất hiệu năng thread pool.
6. **[ExcelDashboardDTO.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/domain/task/ExcelDashboardDTO.java)**: DTO tổng hợp dữ liệu cho Dashboard.
7. **[GetExcelDashboardInputPort.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/application/task/GetExcelDashboardInputPort.java)**: Cổng vào Usecase truy vấn Dashboard.
8. **[GetExcelDashboardUseCase.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/application/task/GetExcelDashboardUseCase.java)**: Use Case tổng hợp các chỉ số thread pool, luồng hoạt động, và task lịch sử.

### 3.2. Các tệp tin được tạo mới (Tầng Infrastructure)
1. **[ExcelThreadTracker.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/infrastructure/task/ExcelThreadTracker.java)**: Registry in-memory ghi nhận hoạt động của các luồng.
2. **[SpringExcelThreadPoolMetricsAdapter.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/infrastructure/task/SpringExcelThreadPoolMetricsAdapter.java)**: Triển khai cổng `ExcelThreadPoolMetricsPort`, đọc dữ liệu trực tiếp từ các bean `ThreadPoolTaskExecutor` của Spring.

---

## 4. Các cơ chế nâng cấp đặc biệt

### 4.1. Cơ chế 1: Tự động khôi phục khi sập Server (Orphaned Task Recovery)
Tạo lớp **[ExcelTaskRecoveryListener.java](file:///home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/src/main/java/com/examp/springmvc/shared/infrastructure/task/ExcelTaskRecoveryListener.java)**:
* **Giải pháp**: Listener lắng nghe sự kiện Spring Boot khởi động hoàn tất (`ContextRefreshedEvent`), tự động quét các tác vụ ở trạng thái `PROCESSING` do sự cố tắt server đột ngột và chuyển chúng sang trạng thái `FAILED`.

### 4.2. Cơ chế 2: Ghi đè/Cập nhật thông tin (Upsert - Idempotency Import)
* **Giải pháp**: Nếu username đã tồn tại, Use Case tìm đối tượng `User` hiện tại lên, gọi các nghiệp vụ domain (`updateProfile`, `changeRole`, `changeStatus`) để cập nhật thay vì ném lỗi trùng lặp dữ liệu.
* **Lợi ích**: Cho phép chạy lại tệp Excel bị lỗi/sập nguồn mà không gây trùng dữ liệu.

### 4.3. Cơ chế 3: Giám sát chi tiết hoạt động từng luồng (Active Threads Tracker)
* **Giải pháp**: Trong các khối `try-finally` của luồng Reader (`ImportUsersUseCase`/`ExportUsersUseCase`) và luồng DB Writer (`processBatch`), hệ thống tự động đăng ký mô tả công việc đang chạy (ví dụ: *"Ghi DB lô 3 (dòng 2001-3000)"*) vào `ExcelThreadTracker`.
* **Kết quả**: Dashboard hiển thị thời gian thực danh sách tên luồng, trạng thái chạy và hành động chi tiết để quản trị viên theo dõi.

---

## 5. Kết quả kiểm thử tự động

Hệ thống đã chạy thành công qua quy trình kiểm tra chất lượng và build nghiêm ngặt:
```bash
mvn clean verify
```

### Kết quả đạt được:
* **Spotless & Checkstyle**: Định dạng chuẩn hóa hoàn hảo, **0** lỗi vi phạm checkstyle.
* **SpotBugs**: **0** cảnh báo lỗi bảo mật hoặc mã nguồn tiềm ẩn bug.
* **Unit Tests**: **165 / 165** ca kiểm thử đều vượt qua thành công.
