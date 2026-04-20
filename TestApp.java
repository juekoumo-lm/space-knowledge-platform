import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.space.knowledge")
public class TestApp {
    public static void main(String[] args) {
        try {
            System.out.println("正在启动应用...");
            SpringApplication.run(TestApp.class, args);
        } catch (Exception e) {
            System.err.println("应用启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
