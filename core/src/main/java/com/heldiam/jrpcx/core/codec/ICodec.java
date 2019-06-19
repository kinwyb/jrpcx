package com.heldiam.jrpcx.core.codec;


import com.heldiam.jrpcx.core.common.RpcException;

/**
 * Codec数据解析接口
 *
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public interface ICodec {

    /**
     * 解码
     *
     * @return
     */
    Object decode(byte[] data, Class retType) throws CoderException;

    /**
     * 编码
     *
     * @param params
     * @return
     * @throws RpcException
     */
    byte[] encode(Object params) throws CoderException;

}
