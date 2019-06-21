package com.heldiam.jrpcx.spring;

import com.heldiam.jrpcx.annotation.RpcxDiscovery;
import com.heldiam.jrpcx.core.discovery.IDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * @author kinwyb
 * @date 2019-06-21 13:02
 **/
public class RpcxDiscoveryBeanFactory implements FactoryBean<IDiscovery> {

    private RpcxDiscovery discovery;
    private static final Logger logger = LoggerFactory.getLogger(RpcxDiscoveryBeanFactory.class.getName());

    public static void Processor(ConfigurableListableBeanFactory beanFactory) {
        Map<String, Object> discoveryMap = beanFactory.getBeansWithAnnotation(RpcxDiscovery.class);
        for (Object obj : discoveryMap.values()) {
            RpcxDiscovery discovery = obj.getClass().getAnnotation(RpcxDiscovery.class);
            if (discovery != null) {
                logger.info("监测到RpcxDiscovery注解");
                RootBeanDefinition beanDefinition = new RootBeanDefinition();
                beanDefinition.getPropertyValues().add("discovery", discovery);
                beanDefinition.setBeanClass(RpcxDiscoveryBeanFactory.class);
                ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition("IDiscovery", beanDefinition);
            }
        }
    }

    public void setDiscovery(RpcxDiscovery discovery) {
        this.discovery = discovery;
    }

    @Override
    public IDiscovery getObject() throws Exception {
        Constructor cs = discovery.cls().getDeclaredConstructor(String[].class);
        IDiscovery ret = (IDiscovery) cs.newInstance(new Object[]{discovery.value()});
        ret.setBasePath(discovery.basePath());
        return ret;
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(discovery.cls());
//        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
//        enhancer.setCallback(new MethodInterceptorImpl());
//        return (IDiscovery) enhancer.create(new Class[]{String[].class}, new Object[]{discovery.value()});
    }

    @Override
    public Class<?> getObjectType() {
        if (discovery != null) {
            return IDiscovery.class;
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
