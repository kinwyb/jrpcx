package com.heldiam.jrpcx.server;

import com.heldiam.jrpcx.core.codec.Coder;
import com.heldiam.jrpcx.core.codec.ICodec;
import com.heldiam.jrpcx.core.protocol.Command;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
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

    private EventLoopGroup workerGroup;
    private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    ConnectHandler() {
        workerGroup = new NioEventLoopGroup();
    }

    /**
     * 关闭
     */
    void Close() {
        workerGroup.shutdownGracefully();
        channels.close().awaitUninterruptibly();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("接收到客户端连接: " + ctx.channel().remoteAddress());
        channels.add(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext chc, Command msg) throws Exception {
        workerGroup.execute(() -> {
            Command cmd;
            try {
                msg.getMessage().decode(msg.getData()); //解码信息
                LOG.debug(msg.getMessage().getSeq() + "收到服务请求:" + msg.getMessage().servicePath + "." + msg.getMessage().serviceMethod);
                ICodec codec = Coder.getCodec(msg.getMessage().getSerializeType().value());
                if (codec == null) {
                    cmd = msg.requestToResponse();
                    cmd.setErrorMessage("Codec", "解码器不存在");
                } else {
                    try {
                        Object resp = Service.ServiceInvoke(msg.getMessage(), codec);
                        cmd = msg.requestToResponse();
                        cmd.getMessage().payload = codec.encode(resp);
                    } catch (Exception ex) {
                        LOG.error("服务调用异常", ex);
                        cmd = msg.requestToResponse();
                        cmd.setErrorMessage("服务异常", ex.getMessage());
                    }
                }
                LOG.debug(msg.getMessage().getSeq() + "服务处理完成了");
            } catch (Exception ex) {
                cmd = msg.requestToResponse();
                cmd.setErrorMessage("服务异常", ex.getMessage());
            }
            if (cmd != null && chc.channel().isActive()) {
                chc.writeAndFlush(cmd); //写入返回
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOG.warn("断开客户端连接");
        channels.remove(ctx.channel());
        ctx.channel().close();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("连接异常", cause);
        super.exceptionCaught(ctx, cause);
        channels.remove(ctx.channel());
        ctx.channel().close();
        ctx.close();
    }

}
