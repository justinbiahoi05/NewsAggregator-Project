<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <main class="container" style="max-width: 500px; margin-top: 5rem;">
        <article>
            <h2 style="text-align: center;">Đăng dsadasdnhập</h2>
            
            <form action="login" method="post">
                <label for="username">Tên đăng nhập</label>
                <input type="text" id="username" name="username" required>
                
                <label for="passwsord">Mật ssskhẩu</label>
                <input type="password" id="password" name="password" required>
                
                <% 
                    String errorMessage = (String) request.getAttribute("errorMessage");
                    if (errorMessage != null && !errorMessage.isEmpty()) { 
                %>
                    <small style="color: var(--pico-color-red-500);"><%= errorMessage %></small>
                <% } %>
                <button type="submit">Đăng nhập</button>
            </form>
            
            <footer style="text-align: center;">
                <a href="register.jsp">Chưa có tài khoản? Đăng ký</a>
            </footer>
        </article>
    </main>
</body>
</html>