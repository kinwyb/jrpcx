package com.heldiam.jrpcx.client.failMode;

import com.heldiam.jrpcx.client.Feature;
import com.heldiam.jrpcx.core.common.RpcException;

/**
 * 失败后调用另外一个节点,直到服务节点能正常返回信息,或者达到最大的重试次数
 * 最大次数设置 Feature.MaxRetryNUM
 *
 * @author kinwyb
 * @date 2019-06-19 10:16
 **/
public class FailOver implements IFailMode {

    @Override
    public void Retry(Feature feature, String lastExMsg) {
        if (feature.retryNum > Feature.MaxRetryNUM) { //大于最大请求次数
            feature.setException(new RpcException("[" + feature.seq + "]" + lastExMsg + ",已达重试最大次数"));
            feature.setResult(null);
            return;
        }
        feature.Call();
    }

}
