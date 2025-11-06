# Báo cáo & Hướng dẫn Project: NewsAggregator (Web Cào Tin Tức)

## 1. 🧭 Giới thiệu

**NewsAggregator** là một ứng dụng web (xây dựng bằng Java Servlet/JSP) cho phép người dùng đăng ký tài khoản và cá nhân hóa trang tin tức của mình.

Người dùng có thể thêm các "chủ đề" (từ khóa) mà họ quan tâm. Hệ thống sẽ tự động chạy ngầm, quét dữ liệu từ nhiều trang báo (VnExpress, Dân Trí, Tuổi Trẻ) để tìm các bài báo khớp với chủ đề đó và hiển thị trên một trang dashboard duy nhất.

### Các chức năng kỹ thuật nổi bật:

* **Xử lý Đa luồng (Multi-threading):** Sử dụng 2 "Bot" (Threads) chạy ngầm để xử lý việc cào tin tức.
* **Mô hình Hàng đợi (Queue):** Khi người dùng thêm chủ đề mới, yêu cầu được đưa vào hàng đợi (`fetch_jobs`) và được một "Bot Worker" xử lý tức thì (đáp ứng yêuv cầu "tính toán lớn").
* **Mô hình Lập lịch (Scheduler):** Một "Bot Scheduler" tự động chạy định kỳ để tìm tin mới.
* **AJAX (Web 2.0):** Trang dashboard tự động cập nhật khi Bot Worker cào tin xong.
* **Bảo mật:** Mật khẩu được băm (hash) bằng SHA-256; sử dụng Filter để bảo vệ các trang riêng tư.
* **Thiết kế CSDL (Many-to-Many):** Tối ưu hóa CSDL, đảm bảo một bài báo chỉ được lưu 1 lần nhưng có thể được nhiều người theo dõi.

---

## 2. 🏛️ Thiết kế Mô hình MVC (Model-View-Controller)

Project được tổ chức theo mô hình MVC 3-tier (3 lớp) rõ ràng.

### Controller (Bộ điều khiển)

* **Package:** `controller`
* **Chức năng:** Nhận request từ người dùng (HTTP), gọi `BO` và `DAO` để xử lý, sau đó trả kết quả về `View`.
* **Các file chính:**
    * `LoginServlet.java`, `RegisterServlet.java`
    * `DashboardServlet.java`
    * `AddTopicServlet.java`, `DeleteTopicServlet.java`
    * `AdminForceRunServlet.java` (Nút demo)
    * `JobStatusServlet.java` (API cho AJAX)
    * `AuthenticationFilter.java` (Bảo vệ trang)

### View (Giao diện)

* **Package:** `src/main/webapp`
* **Chức năng:** Hiển thị giao diện cho người dùng tương tác.
* **Các file chính:**
    * `login.jsp` (Form đăng nhập)
    * `register.jsp` (Form đăng ký)
    * `dashboard.jsp` (Trang chính)
    * **Thư viện:** `Pico.css`

### Model (Mô hình Dữ liệu & Logic)

Phần Model được chia làm 3 lớp con (Beans, BO, DAO).

* **1. `model.Bean` (POJO/Entity)**
    * **Chức năng:** Các "hộp chứa" dữ liệu thô.
    * **Các file chính:** `User.java`, `Topic.java`, `Article.java`.

* **2. `model.BO` (Business Object - Khối Nghiệp vụ)**
    * **Chức năng:** Chứa "bộ não" và toàn bộ logic xử lý chính của ứng dụng.
    * **Các file chính:**
        * `ScrapingService.java` (Logic cào tin Jsoup từ 3 nguồn báo).
        * `PasswordUtil.java` (Logic băm mật khẩu SHA-256).
        * `JobWorker.java` (Logic Bot 1: Xử lý Queue).
        * `SchedulerWorker.java` (Logic Bot 2: Xử lý định kỳ).
        * `AppContextListener.java` (Khởi động 2 Bot).

