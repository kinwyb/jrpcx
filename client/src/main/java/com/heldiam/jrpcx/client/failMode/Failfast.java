package com.heldiam.jrpcx.client.failMode;

import com.heldiam.jrpcx.client.Feature;
import com.heldiam.jrpcx.core.common.RpcException;

/**
 * 一旦调用一个节点失败， 立即会返回错误
 *
 * @author kinwyb
 * @date 2019-06-19 09:59
 **/
class Failfast implements IFailMode {

    @Override
    public void Retry(Feature feature, String lastExMsg) {
        feature.setException(new RpcException("[" + feature.seq + "]" + lastExMsg));
        feature.setResult(null);
    }

}
