package com.heldiam.jrpcx.core.common;

/**
 * @author kinwyb
 * @date 2019-06-15 16:32
 **/
public class RpcRuntimeException extends RuntimeException {

    private String code;

    public RpcRuntimeException(String message, String code) {
        super(message);
        this.code = code;
    }

    public RpcRuntimeException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public RpcRuntimeException(Throwable cause) {
        super(cause);
    }

    public RpcRuntimeException(String message) {
        super(message);
    }

    public RpcRuntimeException(Exception ex) {
        super(ex);
    }


    public String getCode() {
        return code;
    }

}
