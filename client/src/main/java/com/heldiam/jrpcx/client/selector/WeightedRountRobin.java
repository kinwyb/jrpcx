package com.heldiam.jrpcx.client.selector;

import com.heldiam.jrpcx.core.common.StringUtils;
import com.heldiam.jrpcx.core.common.URL;
import com.heldiam.jrpcx.core.discovery.IDiscovery;
import com.heldiam.jrpcx.core.discovery.IDiscoveryWatch;
import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author kinwyb
 * @date 2019-06-17 09:58
 **/
public class WeightedRountRobin implements ISelector, IDiscoveryWatch {

    private Map<String, List<Weighted>> weighteds = new HashMap<>();

    private IDiscovery discovery;

    private static final Logger LOG = LoggerFactory.getLogger(WeightedRountRobin.class.getName());


    public WeightedRountRobin(@NotNull IDiscovery discovery) {
        this.discovery = discovery;
        this.discovery.RegisterWatch(this); //注册监听
    }

    private boolean contains(URL url) {
        String weight = url.getParameter("weight");
        String address = url.getAddress();
        List<Weighted> weighteds = this.weighteds.get(url.getServiceInterface());
        if (weighteds == null || weighteds.size() < 1) {
            return false;
        }
        return weighteds.stream().anyMatch((it) -> {
            if (it.getServerAddress().equals(address) && it.getWeight() == Integer.parseInt(weight)) {
                return true;
            }
            return false;
        });
    }

    private void parseURL(String serviceName, List<String> serviceList) {
        List<Weighted> weighteds = this.weighteds.get(serviceName);
        if (weighteds == null) {
            weighteds = new LinkedList<>();
            this.weighteds.put(serviceName, weighteds);
        } else {
            weighteds.clear();
        }
        for (String it : serviceList) {
            URL url = URL.valueOf(it);
            if (!contains(url)) {
                String weightString = url.getParameter("weight","100");
                Weighted wg = new Weighted(url.getAddress(), Integer.parseInt(weightString));
                wg.setUrl(url);
                weighteds.add(wg);
            }
        }
    }

    public Weighted nextWeighted(String serviceName) {
        List<Weighted> weighteds = this.weighteds.get(serviceName);
        if (weighteds == null || weighteds.size() < 1) {
            if (!StringUtils.isBlank(serviceName)) {
                return nextWeighted(null); // 空字符串表示支持所有服务
            }
            return null;
        }
        Weighted best = null;
        int total = 0;
        for (int i = 0; i < weighteds.size(); i++) {
            Weighted w = weighteds.get(i);
            if (w == null) {
                continue;
            }
            //if w is down, continue
            w.currentWeight += w.effectiveWeight;
            total += w.effectiveWeight;
            if (w.effectiveWeight < w.weight) {
                w.effectiveWeight++;
            }
            if (best == null || w.currentWeight > best.currentWeight) {
                best = w;
            }
        }
        if (best == null) {
            return null;
        }
        best.currentWeight -= total;
        return best;
    }

    @Override
    public URL GetAddress(String serviceName, List<String> address) {
        Weighted weighted = nextWeighted(serviceName);
        if (weighted == null) {
            return null;
        }
        return weighted.getUrl();
    }

    @Override
    public SelectorMode getMode() {
        return SelectorMode.WeightedRoundRobin;
    }

    @Override
    public void ServiceChange(Map<String, List<String>> serviceMap) {
        LOG.debug("WeightedRountRobin 服务列表变动");
        serviceMap.forEach((k, v) -> parseURL(k, v));
    }

    @Override
    public void RemoveService(String serviceName, String serverAddress) {
        LOG.debug("WeightedRountRobin 服务移除：" + serviceName + " => " + serverAddress);
        List<Weighted> weighteds = this.weighteds.get(serviceName);
        if (weighteds == null || weighteds.size() < 1) {
            return;
        }
        URL url = URL.valueOf(serverAddress);
        String weight = url.getParameter("weight", "100");
        String address = url.getAddress();
        weighteds.removeIf((it) -> {
            if (it.getServerAddress().equals(address) && it.getWeight() == Integer.parseInt(weight)) {
                return true;
            }
            return false;
        });
    }

    /**
     * 权重
     */
    private class Weighted {

        public SocketAddress server;
        public int weight;
        public int currentWeight;
        public int effectiveWeight;
        public String serverAddress;
        public URL url;

        public Weighted(String server, int weight) {
            this.serverAddress = server;
            this.server = IDiscovery.parseSocketAddress(server);
            this.weight = weight;
        }

        public String getServerAddress() {
            return serverAddress;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        public URL getUrl() {
            return url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }

        public SocketAddress getServer() {
            return server;
        }

        public void setServer(SocketAddress server) {
            this.server = server;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getCurrentWeight() {
            return currentWeight;
        }

        public void setCurrentWeight(int currentWeight) {
            this.currentWeight = currentWeight;
        }

        public int getEffectiveWeight() {
            return effectiveWeight;
        }

        public void setEffectiveWeight(int effectiveWeight) {
            this.effectiveWeight = effectiveWeight;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Weighted) {
                if (server.toString().equals(((Weighted) obj).server.toString()) &&
                        getWeight() == ((Weighted) obj).getWeight()) {
                    return true;
                }
            }
            return false;
        }
    }

}
