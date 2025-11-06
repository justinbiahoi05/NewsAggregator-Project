package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Bean.User;
import model.DAO.TopicDAO;

@WebServlet("/deleteTopic")
public class DeleteTopicServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private TopicDAO topicDAO;

    public void init() {
        topicDAO = new TopicDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int topicId = Integer.parseInt(request.getParameter("id"));
            
            topicDAO.deleteTopic(topicId, user.getId());
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        response.sendRedirect("dashboard");
    }
}