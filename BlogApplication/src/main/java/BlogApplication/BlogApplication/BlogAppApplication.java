package BlogApplication.BlogApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogAppApplication.class, args);
        System.out.println("====================================");
        System.out.println("  Blog App Server Started!");
        System.out.println("  Running at: http://localhost:8080");
        System.out.println("====================================");
    }
}