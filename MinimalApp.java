import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MinimalApp {
    public static void main(String[] args) {
        try {
            System.out.println("正在加载Spring配置...");
            
            // 只加载Spring MVC配置，不加载数据库配置
            ApplicationContext context = new ClassPathXmlApplicationContext(
                "spring/spring-mvc.xml"
            );
            
            System.out.println("Spring配置加载成功！");
            System.out.println("容器中的Bean数量: " + context.getBeanDefinitionCount());
            
            // 打印所有Bean的名称
            String[] beanNames = context.getBeanDefinitionNames();
            System.out.println("容器中的Bean:");
            for (String beanName : beanNames) {
                System.out.println("  - " + beanName);
            }
            
            context.close();
        } catch (Exception e) {
            System.err.println("Spring配置加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
