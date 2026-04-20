import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpringConfig {
    public static void main(String[] args) {
        try {
            System.out.println("正在加载Spring配置...");
            ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring/applicationContext.xml", "spring/spring-mvc.xml"
            );
            System.out.println("Spring配置加载成功！");
            System.out.println("容器中的Bean数量: " + context.getBeanDefinitionCount());
            context.close();
        } catch (Exception e) {
            System.err.println("Spring配置加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
