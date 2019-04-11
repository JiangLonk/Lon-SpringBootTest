package lon.test.consumer.config;

import lon.test.consumer.utils.ApplicationContextUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author : wangzhiyong
 * @date : 2018/8/6 15:03
 * description : 动态调用dubbo配置
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Configuration("myReferenceConfig")
public class MyReferenceConfig extends ReferenceConfig<GenericService> {

    private static final long serialVersionUID = 1L;
    private static final Boolean CHECK = false;
    private static final Integer TIMEOUT = 300000;
    @Value("${dubbo.application.name:chaofeng-server}")
    private static String applicationName;
    @Value("${dubbo.registry.protocol:zookeeper}")
    private static String protocol;
    @Value("${dubbo.registry.address:}")
    private static String address;
    /**
     * 缓存配置文件
     */
    private static ConcurrentMap<String, MyReferenceConfig> CACHE_HOLDER = new ConcurrentHashMap<>();

    public MyReferenceConfig() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName(applicationName);
        this.setApplication(application);
        RegistryConfig registryConfig = new RegistryConfig(address);
        registryConfig.setProtocol(protocol);
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setCheck(CHECK);
        consumerConfig.setTimeout(TIMEOUT);
        this.setRegistry(registryConfig);
        this.setConsumer(consumerConfig);
    }

    /**
     * 获取缓存的MyReferenceConfig
     *
     * @param interfaceName 接口名称
     * @param version       版本
     * @return
     */
    public static MyReferenceConfig getCacheMyReferenceConfigByKey(String interfaceName, String version) {
        String key = interfaceName + ":" + version;
        MyReferenceConfig referenceConfig;
        if (CACHE_HOLDER.containsKey(key)) {
            referenceConfig = CACHE_HOLDER.get(key);
        } else {
            referenceConfig = ApplicationContextUtils.getBean("myReferenceConfig", MyReferenceConfig.class);
            referenceConfig.setId(interfaceName);
            // 弱类型接口名
            referenceConfig.setInterface(interfaceName);
            // 设置版本
            referenceConfig.setVersion(version);
            // 声明为泛化接口
            referenceConfig.setGeneric(true);
            CACHE_HOLDER.putIfAbsent(key, referenceConfig);
        }
        return referenceConfig;
    }
}
