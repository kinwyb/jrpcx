package com.heldiam.jrpcx.client.failMode;

import com.heldiam.jrpcx.client.Feature;
import com.heldiam.jrpcx.core.common.RpcException;

/**
 * @author kinwyb
 * @date 2019-06-19 10:22
 **/
public class FailBackup implements IFailMode {
    @Override
    public void Retry(Feature feature, String lastExMsg) {
        if (feature.retryNum > 1) { //尝试另外一个节点也不行的，返回错误
            feature.setException(new RpcException("[" + feature.seq + "]" + lastExMsg + ",已达重试最大次数"));
            feature.setResult(null);
            return;
        }
        feature.Call();
    }
}
