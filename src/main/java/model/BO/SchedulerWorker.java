package model.BO;

import java.util.List;

import model.Bean.Article;
import model.Bean.Topic;
import model.DAO.ArticleDAO;
import model.DAO.TopicDAO;

public class SchedulerWorker implements Runnable {

    private volatile boolean running = true;
    private TopicDAO topicDAO;
    private ArticleDAO articleDAO;
    private ScrapingService scrapingService;

    private final long SLEEP_TIME_MS = 1000 * 60 * 60;

    public SchedulerWorker() {
        this.topicDAO = new TopicDAO();
        this.articleDAO = new ArticleDAO();
        this.scrapingService = new ScrapingService();
    }

    @Override
    public void run() {
        System.out.println(">>> Scheduler (1 giờ/lần) đã khởi động!");
        while (running) {
            try {
                System.out.println("Scheduler: Bắt đầu quét tất cả chủ đề...");

                List<Topic> allTopics = topicDAO.getAllTopics();

                for (Topic topic : allTopics) {
                	List<Article> articles = scrapingService.scrapeNewsForTopic(topic);

                    for (Article article : articles) {
                        articleDAO.saveArticle(article);
                    }
                }

                System.out.println("Scheduler: Đã quét xong. Đi ngủ 1 giờ.");
                Thread.sleep(SLEEP_TIME_MS); 

            } catch (InterruptedException e) {
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(">>> Scheduler (1 giờ/lần) đã dừng.");
    }

    public void stop() {
        running = false;
    }
}