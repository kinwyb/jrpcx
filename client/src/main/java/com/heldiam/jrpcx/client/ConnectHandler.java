package com.heldiam.jrpcx.client;

import com.heldiam.jrpcx.core.codec.Coder;
import com.heldiam.jrpcx.core.codec.ICodec;
import com.heldiam.jrpcx.core.common.*;
import com.heldiam.jrpcx.core.protocol.Command;
import com.heldiam.jrpcx.core.protocol.Message;
import com.heldiam.jrpcx.core.protocol.MessageStatusType;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 连接处理类
 *
 * @author heldiam
 */
@Sharable
public class ConnectHandler extends SimpleChannelInboundHandler<Command> implements QueueChannel.QueueChannelRun<Command> {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectHandler.class.getName());

    public static int ConnectHandlerThread = 10;

    /**
     * 执行队列
     */
    private QueueChannel<Command> executeChannel = new QueueChannel(this, ConnectHandlerThread);

    /**
     * 关闭
     */
    public void Close() {
        executeChannel.Close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, Command msg) throws Exception {
        executeChannel.Put(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOG.warn("与服务器断开连接");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("连接异常", cause);
    }

    @Override
    public void Run(Command cmd) {
        try {
            ICodec codec = Coder.decodeCmd(cmd);
            Message msg = cmd.getMessage();
            String id = String.valueOf(msg.getSeq());
            Feature f = FeaturePool.GetUseFeature(id);
            if (f == null) {
//                LOG.error("未知请求[" + id + "]结果");
                return;
            }
            f.setMetaData(msg.getMetadata());
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
        } catch (RpcException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
