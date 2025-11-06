<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng ký</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <main class="container" style="max-width: 500px; margin-top: 5rem;">
        <article>
            <h2 style="text-align: center;">Đăng ký</h2>
            
            <form action="register" method="post">
                <label for="username">Tên đăng nhập</label>
                <input type="text" id="username" name="username" required>
                
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" required>

                <label for="confirm_password">Nhập lại Mật khẩu</label>
                <input type="password" id="confirm_password" name="confirm_password" required>
                <% 
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    if (errorMessage != null && !errorMessage.isEmpty()) { 
                %>
                    <small style="color: var(--pico-color-red-500);"><%= errorMessage %></small>
                <% } %>
                <button type="submit" style="margin-top: 1rem;">Đăng ký</button>
            </form>
            
            <footer style="text-align: center;">
                <a href="login.jsp">Đã có tài khoản? Đăng nhập</a>
            </footer>
        </article>
    </main>
</body>
</html>