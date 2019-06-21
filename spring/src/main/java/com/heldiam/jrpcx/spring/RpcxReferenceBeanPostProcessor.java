package com.heldiam.jrpcx.spring;

import com.heldiam.jrpcx.annotation.RpcxReference;
import com.heldiam.jrpcx.client.Client;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author kinwyb
 * @date 2019-06-21 09:21
 **/
@Component
public class RpcxReferenceBeanPostProcessor extends AnnotatedBeanPostProcessor {

    @Override
    protected Object getInjectionObject(Field field, Class<?> beanClass) {
        RpcxReference reference = field.getAnnotation(RpcxReference.class);
        if (reference != null) {
            try { //存在客户端通过客户端读取数据
                Client client = applicationContext.getBean(Client.class);
                return client.Proxy(field.getType());
            } catch (Exception ex) { //客户端信息获取失败,尝试加载本地对象
                return applicationContext.getBean(field.getType());
            }
        }
        return null;
    }

}
