<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yêu cầu không hợp lệ (400) - Spring MVC Demo</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/pages/error.css?v=${appVersion}">
</head>
<body class="error-400">
    <div class="container">
        <div class="error-card">
            <div class="error-code">400</div>
            <h1 class="error-title">Yêu cầu không hợp lệ</h1>
            <p class="error-desc">Dữ liệu gửi lên máy chủ không hợp lệ hoặc đã bị thay đổi cấu trúc sai quy định.</p>
            <a href="${pageContext.request.contextPath}/" class="btn-home">Quay lại Trang chủ</a>
        </div>
    </div>
</body>
</html>
