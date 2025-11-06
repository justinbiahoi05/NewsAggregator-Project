package model.BO;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    private Thread jobWorkerThread;
    private JobWorker jobWorker;

    private Thread schedulerWorkerThread;
    private SchedulerWorker schedulerWorker;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>> Ứng dụng BẮT ĐẦU. Khởi động các worker...");

        // 1. Khởi động Job Worker (Queue)
        jobWorker = new JobWorker();
        jobWorkerThread = new Thread(jobWorker);
        jobWorkerThread.start(); 

        // 2. Khởi động Scheduler Worker (Định kỳ)
        schedulerWorker = new SchedulerWorker();
        schedulerWorkerThread = new Thread(schedulerWorker);
        schedulerWorkerThread.start();

        System.out.println(">>> Tất cả worker đã được BẬT.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>> Ứng dụng TẮT. Đang dừng các worker...");

        // 1. Dừng Job Worker
        if (jobWorker != null) {
            jobWorker.stop();
        }
        if (jobWorkerThread != null) {
            try {
                jobWorkerThread.join(5000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 2. Dừng Scheduler Worker
        if (schedulerWorker != null) {
            schedulerWorker.stop();
        }
        if (schedulerWorkerThread != null) {
            try {
                schedulerWorkerThread.join(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(">>> Tất cả worker đã DỪNG.");
    }
}