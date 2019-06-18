package com.heldiam.jrpcx.core.protocol;

/**
 * 压缩状态
 * @author kinwyb
 * @date 2019-06-14 16:19s
 */
public enum CompressType {
    /**
     * 不压缩
     */
    None(0),
    /**
     * gzip压缩
     */
    Gzip(1);


    private final int v;

    CompressType(int v) {
        this.v = v;
    }

    private static CompressType[] values = CompressType.values();

    public static CompressType getValue(int i) {
        return values[i];
    }

    public int value() {
        return v;
    }

}
