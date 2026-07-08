# Tổng Quan Về Oracle Database: Định Nghĩa, Đặc Điểm & So Sánh

Tài liệu này cung cấp cái nhìn tổng quan và chuyên sâu về Oracle Database, kiến trúc lõi, sự khác biệt giữa các phiên bản (Editions), và so sánh với các hệ quản trị cơ sở dữ liệu (DBMS) phổ biến khác để phục vụ cho các dự án phát triển phần mềm doanh nghiệp lớn.

---

## 1. Định Nghĩa Oracle Database

**Oracle Database** (hay Oracle RDBMS) là một hệ quản trị cơ sở dữ liệu quan hệ đối tượng (Object-Relational Database Management System - ORDBMS) hàng đầu thế giới được phát triển và thương mại hóa bởi Oracle Corporation. 

Khác với các cơ sở dữ liệu quan hệ thuần túy chỉ làm việc với bảng (rows/columns), Oracle hỗ trợ mô hình hướng đối tượng, cho phép định nghĩa các kiểu dữ liệu phức tạp do người dùng tự đặt ra (User-Defined Types) và tích hợp sâu lập trình thủ tục bên trong DB thông qua ngôn ngữ PL/SQL.

---

## 2. Các Đặc Điểm Nổi Bật Của Oracle

*   **Tính Đa Mô Hình (Multi-model):** Trong cùng một database, Oracle hỗ trợ lưu trữ dữ liệu quan hệ (SQL), tài liệu bán cấu trúc (JSON, XML), dữ liệu đồ thị (Graph), dữ liệu không gian (Spatial), và các cặp khóa-giá trị (Key-Value).
*   **Tính Toàn Vẹn & Nhất Quán Tuyệt Đối (ACID):** Oracle đảm bảo tính nhất quán dữ liệu cực kỳ khắt khe, kiểm soát giao dịch đồng thời ở mức độ cao mà không làm nghẽn quá trình đọc dữ liệu nhờ cơ chế **Multi-Version Read Consistency** (người đọc không block người ghi, người ghi không block người đọc).
*   **PL/SQL (Procedural Language/SQL):** Cho phép lập trình viên viết các stored procedure, trigger, function và package có cấu trúc điều khiển (vòng lặp, rẽ nhánh) chạy trực tiếp và tối ưu hóa cao ngay tại máy chủ database.
*   **Khả năng chịu tải & Tính sẵn sàng cao (High Availability):** Hỗ trợ các công nghệ hàng đầu như **Oracle RAC** (Real Application Clusters - chạy song song nhiều server cùng trỏ vào một DB) và **Active Data Guard** (đồng bộ dữ liệu sang DB dự phòng theo thời gian thực).

---

## 3. Kiến Trúc Cơ Bản Của Oracle

Kiến trúc Oracle được chia làm hai phần tách biệt: **Instance** (Thành phần bộ nhớ & tiến trình logic trong RAM) và **Database** (Thành phần lưu trữ vật lý trên đĩa cứng).

```mermaid
graph TD
    subgraph Instance ["Oracle Instance (RAM)"]
        subgraph SGA ["SGA (Shared Memory)"]
            BC["Database Buffer Cache"]
            SP["Shared Pool"]
            RLB["Redo Log Buffer"]
        en
        subgraph Processes ["Background Processes"]
            DBWn["DBWn (Database Writer)"]
            LGWR["LGWR (Log Writer)"]
            CKPT["CKPT (Checkpoint)"]
        end
    end

    subgraph DB ["Oracle Database (Storage)"]
        DF["Data Files (.dbf)"]
        CF["Control Files (.ctl)"]
        RLF["Redo Log Files (.log)"]
    end

    BC --> DBWn
    RLB --> LGWR
    DBWn --> DF
    LGWR --> RLF
    CKPT --> CF
    CKPT --> DF
```

### 3.1. Oracle Instance (Thành phần logic & Bộ nhớ trong RAM)
Mỗi khi khởi động Oracle Database, một **Instance** được tạo ra trong bộ nhớ RAM của máy chủ để quản lý dữ liệu và thực thi các câu lệnh. Nó bao gồm hai vùng nhớ chính và các tiến trình nền:

*   **SGA (System Global Area - Vùng nhớ dùng chung):** Được chia sẻ cho tất cả các session kết nối vào Database.
    *   **Database Buffer Cache:** Nơi lưu trữ tạm thời các khối dữ liệu (Data Blocks) được đọc từ đĩa cứng lên. Khi có câu truy vấn mới, Oracle sẽ tìm ở đây trước để tránh việc đọc đĩa chậm chạp.
    *   **Shared Pool:** Lưu trữ mã SQL/PL-SQL đã biên dịch và từ điển dữ liệu (Data Dictionary). Giúp tăng tốc thực thi cho các câu lệnh giống nhau chạy nhiều lần.
    *   **Redo Log Buffer:** Bộ đệm ghi lại nhật ký tất cả các thay đổi trên cơ sở dữ liệu. Dữ liệu từ đây sẽ được ghi xuống đĩa trước khi giao dịch (Transaction) hoàn tất để đảm bảo tính an toàn dữ liệu.
*   **PGA (Program Global Area - Vùng nhớ riêng tư):** Cấp phát riêng cho mỗi phiên kết nối (Session). Nó chứa thông tin trạng thái của session và được dùng cho các hoạt động tính toán nặng như sắp xếp dữ liệu (`Sort Area`) và gộp bảng (`Hash Join Area`).
*   **Background Processes (Các tiến trình nền chạy ngầm):**
    *   **DBWn (Database Writer):** Tiến trình ghi ngầm các khối dữ liệu bị thay đổi (dirty blocks) từ `Buffer Cache` xuống các file lưu trữ vật lý (`Data Files`).
    *   **LGWR (Log Writer):** Ghi nhật ký giao dịch từ `Redo Log Buffer` xuống các file nhật ký vật lý (`Redo Log Files`).
    *   **CKPT (Checkpoint):** Tiến trình đồng bộ trạng thái, cập nhật thông tin checkpoint vào `Control Files` và ghi nhận để đảm bảo tính nhất quán dữ liệu vật lý khi hệ thống gặp sự cố.

