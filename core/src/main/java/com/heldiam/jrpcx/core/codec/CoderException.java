package com.heldiam.jrpcx.core.codec;

import com.heldiam.jrpcx.core.common.RpcException;

/**
 * 编码解码错误
 *
 * @author kinwyb
 * @date 2019-06-19 10:40
 **/
public class CoderException extends RpcException {

    public CoderException(String message) {
        super(message, "coder");
    }

    public CoderException(String message, Exception ex) {
        super(message, ex, "coder");
    }


    public CoderException(Exception ex) {
        super(ex);
    }


}
