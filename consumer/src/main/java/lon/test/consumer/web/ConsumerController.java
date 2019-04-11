package lon.test.consumer.web;

import lon.test.provider.bean.TestBean;
import lon.test.provider.services.DemoDubboService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Reference(version = "1.0")
    private DemoDubboService demoDubboService;

    @GetMapping("/testDubbo")
    public String hello(TestBean testBean) {
        try {
            return demoDubboService.testDubboApiO(testBean);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}