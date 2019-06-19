package com.heldiam.jrpcx.client.failMode;

import com.heldiam.jrpcx.client.Feature;
import com.heldiam.jrpcx.core.common.RpcException;

/**
 * 失败后会将请求发送到另外一个节点,两个节点任何一个返回就算调用成功
 *
 * @author kinwyb
 * @date 2019-06-19 10:20
 **/
public class FailTry implements IFailMode {

    @Override
    public void Retry(Feature feature, String lastExMsg) {
        if (feature.retryNum > Feature.MaxRetryNUM) { //大于最大请求次数
            feature.setException(new RpcException("[" + feature.seq + "]" + lastExMsg + ",已达重试最大次数"));
            feature.setResult(null);
            return;
        }
        feature.ignoreAddress.clear(); //清空忽略列表
        feature.Call();
    }

}
