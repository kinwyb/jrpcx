package com.heldiam.jrpcx.client;

import com.heldiam.jrpcx.core.codec.Coder;
import com.heldiam.jrpcx.core.common.QueueChannel;
import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.protocol.Command;
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
public class ConnectHandler extends SimpleChannelInboundHandler<Command> {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectHandler.class.getName());

    public final Coder coder = new Coder();

    /**
     * 执行队列
     */
    private QueueChannel<Command> executeChannel = new QueueChannel<>();

    public ConnectHandler() {
        executeChannel.SubmitRun((r) -> {
            try {
                coder.decodeCmd(r);
            } catch (RpcException e) {
                LOG.error(e.getMessage(), e);
            }
        });
    }

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

}
