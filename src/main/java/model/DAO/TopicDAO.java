package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Bean.Topic;

public class TopicDAO {

    public List<Topic> getTopicsByUserId(int userId) {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topics WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Topic topic = new Topic();
                topic.setId(rs.getInt("id"));
                topic.setUserId(rs.getInt("user_id"));
                topic.setKeyword(rs.getString("keyword"));
                topics.add(topic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }

    public void addTopic(Topic topic) {
        String sql = "INSERT INTO topics (user_id, keyword) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, topic.getUserId());
            ps.setString(2, topic.getKeyword());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Topic getLastTopicByUser(int userId) {
        String sql = "SELECT * FROM topics WHERE user_id = ? ORDER BY id DESC LIMIT 1";
        Topic topic = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                topic = new Topic();
                topic.setId(rs.getInt("id"));
                topic.setUserId(rs.getInt("user_id"));
                topic.setKeyword(rs.getString("keyword"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topic;
    }
    
    public List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM topics";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Topic topic = new Topic();
                topic.setId(rs.getInt("id"));
                topic.setUserId(rs.getInt("user_id"));
                topic.setKeyword(rs.getString("keyword"));
                topics.add(topic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }
    
    public void deleteTopic(int topicId, int userId) {
        String sql = "DELETE FROM topics WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, topicId);
            ps.setInt(2, userId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}