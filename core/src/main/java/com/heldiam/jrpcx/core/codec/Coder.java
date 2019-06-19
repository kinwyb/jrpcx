package com.heldiam.jrpcx.core.codec;

import com.heldiam.jrpcx.core.codec.impl.Json;
import com.heldiam.jrpcx.core.codec.impl.MsgPack;
import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.protocol.*;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 编解码器
 *
 * @author kinwyb
 * @date 2019-06-14 16:46
 **/
public class Coder {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Coder.class.getName());

    private static final Map<Integer, ICodec> _coderMap = new HashMap() {{
        put(SerializeType.JSON.value(), new Json());
        put(SerializeType.MsgPack.value(), new MsgPack());
    }};

    /**
     * 注册编解码器
     *
     * @param tag
     * @param codec
     */
    public static void RegisterCodec(int tag, ICodec codec) {
        _coderMap.put(tag, codec);
    }

    /**
     * 获取编解码器
     *
     * @param tag
     * @return
     */
    public static ICodec getCodec(int tag) {
        return _coderMap.get(tag);
    }

    /**
     * 解析一个结果
     *
     * @param cmd
     * @throws RpcException
     */
    public static ICodec decodeCmd(Command cmd) throws CoderException {
        try {
            cmd.getMessage().decode(cmd.getData());
        } catch (Exception ex) {
            throw new CoderException("原始数据解码异常" + ex.getMessage(), ex);
        }
        ICodec codec = _coderMap.get(cmd.getMessage().getSerializeType().value());
        if (codec == null) {
            throw new CoderException("未知解码器" + cmd.getMessage().getSerializeType().value());
        }
        return codec;
    }

    /**
     * 编码一个请求
     *
     * @param service
     * @param method
     * @param params
     * @param serializeType
     * @param seq
     * @return
     * @throws Exception
     */
    public static Command getRequest(String service, String method, Object params, SerializeType serializeType, long seq) throws CoderException {
        ICodec codec = _coderMap.get(serializeType.value());
        if (codec == null) {
            throw new CoderException("未知解码器:" + serializeType.value());
        }
        Message msg = new Message();
        msg.setMessageType(MessageType.Request);
        msg.setCompressType(CompressType.None);
        msg.setSerializeType(serializeType);
        msg.setSeq(seq);
        msg.servicePath = service;
        msg.serviceMethod = method;
        msg.payload = codec.encode(params);
        return Command.createRequestCommand(msg);
    }
}
