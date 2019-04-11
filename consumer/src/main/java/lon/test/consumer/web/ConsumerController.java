package lon.test.consumer.web;

import lon.test.consumer.config.MyReferenceConfig;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @GetMapping("/testDubbo")
    public String hello(String testField, String testField2, String testField3) {
        try {
            MyReferenceConfig config = MyReferenceConfig.getCacheMyReferenceConfigByKey("lon.test.provider.services.DemoDubboService", "1.0");
            GenericService genericService = config.get();
            HashMap<String, Object> param = new HashMap<>();
            param.put("testField", testField);
            param.put("testField2", testField2);
            param.put("testField3", testField3);
            return genericService.$invoke("testDubboApiO", new String[]{"lon.test.provider.bean.TestBean"}, new Object[]{param}).toString();
        } catch (GenericException ge) {
            return ge.getExceptionMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}