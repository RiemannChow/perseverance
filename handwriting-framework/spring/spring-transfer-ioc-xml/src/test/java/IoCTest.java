import com.riemann.dao.AccountDao;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class IoCTest {

    @Test
    public void testXXX() {

        // 通过classpath下的xml文件来启动容器（xml模式SE应用下推荐）
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        // 不推荐使用
        // ApplicationContext applicationContext1 = new FileSystemXmlApplicationContext("文件系统的绝对路径");

        AccountDao accountDao = (AccountDao) applicationContext.getBean("accountDao");

        System.out.println(accountDao);

    }

}
