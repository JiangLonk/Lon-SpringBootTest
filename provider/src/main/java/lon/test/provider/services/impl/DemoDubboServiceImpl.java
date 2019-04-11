package lon.test.provider.services.impl;

import lon.test.provider.bean.TestBean;
import lon.test.provider.services.DemoDubboService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

@Service(version = "1.0")
@Component
public class DemoDubboServiceImpl implements DemoDubboService {

    @Override
    public String testDubboApiO(TestBean testBean) {
        return "param:" + testBean.toString() + ";";
    }
}
