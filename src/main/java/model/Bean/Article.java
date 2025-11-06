package model.Bean;

import java.sql.Timestamp;

public class Article {
    private int id;
    private String title;
    private String description;
    private String link;
    private Timestamp scrapedAt;
    private int transientTopicId; 

    public int getTransientTopicId() {
        return transientTopicId;
    }
    public void setTransientTopicId(int transientTopicId) {
        this.transientTopicId = transientTopicId;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
  
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public Timestamp getScrapedAt() {
        return scrapedAt;
    }
    public void setScrapedAt(Timestamp scrapedAt) {
        this.scrapedAt = scrapedAt;
    }
}