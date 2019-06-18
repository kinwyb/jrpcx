package com.heldiam.jrpcx.core.protocol;

/**
 * 消息类型
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public enum MessageType {
    /**
     * 请求
     */
    Request(0),

    /**
     * 应答
     */
    Response(1);


    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    private static MessageType[] values = MessageType.values();

    public static MessageType getValue(int i) {
        return values[i];
    }

}