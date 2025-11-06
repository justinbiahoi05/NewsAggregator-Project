package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.BO.ScrapingService;
import model.Bean.Article;
import model.Bean.Topic;
import model.DAO.ArticleDAO;
import model.DAO.TopicDAO;

@WebServlet("/admin-run")
public class AdminForceRunServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private TopicDAO topicDAO;
    private ArticleDAO articleDAO;
    private ScrapingService scrapingService;

    public void init() {
        topicDAO = new TopicDAO();
        articleDAO = new ArticleDAO();
        scrapingService = new ScrapingService();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println(">>> ADMIN: Yêu cầu buộc chạy quét TẤT CẢ chủ đề...");
        
        try {
            List<Topic> allTopics = topicDAO.getAllTopics(); 

            for (Topic topic : allTopics) {
            	List<Article> articles = scrapingService.scrapeNewsForTopic(topic);

                for (Article article : articles) {
                    articleDAO.saveArticle(article);
                }
            }
            
            System.out.println(">>> ADMIN: Đã quét xong.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect("dashboard");
    }
}