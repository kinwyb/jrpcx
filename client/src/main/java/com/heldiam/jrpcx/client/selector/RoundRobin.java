package com.heldiam.jrpcx.client.selector;

import com.heldiam.jrpcx.core.common.URL;

import java.util.List;

/**
 * @author kinwyb
 * @date 2019-06-17 09:53
 **/
public class RoundRobin implements ISelector {

    public int current = 0;

    @Override
    public URL GetAddress(String serviceName, List<String> address) {
        current++;
        current = current % address.size();
        URL url = URL.valueOf(address.get(current));
        return url;
    }

    @Override
    public SelectorMode getMode() {
        return SelectorMode.RoundRobin;
    }
}
