package controller;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpRes = (HttpServletResponse) res;
        
        String uri = httpReq.getRequestURI();
        
        HttpSession session = httpReq.getSession(false); 
        
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);
        
        boolean isPublicPage = uri.endsWith("login.jsp") || 
                               uri.endsWith("/login") || 
                               uri.endsWith("register.jsp") || 
                               uri.endsWith("/register") ||
                               uri.contains("/pico@2/css/");

        if (isLoggedIn || isPublicPage) {
            chain.doFilter(req, res);
        } else {
            httpRes.sendRedirect(httpReq.getContextPath() + "/login.jsp");
        }
    }
}