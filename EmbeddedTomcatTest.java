import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class EmbeddedTomcatTest {
    public static void main(String[] args) throws LifecycleException {
        try {
            System.out.println("正在启动嵌入式Tomcat...");
            
            // 创建Tomcat实例
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8081);
            tomcat.setBaseDir("target/tomcat");
            
            // 创建Web应用上下文
            String webappDir = "target/space-knowledge";
            Context context = tomcat.addWebapp("/space-knowledge", new File(webappDir).getAbsolutePath());
            
            // 启动Tomcat
            tomcat.start();
            System.out.println("Tomcat启动成功！");
            System.out.println("访问地址: http://localhost:8081/space-knowledge");
            
            // 等待关闭
            tomcat.getServer().await();
        } catch (Exception e) {
            System.err.println("Tomcat启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
