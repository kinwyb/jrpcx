package com.heldiam.jrpcx.core.codec;

import com.heldiam.jrpcx.core.protocol.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 命令编码
 *
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public class NettyEncoder extends MessageToByteEncoder<Command> {

    private static final Logger log = LoggerFactory.getLogger(NettyEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, Command remotingCommand, ByteBuf out) {
        try {
            byte[] data = remotingCommand.getMessage().encode();
            out.writeBytes(data);
        } catch (Exception e) {
            log.error("encode exception", e);
        }
    }
}
