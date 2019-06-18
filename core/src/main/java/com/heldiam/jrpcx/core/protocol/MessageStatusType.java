package com.heldiam.jrpcx.core.protocol;

/**
 * 消息状态
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public enum MessageStatusType {
    /**
     * 一般
     */
    Normal(0),
    /**
     * 错误
     */
    Error(1);


    private final int v;

    MessageStatusType(int v) {
        this.v = v;
    }

    private static MessageStatusType[] values = MessageStatusType.values();
    public static MessageStatusType getValue(int i) {
        return values[i];
    }

    public int value() {
        return v;
    }
}