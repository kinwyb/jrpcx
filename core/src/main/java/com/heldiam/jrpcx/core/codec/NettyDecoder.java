package com.heldiam.jrpcx.core.codec;

import com.heldiam.jrpcx.core.common.RpcRuntimeException;
import com.heldiam.jrpcx.core.protocol.Command;
import com.heldiam.jrpcx.core.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 命令解码
 *
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public class NettyDecoder extends ReplayingDecoder<DecoderState> {

    private Message message = null;

    public NettyDecoder() {
        super(DecoderState.MagicNumber);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        switch (state()) {
            case MagicNumber:
                message = new Message();
                byte magicNumber = in.readByte();
                if (magicNumber != Message.magicNumber) {
                    throw new RpcRuntimeException("magicNumber error:" + magicNumber);
                }
                checkpoint(DecoderState.Header);
            case Header:
                byte[] header = new byte[12];
                header[0] = Message.magicNumber;
                in.readBytes(header, 1, 11);
                message.header = header;
                checkpoint(DecoderState.Body);
            case Body:
                int totalLen = in.readInt();
                byte[] data = new byte[totalLen];
                in.readBytes(data);
                Command command = new Command(message, data);
                checkpoint(DecoderState.MagicNumber);
                //业务解码交给业务层
                out.add(command);
                break;
            default:
                throw new RpcRuntimeException("Shouldn't reach here.");
        }
    }

}