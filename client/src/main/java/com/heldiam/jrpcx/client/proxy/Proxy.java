package com.heldiam.jrpcx.client.proxy;

import com.heldiam.jrpcx.client.Client;
import net.sf.cglib.proxy.Enhancer;

/**
 * 代理对象
 *
 * @author heldiam
 */
public class Proxy {

    private final CglibProxy cgp;

    public Proxy(Client client) {
        this.cgp = new CglibProxy(client);
    }

    /**
     * 创建一个代理对象
     *
     * @param <T>
     * @param t
     * @return
     */
    public <T> T createProxy(Class<T> t) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(t);
        enhancer.setCallback(cgp);
        return (T) enhancer.create();
    }

}
