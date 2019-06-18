package com.heldiam.jrpcx.client.selector;

import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.core.common.URL;
import com.heldiam.jrpcx.core.discovery.IDiscovery;
import com.sun.istack.internal.NotNull;

/**
 * @author kinwyb
 * @date 2019-06-17 09:19
 **/
public class Selector {

    private ISelector selector;
    private IDiscovery discovery;

    private Selector(@NotNull IDiscovery discovery, ISelector selector) {
        this.discovery = discovery;
        this.selector = selector;
    }

    /**
     * 随机选择器
     *
     * @param discovery
     * @return
     */
    public static Selector Random(@NotNull IDiscovery discovery) {
        return new Selector(discovery, new Random());
    }

    /**
     * 轮巡
     *
     * @param discovery
     * @return
     */
    public static Selector RoundRobin(@NotNull IDiscovery discovery) {
        return new Selector(discovery, new Random());
    }

    public static Selector WeightedRountRobin(@NotNull IDiscovery discovery) {
        return new Selector(discovery, new WeightedRountRobin(discovery));
    }

    /**
     * 设置选择模式
     *
     * @param mode
     */
    public void setMode(SelectorMode mode) {
        switch (mode) {
            case RandomSelect:
                this.selector = new Random();
            case RoundRobin:
                this.selector = new RoundRobin();
            case WeightedRoundRobin:
                this.selector = new WeightedRountRobin(discovery);
        }
    }

    /**
     * 获取选择模式
     *
     * @return
     */
    public SelectorMode getMode() {
        return this.selector.getMode();
    }

    public URL GetAddress(String serviceName) {
        return this.selector.GetAddress(serviceName, this.discovery.getServices(serviceName));
    }

    /**
     * 构建客户端
     *
     * @return
     */
    public Client buildClient() {
        return new Client(this,discovery);
    }

}
