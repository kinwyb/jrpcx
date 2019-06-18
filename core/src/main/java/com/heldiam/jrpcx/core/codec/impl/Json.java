package com.heldiam.jrpcx.core.codec.impl;

import com.google.gson.Gson;
import com.heldiam.jrpcx.core.codec.ICodec;
import com.heldiam.jrpcx.core.common.Constants;
import com.heldiam.jrpcx.core.common.RpcException;

import java.io.UnsupportedEncodingException;

/**
 * @author kinwyb
 * @date 2019-06-14 16:49
 **/
public class Json implements ICodec {

    static final Gson GSON = new Gson();

    @Override
    public Object decode(byte[] data, Class retType) throws RpcException {
        try {
            return GSON.fromJson(new String(data, Constants.CharsetName), retType);
        } catch (UnsupportedEncodingException e) {
            throw new RpcException("Json bytes2String 转换失败");
        }
    }

    @Override
    public byte[] encode(Object params) throws RpcException {
        try {
            return GSON.toJson(params).getBytes(Constants.CharsetName);
        } catch (UnsupportedEncodingException e) {
            throw new RpcException("Json String2bytes 转换失败");
        }
    }
}
