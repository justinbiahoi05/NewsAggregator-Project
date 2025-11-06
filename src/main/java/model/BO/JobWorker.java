package model.BO;

import java.util.List;

import model.Bean.Article;
import model.Bean.Topic;
import model.DAO.ArticleDAO;
import model.DAO.JobDAO;

public class JobWorker implements Runnable {

    private volatile boolean running = true;
    private JobDAO jobDAO;
    private ArticleDAO articleDAO;
    private ScrapingService scrapingService;

    public JobWorker() {
        this.jobDAO = new JobDAO();
        this.articleDAO = new ArticleDAO();
        this.scrapingService = new ScrapingService();
    }

    @Override
    public void run() {
        System.out.println(">>> Job Worker (Queue) đã khởi động!");
        while (running) {
            try {
                Topic topicToScrape = jobDAO.getAndMarkPendingJob();

                if (topicToScrape != null) {
                    System.out.println("Worker: Đang xử lý job cho chủ đề: " + topicToScrape.getKeyword());
                    List<Article> articles = scrapingService.scrapeNewsForTopic(topicToScrape);

                    for (Article article : articles) {
                        articleDAO.saveArticle(article);
                    }
                    
                    jobDAO.completeJobByTopicId(topicToScrape.getId());
                    System.out.println("Worker: Đã xử lý xong job cho: " + topicToScrape.getKeyword());

                } else {
                    Thread.sleep(5000); 
                }

            } catch (InterruptedException e) {
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(">>> Job Worker (Queue) đã dừng.");
    }

    public void stop() {
        running = false;
    }
}