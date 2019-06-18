package com.heldiam.jrpcx.core.codec;

/**
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public enum DecoderState {
    /**
     * 魔术值
     */
    MagicNumber,
    /**
     * 包头
     */
    Header,
    /**
     * 包体
     */
    Body,
}