### 3.2. Oracle Database (Thành phần lưu trữ vật lý trên đĩa cứng)
Là nơi lưu trữ dữ liệu bền vững dưới dạng các tệp tin trên hệ điều hành:

*   **Data Files (.dbf):** Chứa dữ liệu thực tế của các bảng, các chỉ mục (indexes) và hệ thống dữ liệu cốt lõi.
*   **Control Files (.ctl):** File điều khiển chứa thông tin cấu trúc vật lý của cơ sở dữ liệu (tên DB, vị trí và trạng thái của các files dữ liệu và nhật ký). Đây là file tối quan trọng để Oracle có thể khởi động (Mount/Open).
*   **Redo Log Files (.log):** File lưu trữ nhật ký giao dịch vật lý. Khi xảy ra sự cố sập nguồn hoặc mất điện đột ngột, Oracle sẽ dùng file này để khôi phục lại dữ liệu chưa kịp ghi xuống Data Files.

### 3.3. Kiến trúc CDB & PDB (Multitenant)
Từ phiên bản 12c trở đi, Oracle hỗ trợ kiến trúc đa thuê:
*   **CDB (Container Database):** Database mẹ, quản lý tài nguyên hệ thống, bộ nhớ SGA và tiến trình nền chung.
*   **PDB (Pluggable Database):** Các database con hoạt động độc lập và "cắm" vào CDB mẹ. Mỗi ứng dụng Java/Spring sẽ kết nối tới một PDB riêng biệt như một database thông thường, giúp dễ dàng di chuyển và quản trị tập trung.

---

## 4. Phân Biệt Các Phiên Bản (Editions) Của Oracle

| Phiên bản (Edition) | Đối tượng & Quy mô | Giới hạn tài nguyên | Tính năng nổi bật | Chi phí |
| :--- | :--- | :--- | :--- | :--- |
| **Enterprise Edition (EE)** | Doanh nghiệp lớn, ngân hàng, viễn thông | Không giới hạn | Đầy đủ mọi tính năng: RAC, Data Guard, Partitioning, Advanced Security | Rất đắt |
| **Standard Edition 2 (SE2)** | Doanh nghiệp vừa và nhỏ | Max 2 CPU Sockets, giới hạn 16 threads/instance | Có các tính năng cơ bản, không có RAC/Data Guard nâng cao | Trung bình |
| **Free / Express Edition (XE)** | Học tập, thử nghiệm, dự án siêu nhỏ | Max 2 Cores CPU, 2GB RAM, 12GB Dữ liệu | Hỗ trợ hầu hết tính năng phát triển của EE, bao gồm cả Vector Search | **Miễn phí** |
| **Personal Edition** | Máy cá nhân của lập trình viên | Chỉ cho 1 người dùng | Đầy đủ tính năng của EE ngoại trừ RAC | Trả phí |

---

## 5. So Sánh Oracle Database Với Các Hệ Quản Trị CSDL Khác

| Tiêu chí | Oracle Database | MySQL | PostgreSQL | SQL Server |
| :--- | :--- | :--- | :--- | :--- |
| **Bản quyền** | Thương mại (Oracle) | Open-source (Oracle sở hữu) | Open-source (Tự do hoàn toàn) | Thương mại (Microsoft) |
| **Môi trường phù hợp** | Hệ thống Core lớn, ngân hàng, ERP | Ứng dụng Web, thương mại điện tử | Xử lý dữ liệu phức tạp, Startup, GIS | Doanh nghiệp dùng hệ sinh thái Windows |
| **Khả năng Scaling & HA** | **Oracle RAC** (Active-Active) tốt nhất thị trường | Master-Slave / InnoDB Cluster | Replication hoặc BDR phức tạp | Always On Availability Groups |
| **Hỗ trợ lập trình (Stored Code)** | **PL/SQL** cực mạnh và phổ biến | SQL Stored Procedure cơ bản | PL/pgSQL mạnh mẽ, mở rộng tốt | T-SQL mạnh mẽ |
| **Chi phí triển khai** | Rất cao | Thấp / Miễn phí | Miễn phí | Trung bình - Cao |

---

## 6. Ý Nghĩa Ký Tự Phiên Bản (g, c, và ai)

*   **g (Grid Computing - Tính toán lưới):** Ví dụ `Oracle 10g`, `11g`. Tập trung vào khả năng kết nối nhiều máy chủ đơn lẻ thành một lưới tài nguyên tính toán đồng nhất.
*   **c (Cloud Computing - Điện toán đám mây):** Ví dụ `Oracle 12c`, `18c`, `19c`, `21c`. Ra mắt kiến trúc Container (CDB) và Pluggable Database (PDB) hỗ trợ triển khai linh hoạt trên hạ tầng đám mây.
*   **ai (Artificial Intelligence - Trí tuệ nhân tạo):** Ví dụ `Oracle 23ai`. Phiên bản mới nhất (thay thế cho tên gọi 23c trước đó) tập trung tích hợp sâu các tính năng tìm kiếm Vector (Vector Search), AI và mô hình hóa dữ liệu thông minh JSON-Relational Duality phục vụ phát triển ứng dụng thông minh hiện đại.