* **3. `model.DAO` (Data Access Object - Khối Dữ liệu)**
    * **Chức năng:** Các "tay chân" chỉ làm 1 việc duy nhất: nói chuyện với CSDL (chạy SQL).
    * **Các file chính:**
        * `DBConnection.java` (Cầu nối CSDL).
        * `UserDAO.java`, `TopicDAO.java`, `ArticleDAO.java`, `JobDAO.java`.

---

## 3. ⚙️ Hướng dẫn Cài đặt (Từ A-Z)

Bạn cần 3 thành phần: **Database**, **Server**, và **Code**.

### A. Cài đặt Database (MySQL Server 8.0)

1.  **Tải MySQL:** Truy cập [https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/) và tải file "offline installer".
2.  **Cài đặt:** Chạy file `.msi`. Ở bước "Choosing a Setup Type", chọn **"Custom"**.
3.  **Chọn thành phần:**
    * Chọn `MySQL Server 8.0.x` và `MySQL Workbench 8.x` và bấm `->` để thêm vào.
4.  **Đặt mật khẩu `root`:** Đặt mật khẩu là `123` (hoặc mật khẩu bạn nhớ).
5.  **Tạo CSDL (Database):**
    * Mở **MySQL Workbench**.
    * Kết nối vào server (nhập mật khẩu `root`).
    * Vào `File > Open SQL Script...` và trỏ đến file script SQL của project (hoặc copy-paste script dưới đây) và bấm "Chạy" ⚡.

   
    DROP SCHEMA IF EXISTS news_db;
    CREATE SCHEMA news_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    USE news_db;
    SET FOREIGN_KEY_CHECKS=0;

    CREATE TABLE users (
      id INT NOT NULL AUTO_INCREMENT,
      username VARCHAR(100) NOT NULL,
      password VARCHAR(255) NOT NULL,
      PRIMARY KEY (id),
      UNIQUE INDEX username_UNIQUE (username ASC)
    ) ENGINE = InnoDB;

    CREATE TABLE topics (
      id INT NOT NULL AUTO_INCREMENT,
      user_id INT NOT NULL,
      keyword VARCHAR(255) NOT NULL,
      PRIMARY KEY (id),
      INDEX fk_topics_users_idx (user_id ASC),
      CONSTRAINT fk_topics_users
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
    ) ENGINE = InnoDB;

    CREATE TABLE articles (
      id INT NOT NULL AUTO_INCREMENT,
      title TEXT NOT NULL,
      description TEXT NULL,
      link VARCHAR(512) NOT NULL,
      scraped_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      PRIMARY KEY (id),
      UNIQUE INDEX link_UNIQUE (link ASC)
    ) ENGINE = InnoDB;

    CREATE TABLE topic_articles (
      topic_id INT NOT NULL,
      article_id INT NOT NULL,
      PRIMARY KEY (topic_id, article_id),
      INDEX fk_ta_articles_idx (article_id ASC),
      CONSTRAINT fk_ta_topics
        FOREIGN KEY (topic_id) REFERENCES topics(id)
        ON DELETE CASCADE,
      CONSTRAINT fk_ta_articles
        FOREIGN KEY (article_id) REFERENCES articles(id)
        ON DELETE CASCADE
    ) ENGINE = InnoDB;

    CREATE TABLE fetch_jobs (
      id INT NOT NULL AUTO_INCREMENT,
      topic_id INT NOT NULL,
      status VARCHAR(20) NOT NULL DEFAULT 'pending',
      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
      is_viewed TINYINT(1) NOT NULL DEFAULT 0,
      PRIMARY KEY (id),
      INDEX fk_jobs_topics_idx (topic_id ASC),
      CONSTRAINT fk_jobs_topics
        FOREIGN KEY (topic_id) REFERENCES topics(id)
        ON DELETE CASCADE
    ) ENGINE = InnoDB;

    SET FOREIGN_KEY_CHECKS=1; 
    COMMIT;

### B. Cài đặt Server (Apache Tomcat 9)

