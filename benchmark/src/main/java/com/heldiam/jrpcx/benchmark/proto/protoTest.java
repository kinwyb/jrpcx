package com.heldiam.jrpcx.benchmark.proto;

/**
 * @author kinwyb
 * @date 2019-06-20 10:39
 **/
public class protoTest {

    public static void main(String[] args) {
        BenchmarkMsg.BenchmarkMessage.Builder msgBuilder = BenchmarkMsg.BenchmarkMessage.newBuilder();
        msgBuilder.setField1("许多往事在眼前一幕一幕，变的那麼模糊");
        msgBuilder.setField2(100000);
        msgBuilder.setField3(2584);
        BenchmarkMsg.BenchmarkMessage msg = msgBuilder.build();
        byte[] data = msg.toByteArray();
        System.out.println(data);
    }
}
