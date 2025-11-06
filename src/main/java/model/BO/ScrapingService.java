package model.BO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.Bean.Article;
import model.Bean.Topic;

public class ScrapingService {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.0.0 Safari/537.36";

    public List<Article> scrapeNewsForTopic(Topic topic) {
        List<Article> allArticles = new ArrayList<>();
        
        allArticles.addAll( scrapeVnExpressHomepage(topic) ); 
        
        allArticles.addAll( scrapeDanTriHomepage(topic) ); 
        
        allArticles.addAll( scrapeTuoiTreHomepage(topic) ); 

        return allArticles;
    }

    private List<Article> scrapeVnExpressHomepage(Topic topic) {
        List<Article> articles = new ArrayList<>();
        String url = "https://vnexpress.net/";
        System.out.println(">>> DEBUG (VnExpress): Cào TRANG CHỦ: " + url + " cho: " + topic.getKeyword());
        
        try {
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000).get();
            
            Elements articleElements = doc.select("article.item-news"); 
            System.out.println(">>> DEBUG (VnExpress): Tìm thấy " + articleElements.size() + " bài trên trang chủ.");

            for (Element el : articleElements) {
                String title = el.select("h3.title-news > a").text();
                String link = el.select("h3.title-news > a").attr("href");
                String description = el.select("p.description").text();

                if (!title.isEmpty() && !link.isEmpty() && 
                    title.toLowerCase().contains(topic.getKeyword().toLowerCase())) {
                    
                    System.out.println(">>> DEBUG (VnExpress): [KHỚP!] + " + title);
                    articles.add(createArticle(topic, title, link, description));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return articles;
    }
    
    private List<Article> scrapeDanTriHomepage(Topic topic) {
        List<Article> articles = new ArrayList<>();
        String url = "https://dantri.com.vn/";
        System.out.println(">>> DEBUG (DanTri): Cào TRANG CHỦ: " + url + " cho: " + topic.getKeyword());
        
        try {
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000).get();
            Elements articleElements = doc.select("article.article-item"); 
            System.out.println(">>> DEBUG (DanTri): Tìm thấy " + articleElements.size() + " bài trên trang chủ.");

            for (Element el : articleElements) {
                String title = el.select("h3.article-title > a").text();
                String link = el.select("h3.article-title > a").attr("href");
                String description = el.select("div.article-excerpt, p.article-excerpt").text();

                if (!link.startsWith("http")) {
                    link = "https://dantri.com.vn" + link;
                }

                if (!title.isEmpty() && !link.isEmpty() && 
                    title.toLowerCase().contains(topic.getKeyword().toLowerCase())) {
                        
                    System.out.println(">>> DEBUG (DanTri): [KHỚP!] + " + title);
                    articles.add(createArticle(topic, title, link, description));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return articles;
    }
    
    private List<Article> scrapeTuoiTreHomepage(Topic topic) {
        List<Article> articles = new ArrayList<>();
        String url = "https://tuoitre.vn/";
        System.out.println(">>> DEBUG (TuoiTre): Cào TRANG CHỦ: " + url + " cho: " + topic.getKeyword());
        
        try {
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(5000).get();
            
            Elements articleElements = doc.select("a[title]"); 
            System.out.println(">>> DEBUG (TuoiTre): Tìm thấy " + articleElements.size() + " link trên trang chủ.");

            for (Element linkEl : articleElements) {
                String title = linkEl.attr("title");
                String link = linkEl.attr("href");
                String description = "";

                if (!link.startsWith("http")) {
                    link = "https://tuoitre.vn" + link;
                }

                if (!title.isEmpty() && !link.isEmpty() && 
                    link.endsWith(".htm") &&
                    title.toLowerCase().contains(topic.getKeyword().toLowerCase())) {
                        
                    System.out.println(">>> DEBUG (TuoiTre): [KHỚP!] + " + title);
                    articles.add(createArticle(topic, title, link, description));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return articles;
    }

    private Article createArticle(Topic topic, String title, String link, String description) {
        Article article = new Article();
        article.setTransientTopicId(topic.getId());
        article.setTitle(title);
        article.setLink(link);	
        article.setDescription(description);
        return article;
    }
}