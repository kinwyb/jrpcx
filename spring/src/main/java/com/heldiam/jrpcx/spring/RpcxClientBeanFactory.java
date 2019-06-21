package com.heldiam.jrpcx.spring;

import com.heldiam.jrpcx.annotation.RpcxClient;
import com.heldiam.jrpcx.annotation.RpcxServer;
import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.discovery.IDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author kinwyb
 * @date 2019-06-21 13:43
 **/
public class RpcxClientBeanFactory implements ApplicationContextAware, FactoryBean<Client>, DisposableBean {

    private RpcxClient clientConfig;
    private static final Logger logger = LoggerFactory.getLogger(RpcxClientBeanFactory.class.getName());
    static Client client;

    public void setClientConfig(RpcxClient clientConfig) {
        this.clientConfig = clientConfig;
    }

    public static void Processor(ConfigurableListableBeanFactory beanFactory) {
        Map<String, Object> discoveryMap = beanFactory.getBeansWithAnnotation(RpcxServer.class);
        for (Object obj : discoveryMap.values()) {
            RpcxClient clientConfig = obj.getClass().getAnnotation(RpcxClient.class);
            if (clientConfig != null) {
                logger.info("监测到RpcxClient注解");
                RootBeanDefinition beanDefinition = new RootBeanDefinition();
                beanDefinition.getPropertyValues().add("clientConfig", clientConfig);
                beanDefinition.setBeanClass(RpcxClientBeanFactory.class);
                ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition("RpcxClient", beanDefinition);
            }
        }
    }

    @Override
    public Client getObject() throws Exception {
        if (RpcxClientBeanFactory.client == null) {
            IDiscovery discovery = applicationContext.getBean(IDiscovery.class);
            Selector selector;
            switch (clientConfig.selectorMode()) {
                case RandomSelect:
                    selector = Selector.Random(discovery);
                    break;
                case WeightedRoundRobin:
                    selector = Selector.WeightedRountRobin(discovery);
                    break;
                default:
                    selector = Selector.RoundRobin(discovery);
            }
            RpcxClientBeanFactory.client = selector.buildClient();
            client.setFailMode(clientConfig.failMode());
        }
        return RpcxClientBeanFactory.client;
    }

    @Override
    public Class<?> getObjectType() {
        return Client.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        if (client != null) {
            client.Close();
        }
    }
}
