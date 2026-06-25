# Lý thuyết Domain-Driven Design (DDD) - Core Concepts

Tài liệu này tổng hợp lại các khái niệm lý thuyết cốt lõi về DDD (Aggregate, Aggregate Root, Value Object, Bounded Context) mà chúng ta đã cùng tìm hiểu và làm rõ.

---

## 1. Value Object (Đối tượng giá trị)
*   **Đặc điểm:** 
    *   Không có danh tính (no identity) riêng biệt. Hai Value Object được coi là bằng nhau nếu mọi thuộc tính của chúng bằng nhau.
    *   **Bất biến (Immutable):** Giá trị không thể thay đổi sau khi tạo ra. Muốn đổi phải tạo đối tượng mới thay thế hoàn toàn.
    *   **Tự xác thực (Self-validation):** Tự kiểm tra tính hợp lệ dữ liệu ngay trong hàm khởi tạo.
*   **Ví dụ trong dự án:**
    *   `Email` (chứa chuỗi email và kiểm tra định dạng Regex).
    *   `Password` (chứa mật khẩu đã mã hóa).
    *   `Address` (chứa địa chỉ giao hàng của người dùng).

---

## 2. Aggregate & Aggregate Root (Cụm thực thể & Gốc thực thể)
*   **Aggregate (Cụm thực thể):** Là một cụm gồm các thực thể (Entities) và Value Objects đi liền với nhau nhằm đảm bảo các quy tắc nghiệp vụ và ràng buộc tính nhất quán dữ liệu (Business Invariants).
*   **Aggregate Root (AR - Gốc thực thể):**
    *   Là cổng giao tiếp duy nhất giữa các thành phần bên ngoài và các đối tượng bên trong Aggregate.
    *   AR bắt buộc phải có **ID toàn cục** (Global Identity) để hệ thống truy vấn thông qua Repository.
    *   Mọi thay đổi dữ liệu của các thành phần con bắt buộc phải thực hiện thông qua các phương thức nghiệp vụ của AR.
*   **Ví dụ trong dự án:**
    *   `User` là **Aggregate Root**.
    *   `Address` là **Value Object** thuộc Aggregate của `User`.
    *   Để thêm/sửa địa chỉ, ta bắt buộc phải gọi: `user.addAddress(newAddress)` thay vì gọi trực tiếp lưu địa chỉ.

---

## 3. Bounded Context (Ngữ cảnh giới hạn)
*   **Đặc điểm:**
    *   Là ranh giới lớn ở cấp độ chiến lược (vĩ mô). Thường tương ứng với 1 Microservice hoặc 1 phân hệ lớn độc lập.
    *   Trong một Bounded Context, ngôn ngữ nghiệp vụ (Ubiquitous Language) được định nghĩa thống nhất và không bị mơ hồ.
*   **Mối quan hệ với Aggregate:**
    *   **Một Bounded Context có thể chứa nhiều Aggregate Root khác nhau.**
    *   *Ví dụ:* Bounded Context `User (Identity)` có thể chứa Aggregate Root `User`, `Role`.

---

## 4. Tóm tắt mô hình phân tách

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       BOUNDED CONTEXT (Ví dụ: User Context)                 │
│                                                                             │
│   ┌─────────────────────────────────────────────────────────────┐           │
│   │               AGGREGATE (Ranh giới nhất quán)               │           │
│   │                                                             │           │
│   │      ┌───────────────────────────────────────────────┐      │           │
│   │      │         AGGREGATE ROOT (Ví dụ: User)          │      │           │
│   │      │              (Có Global ID)                   │      │           │
│   │      └──────────────────────┬────────────────────────┘      │           │
│   │                             │ quản lý                       │           │
│   │                             ▼                               │           │
│   │      ┌───────────────────────────────────────────────┐      │           │
│   │      │     VALUE OBJECTS (Ví dụ: Email, Address)     │      │           │
│   │      │              (Không có ID riêng)              │      │           │
│   │      └───────────────────────────────────────────────┘      │           │
│   └─────────────────────────────────────────────────────────────┘           │
└─────────────────────────────────────────────────────────────────────────────┘
```
