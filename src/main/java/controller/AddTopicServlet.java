package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Bean.Topic;
import model.Bean.User;
import model.DAO.JobDAO;
import model.DAO.TopicDAO;

@WebServlet("/addTopic")
public class AddTopicServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private TopicDAO topicDAO;
    private JobDAO jobDAO;

    public void init() {
        topicDAO = new TopicDAO();
        jobDAO = new JobDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String keyword = request.getParameter("keyword");

        if (keyword != null && !keyword.trim().isEmpty()) {
            Topic newTopic = new Topic();
            newTopic.setUserId(user.getId());
            newTopic.setKeyword(keyword.trim());
            
            topicDAO.addTopic(newTopic); 
            
            Topic addedTopic = topicDAO.getLastTopicByUser(user.getId());
            
            if(addedTopic != null) {
                 jobDAO.createJob(addedTopic.getId());
            }
        }
        
        response.sendRedirect("dashboard");
    }
}