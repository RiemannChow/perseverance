import com.riemann.AutodeliverApplication8090;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest(classes = {AutodeliverApplication8090.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AutodeliverApplication8090Test {


    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void testInstanceMetadata() {
        List<ServiceInstance> instances = discoveryClient.getInstances("lagou-service-resume");
        for (int i = 0; i < instances.size(); i++) {
            ServiceInstance serviceInstance =  instances.get(i);
            System.out.println(serviceInstance);
        }
    }


    @Test
    public void testRibbon() {
        // 客户端面对的其实是服务了----lagou-service-resume，具体访问到哪个实例是由ribbon决定的
        String url = "http://lagou-service-resume/resume/openstate/1545132";
        // 调用远程服务—> 简历微服务接口  RestTemplate  -> JdbcTempate
        // httpclient封装好多内容进行远程调用
        Integer forObject = restTemplate.getForObject(url, Integer.class);
        System.out.println("=====》》》使用ribbon负载均衡访问，访问的服务实例的端口号：" + forObject);
    }
}
