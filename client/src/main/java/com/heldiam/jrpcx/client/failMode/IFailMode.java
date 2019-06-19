package com.heldiam.jrpcx.client.failMode;

import com.heldiam.jrpcx.client.Feature;

/**
 * 失败模式接口
 *
 * @author kinwyb
 * @date 2019-06-19 09:59
 **/
public interface IFailMode {
    /**
     * 重试
     *
     * @param feature 重试对象
     */
    void Retry(Feature feature, String lastExMsg);
}
