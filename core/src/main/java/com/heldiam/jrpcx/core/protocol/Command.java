package com.heldiam.jrpcx.core.protocol;

import com.heldiam.jrpcx.core.common.Constants;

/**
 * 指令
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public class Command {


    private int code = 0;
    private int version = 0;

    /**
     * 解码的时候会用到,不会实际传输
     */
    private transient byte[] data;

    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Command() {
    }

    public Command(Message message, byte[] body) {
        this.message = message;
        setData(body);
    }


    public static Command createRequestCommand(Message message) {
        Command cmd = new Command();
        cmd.setMessage(message);
        return cmd;
    }


    public static Command createResponseCommand(Message message) {
        Command cmd = new Command();
        cmd.setMessage(message);
        return cmd;
    }



    public void markResponseType() {
        this.message.setMessageType(MessageType.Response);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    public boolean isOnewayRPC() {
        return this.message.isOneway();
    }


    public CommandType getType() {
        if (this.isResponseType()) {
            return CommandType.RESPONSE;
        }
        return CommandType.REQUEST;
    }


    public boolean isResponseType() {
        return this.message.getMessageType().equals(MessageType.Response);
    }

    public void markOnewayRPC() {
        this.message.setOneway(true);
    }

    public static Command createResponseCommand(int errorCode, String errorMessage) {
        Command cmd = new Command();
        cmd.markResponseType();
        cmd.getMessage().setMessageStatusType(MessageStatusType.Error);
        cmd.getMessage().metadata.put(Constants.RPCX_ERROR_CODE, String.valueOf(errorCode));
        cmd.getMessage().metadata.put(Constants.RPCX_ERROR_MESSAGE, errorMessage);
        return cmd;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public void setErrorMessage(int code, String message) {
        this.setErrorMessage(String.valueOf(code), message);
    }


    /**
     * rpcx 是通过 meta 传递错误信息的
     *
     * @param code
     * @param message
     */
    public void setErrorMessage(String code, String message) {
        //带有错误的返回结果
        this.message.setMessageStatusType(MessageStatusType.Error);
        this.message.metadata.put(Constants.RPCX_ERROR_CODE, code);
        this.message.metadata.put(Constants.RPCX_ERROR_MESSAGE, message);
    }

    public Command requestToResponse() {
        this.message.setMessageType(MessageType.Response);
        this.data = new byte[]{};
        this.message.payload = new byte[]{};
        this.message.metadata.clear();
        return this;
    }

}