1.  **Tải Tomcat 9:** Truy cập [https://tomcat.apache.org/download-90.cgi](https://tomcat.apache.org/download-90.cgi).
2.  Tải file "64-bit Windows zip" trong mục "Core".
3.  **Giải nén:** Giải nén file `.zip` vào một thư mục (ví dụ: `C:\tomcat9`).

### C. Cài đặt Project (Eclipse)

1.  **Tải Eclipse:** Tải **"Eclipse IDE for Enterprise Java and Web Developers"**.
2.  **Import Project:** Mở Eclipse, `File` > `Import...` > `General` > `Existing Projects into Workspace`. Trỏ đến thư mục `NewsAggregator`.
3.  **Kiểm tra thư viện:** Project đã bao gồm sẵn các file `.jar` (MySQL Connector, Jsoup) trong `webapp/WEB-INF/lib`.
4.  **Cấu hình Mật khẩu CSDL (Rất quan trọng):**
    * Nếu mật khẩu `root` MySQL của bạn **không phải** là `123`, hãy mở file:
        `model/DAO/DBConnection.java`
    * Sửa lại dòng `private static final String DB_PASSWORD = "123";` thành mật khẩu của bạn.
5.  **Kết nối Eclipse với Tomcat:**
    * Mở tab "Servers" (`Window > Show View > Servers`).
    * Bấm "Click this link to create a new server...".
    * Chọn `Apache` > `Tomcat v9.0 Server`.
    * Bấm `Next`, trỏ đến thư mục `C:\tomcat9`.
    * Bấm `Finish`.
6.  **Deploy Project:**
    * Chuột phải vào server "Tomcat v9.0" (trong tab "Servers") -> "Add and Remove...".
    * Chọn `NewsAggregator` và bấm "Add >".
    * Bấm `Finish`.
7.  **Chạy:**
    * Chuột phải vào server "Tomcat v9.0" -> **"Start"**.
    * Mở trình duyệt và truy cập: `http://localhost:8080/NewsAggregator/register.jsp`

---

## 4. 📖 Hướng dẫn Sử dụng (Các Chức năng)

### Kịch bản 1: Đăng ký & Đăng nhập

1.  Truy cập `.../register.jsp`.
2.  Thử nhập mật khẩu `12345` (dưới 6 ký tự) -> Hệ thống báo lỗi.
3.  Thử nhập `123456` và `123457` -> Hệ thống báo lỗi "Mật khẩu không khớp."
4.  Tạo tài khoản `demo` / `123456` / `123456` -> Thành công, chuyển về trang `login`.
5.  Đăng nhập `demo` / `123456` -> Thành công, vào trang "Bảng điều khiển".

### Kịch bản 2: Bảo mật (Filter)

1.  Bấm link "Đăng xuất" -> Quay về trang `login`.
2.  Thử truy cập thẳng vào link `.../dashboard`.
3.  **Kết quả:** Bạn sẽ tự động bị "đá" về trang `login.jsp`.

### Kịch bản 3: Chức năng Cào tin (Queue & AJAX)

1.  Đăng nhập bằng tài khoản `demo` (bảng tin đang trống).
2.  Gõ "Bão" vào ô "Từ khóa" và bấm "Thêm".
3.  **Không làm gì cả.** Chờ 5-10 giây.
4.  **Kết quả:** Trang web sẽ **tự động tải lại**. Bảng "Tin tức" xuất hiện các bài báo về "Bão" (Chứng tỏ `JobWorker` và `AJAX` hoạt động).

### Kịch bản 4: Chức năng Cào tin (Admin)

1.  Gõ "Bóng đá" vào ô "Từ khóa" và bấm "Thêm".
2.  Bấm vào link `Quét ngay`.
3.  **Kết quả:** Trang web tải lại, bảng "Tin tức" cập nhật thêm các bài báo về "Bóng đá" từ cả 3 nguồn.

---
## 5. 🧑‍💻 Thông tin Tác giả

* **Họ và tên:** `Đặng Hoàng Huy`
* **Mã số sinh viên:** `102230349`
* **Lớp:** `23T_DT4`
* **Email:** `danghoangdanghoang2018@gmail.com`
* **GitHub:** `justinbiahoi05`