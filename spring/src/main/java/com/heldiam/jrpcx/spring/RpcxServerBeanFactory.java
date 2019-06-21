package com.heldiam.jrpcx.spring;

import com.heldiam.jrpcx.annotation.RpcxServer;
import com.heldiam.jrpcx.core.discovery.IDiscovery;
import com.heldiam.jrpcx.server.Server;
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
public class RpcxServerBeanFactory implements ApplicationContextAware, FactoryBean<Server>, DisposableBean {

    private String serverAddress;
    private static final Logger logger = LoggerFactory.getLogger(RpcxServerBeanFactory.class.getName());
    static Server server;

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public static void Processor(ConfigurableListableBeanFactory beanFactory) {
        Map<String, Object> discoveryMap = beanFactory.getBeansWithAnnotation(RpcxServer.class);
        for (Object obj : discoveryMap.values()) {
            RpcxServer server = obj.getClass().getAnnotation(RpcxServer.class);
            if (server != null) {
                logger.info("监测到RpcxServer注解");
                RootBeanDefinition beanDefinition = new RootBeanDefinition();
                beanDefinition.getPropertyValues().add("serverAddress", server.value());
                beanDefinition.setBeanClass(RpcxServerBeanFactory.class);
                ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition("RpcxServer", beanDefinition);
            }
        }
    }

    @Override
    public Server getObject() throws Exception {
        if (RpcxServerBeanFactory.server == null) {
            IDiscovery discovery = applicationContext.getBean(IDiscovery.class);
            RpcxServerBeanFactory.server = new Server(serverAddress, discovery);
        }
        return RpcxServerBeanFactory.server;
    }

    @Override
    public Class<?> getObjectType() {
        return Server.class;
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
        if (server != null) {
            server.Close();
        }
    }
}
