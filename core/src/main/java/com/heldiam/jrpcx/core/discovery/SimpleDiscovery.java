package com.heldiam.jrpcx.core.discovery;

import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * 简单注册中心
 *
 * @author kinwyb
 * @date 2019-06-17 09:10
 **/
public class SimpleDiscovery extends BaseDiscovery {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(SimpleDiscovery.class.getName());

    private List<String> address = new LinkedList<>();

    @Override
    public List<String> getServices(String ServiceName) {
        return address;
    }

    public SimpleDiscovery(String... address) {
        for (String addr : address) {
            this.address.add(addr);
        }
        serviceMap.put(null, this.address);
    }

    @Override
    public void RegisterWatch(IDiscoveryWatch watch) {
        super.RegisterWatch(watch);
        watch.ServiceChange(serviceMap);
    }

    /**
     * 新增一个服务
     *
     * @param address
     */
    public void AddServer(String address) {
        this.address.add(address);
        super.ServiceChange(serviceMap);
    }

}
