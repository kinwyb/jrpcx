package com.heldiam.jrpcx.client.selector;

import com.heldiam.jrpcx.core.common.URL;

import java.util.Date;
import java.util.List;

/**
 * 随机选择器
 *
 * @author kinwyb
 * @date 2019-06-17 09:25
 **/
public class Random implements ISelector {

    private final java.util.Random random = new java.util.Random(new Date().getTime());

    private final SelectorMode mode = SelectorMode.RandomSelect;

    @Override
    public URL GetAddress(String serviceName, List<String> address) {
        return URL.valueOf(address.get(random.nextInt(address.size())));
    }

    /**
     * 获取选择器模式
     *
     * @return
     */
    @Override
    public SelectorMode getMode() {
        return mode;
    }

}