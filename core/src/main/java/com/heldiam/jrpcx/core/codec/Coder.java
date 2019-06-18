package com.heldiam.jrpcx.core.codec;

import com.heldiam.jrpcx.core.codec.impl.Json;
import com.heldiam.jrpcx.core.codec.impl.MsgPack;
import com.heldiam.jrpcx.core.common.Constants;
import com.heldiam.jrpcx.core.common.Feature;
import com.heldiam.jrpcx.core.common.FeaturePool;
import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.protocol.*;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 编解码器
 *
 * @author kinwyb
 * @date 2019-06-14 16:46
 **/
public class Coder {

    public static AtomicLong seqAtomic = new AtomicLong();

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
    public void decodeCmd(Command cmd) throws RpcException {
        try {
            cmd.getMessage().decode(cmd.getData());
        } catch (Exception ex) {
            throw new RpcException("原始数据解码异常:" + ex.getMessage(), ex, "coder");
        }
        ICodec codec = _coderMap.get(cmd.getMessage().getSerializeType().value());
        if (codec == null) {
            throw new RpcException("未知解码器:" + cmd.getMessage().getSerializeType().value());
        }
        Message msg = cmd.getMessage();
        String id = String.valueOf(msg.getSeq());
        Feature f = FeaturePool.GetUseFeature(id);
        if (f == null) {
            LOG.error("未知请求[" + id + "]结果");
            return;
        }
        if (msg.getMessageStatusType() == MessageStatusType.Error) {
            f.setException(new RpcException("服务调用失败:" + msg.getMetadata().get(Constants.RPCX_ERROR_MESSAGE)));
            f.setResult(null);
            return;
        }
        try {
            Object obj = codec.decode(msg.payload, f.getRetClass());
            f.setResult(obj);
        } catch (Exception ex) {
            f.setException(ex);
            f.setResult(null);
        }
    }

    /**
     * 编码一个请求
     *
     * @param service
     * @param method
     * @param params
     * @param serializeType
     * @param f
     * @return
     * @throws Exception
     */
    public Command getRequest(String service, String method, Object params, SerializeType serializeType, Feature f) throws Exception {
        ICodec codec = _coderMap.get(serializeType.value());
        if (codec == null) {
            throw new RpcException("未知解码器:" + serializeType.value());
        }
        Long seq = seqAtomic.incrementAndGet();
        Message msg = new Message();
        msg.setMessageType(MessageType.Request);
        msg.setCompressType(CompressType.None);
        msg.setSerializeType(serializeType);
        msg.setSeq(seq);
        msg.servicePath = service;
        msg.serviceMethod = method;
        msg.payload = codec.encode(params);
        FeaturePool.AddUseFeature(String.valueOf(seq), f);
        return Command.createRequestCommand(msg);
    }
}
