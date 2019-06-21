package com.heldiam.jrpcx.spring;

import com.heldiam.jrpcx.annotation.RpcxService;
import com.heldiam.jrpcx.server.Server;
import com.heldiam.jrpcx.server.Service;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author kinwyb
 * @date 2019-06-20 14:54
 **/
@Component
public class BeanScannerConfigurer implements BeanFactoryPostProcessor,
        ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        RpcxDiscoveryBeanFactory.Processor(beanFactory);
        RpcxServerBeanFactory.Processor(beanFactory);
        RpcxClientBeanFactory.Processor(beanFactory);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent ContextRefreshedEvent) {
        //开始获取注册的服务信息
        Map<String, Object> serviceBeans = applicationContext.getBeansWithAnnotation(RpcxService.class);
        for (Object obj : serviceBeans.values()) {
            RpcxService serviceConfig = obj.getClass().getAnnotation(RpcxService.class);
            if (serviceConfig != null) {
                Service.registerService(obj, serviceConfig.Interface());
            }
        }
        Server server = applicationContext.getBean(Server.class);
        server.Start();
    }

}
