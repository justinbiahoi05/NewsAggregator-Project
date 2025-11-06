package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Bean.Topic;

public class JobDAO {

    public void createJob(int topicId) {
        String sql = "INSERT INTO fetch_jobs (topic_id, status) VALUES (?, 'pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, topicId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Topic getAndMarkPendingJob() {
        Topic topic = null;
        int jobId = -1;
        
        String findJobSql = "SELECT j.id, t.id as topic_id, t.keyword " +
                            "FROM fetch_jobs j " +
                            "JOIN topics t ON j.topic_id = t.id " +
                            "WHERE j.status = 'pending' " +
                            "LIMIT 1 FOR UPDATE";

        String updateStatusSql = "UPDATE fetch_jobs SET status = 'running' WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Tìm và khóa job
            try (PreparedStatement psFind = conn.prepareStatement(findJobSql);
                 ResultSet rs = psFind.executeQuery()) {
                
                if (rs.next()) {
                    jobId = rs.getInt("id");
                    topic = new Topic();
                    topic.setId(rs.getInt("topic_id"));
                    topic.setKeyword(rs.getString("keyword"));
                }
            }

            // 2. Nếu tìm thấy job, cập nhật status
            if (jobId != -1) {
                try (PreparedStatement psUpdate = conn.prepareStatement(updateStatusSql)) {
                    psUpdate.setInt(1, jobId);
                    psUpdate.executeUpdate();
                }
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return topic;
    }
    
    public boolean hasRunningJob(int userId) {
        String sql = "SELECT 1 FROM fetch_jobs j " +
                     "JOIN topics t ON j.topic_id = t.id " +
                     "WHERE t.user_id = ? AND j.status = 'running' LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void completeJobByTopicId(int topicId) {
        String sql = "UPDATE fetch_jobs SET status = 'completed', is_viewed = 0 " +
                     "WHERE topic_id = ? AND status = 'running'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, topicId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getJobStatus(int userId) {
        String status = "idle";
        
        String sqlRunning = "SELECT 'running' as status FROM fetch_jobs j " +
                            "JOIN topics t ON j.topic_id = t.id " +
                            "WHERE t.user_id = ? AND j.status = 'running' LIMIT 1";

        String sqlCompleted = "SELECT 'completed' as status FROM fetch_jobs j " +
                              "JOIN topics t ON j.topic_id = t.id " +
                              "WHERE t.user_id = ? AND j.status = 'completed' AND j.is_viewed = 0 LIMIT 1";
                              
        String sqlPending = "SELECT 'pending' as status FROM fetch_jobs j " +
                            "JOIN topics t ON j.topic_id = t.id " +
                            "WHERE t.user_id = ? AND j.status = 'pending' LIMIT 1";

        try (Connection conn = DBConnection.getConnection()) {
            
            try (PreparedStatement ps = conn.prepareStatement(sqlRunning)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("status");
                }
            }
            
            try (PreparedStatement ps = conn.prepareStatement(sqlCompleted)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("status");
                }
            }
            
            // Check 3: Pending?
            try (PreparedStatement ps = conn.prepareStatement(sqlPending)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getString("status");
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return status;
    }
    
    public void markJobsAsViewed(int userId) {
        String sql = "UPDATE fetch_jobs j " +
                     "JOIN topics t ON j.topic_id = t.id " +
                     "SET j.is_viewed = 1 " +
                     "WHERE t.user_id = ? AND j.status = 'completed'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}