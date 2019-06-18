package com.heldiam.jrpcx.server;

import com.heldiam.jrpcx.core.codec.NettyDecoder;
import com.heldiam.jrpcx.core.codec.NettyEncoder;
import com.heldiam.jrpcx.core.common.RpcException;
import com.heldiam.jrpcx.core.discovery.IDiscovery;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Arrays;

/**
 * 服务器
 *
 * @author heldiam
 */
public class Server {

    private final SocketAddress remote;
    private final String address;
    private static final Logger LOG = LoggerFactory.getLogger(Server.class.getName());
    private ConnectHandler handler;
    private Channel channel;
    private IDiscovery discovery;

    public Server(String address, IDiscovery discovery) {
        this.address = address;
        this.remote = IDiscovery.parseSocketAddress(address);
        this.discovery = discovery;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.Close()));
    }

    /**
     * 连接服务器
     */
    public void Start() {
        LOG.debug("RPCX服务启动中...");
        registerService(); //注册服务
        try {
            handler = new ConnectHandler();
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                //create ServerBootstrap instance
                ServerBootstrap b = new ServerBootstrap();
                b.group(group).channel(NioServerSocketChannel.class);
                b.childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new NettyDecoder());
                        ch.pipeline().addLast(new NettyEncoder());
                        ch.pipeline().addLast(handler);
                    }
                });
                //Binds Server, waits for Server to close, and releases resources
                ChannelFuture f = b.bind(remote).sync();
                LOG.debug("RPCX服务监听启动: " + f.channel().localAddress());
                this.channel = f.channel();
                this.channel.closeFuture().sync();
            } finally {
                unRegisterService();
                group.shutdownGracefully().sync();
            }
        } catch (Exception e) {
            LOG.error("RPCX服务监听失败", e);
        }
    }

    /**
     * 注册服务
     */
    public void registerService() {
        if (discovery == null) {
            return;
        }
        Arrays.stream(Service.getAllService()).forEach(a -> {
            try {
                discovery.registerService(a, "tcp@" + address, "tps=0");
            } catch (RpcException e) {
                LOG.warn("注册中心服务[" + a + "]注册失败:" + e.getMessage());
            }
        });
    }

    /**
     * 取消注册服务
     */
    public void unRegisterService() {
        if (discovery == null) {
            return;
        }
        Arrays.stream(Service.getAllService()).forEach(a -> {
            try {
                discovery.unRegisterService(a, "tcp@" + address);
            } catch (RpcException e) {
                LOG.warn("注册中心服务[" + a + "]取消失败:" + e.getMessage());
            }
        });
    }

    /**
     * 关闭
     */
    public void Close() {
        System.out.println("RPCX Server 关闭");
        if (handler != null) {
            handler.Close(); //关闭handler
            handler = null;
        }
        if (channel != null) {
            channel.disconnect();
            channel.disconnect();
            channel.close();
            channel = null;
        }
        unRegisterService();
    }

}
