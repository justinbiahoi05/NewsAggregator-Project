package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Bean.Article;
import model.Bean.Topic;
import model.Bean.User;
import model.DAO.ArticleDAO;
import model.DAO.JobDAO;
import model.DAO.TopicDAO;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private TopicDAO topicDAO;
    private ArticleDAO articleDAO;
    private JobDAO jobDAO;

    public void init() {
        topicDAO = new TopicDAO();
        articleDAO = new ArticleDAO();
        jobDAO = new JobDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Đánh dấu job là đã xem
        jobDAO.markJobsAsViewed(user.getId());

        // Lấy danh sách chủ đề
        List<Topic> topics = topicDAO.getTopicsByUserId(user.getId());

        // Lấy danh sách bài báo
        List<Article> articles = articleDAO.getArticlesByUserId(user.getId());

        request.setAttribute("topics", topics);
        request.setAttribute("articles", articles);

        RequestDispatcher dispatcher = request.getRequestDispatcher("dashboard.jsp");
        dispatcher.forward(request, response);
    }
}