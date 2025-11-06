package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Bean.User;
import model.DAO.JobDAO;

@WebServlet("/jobStatus")
public class JobStatusServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private JobDAO jobDAO;

    public void init() {
        jobDAO = new JobDAO();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String jsonResponse;
        
        if (user == null) {
            jsonResponse = "{\"status\": \"error\", \"message\": \"Not logged in\"}";
        } else {
            String status = jobDAO.getJobStatus(user.getId());
            jsonResponse = "{\"status\": \"" + status + "\"}";
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }
}