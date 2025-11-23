<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Bean.User" %>
<%@ page import="model.Bean.Topic" %>
<%@ page import="model.Bean.Article" %>
<%
    User user = (User) session.getAttribute("user");

	@SuppressWarnings("unchecked") 
	List<Topic> topics = (List<Topic>) request.getAttribute("topics");
	
	@SuppressWarnings("unchecked")
	List<Article> articles = (List<Article>) request.getAttribute("articles");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bảng điều khiển của <%= user.getUsername() %></title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <script type="text/javascript">
    function checkJobStatus() {
        fetch('jobStatus')
            .then(response => response.json())
            .then(data => {
                console.log("Kiểm tra job: " + data.status);
                
                if (data.status === 'running' || data.status === 'pending') {
                    setTimeout(checkJobStatus, 5000); 
                } else if (data.status === 'completed') {
                    console.log("Job hoàn tất! Đang tải lại trang...");
                    window.location.reload();
                }
            });
    }

    window.onload = function() {
        checkJobStatus();
    };
</script>
</head>
<body>

    <nav class="container">
        <ul>
            <li><strong>Xin chào, <%= user.getUsername() %>!</strong></li>
        </ul>
        <ul>
            <li><a href="admin-run" role="button" class="secondary">Quét ngay</a></li>
            <li><a href="logout" role="button">Đăng xuất</a></li>
        </ul>
    </nav>

    <main class="container">
    
        <div class="grid">
            <article>
                <h3>Thêm chủ đề mới</h3>
                <form action="addTopic" method="post">
                    <div class="grid">
                        <input type="text" name="keyword" placeholder="Ví dụ: Lũ lụt" required>
                        <button type="submit">Thêm</button>
                    </div>
                </form>
            </article>
            
            <article>
                <h3>Các chủ đề đang theo dõi</h3>
                <ul>
                    <% for (Topic topic : topics) { %>
                        <li>
                            <%= topic.getKeyword() %> 
                            <a href="deleteTopic?id=<%= topic.getId() %>" 
                               style="color:var(--pico-color-red-500); text-decoration:none;" 
                               title="Xóa chủ đề">   [Xóa]</a>
                        </li>
                    <% } %>
                </ul>
            </article>
        </div>

        <article>
            <h3>Tin tức của bạn</h3>
            <figure>
                <table role="grid">
                    <thead>
                        <tr>
                            <th scope="col">Tiêu đề</th>
                            <th scope="col">Mô tả</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Article article : articles) { %>
                            <tr>
                                <td><a href="<%= article.getLink() %>" target="_blank"><%= article.getTitle() %></a></td>
                                <td><%= article.getDescription() %></td>
                            </tr>
                        <% } %>
                        <% if (articles.isEmpty()) { %>
                            <tr>
                                <td colspan="2">Chưa có tin tức nào. Hãy thêm chủ đề và bấm "Quét ngay".</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </figure>
        </article>
        
    </main>
</body>
</html>