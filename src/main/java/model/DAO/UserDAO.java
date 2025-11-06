package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Bean.User;

public class UserDAO {

public User checkLogin(String username, String password) {
        
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null; 

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                
                if (rs.next()) {
                    String hashedPasswordFromDB = rs.getString("password");
                    
                    if (model.BO.PasswordUtil.checkPassword(password, hashedPasswordFromDB)) {
                        user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        user.setPassword(hashedPasswordFromDB);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    
    public boolean registerUser(String username, String password) {
        String hashedPassword = model.BO.PasswordUtil.hashPassword(password);
        
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            int rowsAffected = ps.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Lỗi 1062 là lỗi UNIQUE (trùng username) của MySQL
            if (e.getErrorCode() == 1062) {
                System.err.println("Lỗi: Tên đăng nhập '" + username + "' đã tồn tại.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
    
}