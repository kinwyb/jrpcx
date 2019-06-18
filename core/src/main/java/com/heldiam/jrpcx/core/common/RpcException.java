package com.heldiam.jrpcx.core.common;

/**
 * @date 2019-06-14 16:18
 * @author kinwyb
 */
public class RpcException extends Exception {

    private String code;

    public RpcException(String message, String code) {
        super(message);
        this.code = code;
    }

    public RpcException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Exception ex) {
        super(ex);
    }


    public String getCode() {
        return code;
    }

}
