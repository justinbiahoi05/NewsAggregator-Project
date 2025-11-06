package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Bean.Article;

public class ArticleDAO {

    public List<Article> getArticlesByUserId(int userId) {
        List<Article> articles = new ArrayList<>();
        
        String sql = "SELECT a.* FROM articles a " +
                     "JOIN topic_articles ta ON a.id = ta.article_id " +
                     "JOIN topics t ON ta.topic_id = t.id " +
                     "WHERE t.user_id = ? " +
                     "ORDER BY a.scraped_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setTitle(rs.getString("title"));
                article.setDescription(rs.getString("description"));
                article.setLink(rs.getString("link"));
                article.setScrapedAt(rs.getTimestamp("scraped_at"));
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public void saveArticle(Article article) {
        String checkArticleSql = "SELECT id FROM articles WHERE link = ?";
        String insertArticleSql = "INSERT INTO articles (title, description, link) VALUES (?, ?, ?)";
        String linkArticleSql = "INSERT IGNORE INTO topic_articles (topic_id, article_id) VALUES (?, ?)";
        
        Connection conn = null;
        long articleId = -1;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement psCheck = conn.prepareStatement(checkArticleSql)) {
                psCheck.setString(1, article.getLink());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        articleId = rs.getLong("id");
                    }
                }
            }
            
            if (articleId == -1) {
                try (PreparedStatement psInsert = conn.prepareStatement(insertArticleSql, Statement.RETURN_GENERATED_KEYS)) {
                    psInsert.setString(1, article.getTitle());
                    psInsert.setString(2, article.getDescription());
                    psInsert.setString(3, article.getLink());
                    psInsert.executeUpdate();
                    
                    try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            articleId = generatedKeys.getLong(1);
                        }
                    }
                }
            }
            
            if (articleId != -1) {
                try (PreparedStatement psLink = conn.prepareStatement(linkArticleSql)) {
                    psLink.setInt(1, article.getTransientTopicId());
                    psLink.setLong(2, articleId);
                    psLink.executeUpdate();
                }
            }
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}