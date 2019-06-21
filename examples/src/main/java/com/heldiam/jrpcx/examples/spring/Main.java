package com.heldiam.jrpcx.examples.spring;

import com.heldiam.jrpcx.annotation.RpcxClient;
import com.heldiam.jrpcx.annotation.RpcxDiscovery;
import com.heldiam.jrpcx.annotation.RpcxServer;
import org.apache.log4j.BasicConfigurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author kinwyb
 * @date 2019-06-20 15:36
 **/
@ComponentScan(value = {"com.heldiam"})
@RpcxDiscovery //开启注册中心(必须)
@RpcxServer //开启服务
@RpcxClient //开启客户端
public class Main {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.register(Main.class);
        annotationConfigApplicationContext.refresh();
        SimpleComponent component = annotationConfigApplicationContext.getBean(SimpleComponent.class);
        System.out.println(component.getData());
        annotationConfigApplicationContext.close();
    }

}
