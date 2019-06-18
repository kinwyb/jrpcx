package com.heldiam.jrpcx.client.selector;

/**
 * 负载均衡模式
 *
 * @author heldiam
 */
public enum SelectorMode {
    //随机
    RandomSelect,
    //轮询
    RoundRobin,
    //加权轮询
    WeightedRoundRobin,
    //权重和ping时间
    WeightedICMP,
    //hash一致
    ConsistentHash,
    //最近的
    Closest
}
