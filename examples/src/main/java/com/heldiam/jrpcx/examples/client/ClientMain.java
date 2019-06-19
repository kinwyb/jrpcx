package com.heldiam.jrpcx.examples.client;

import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.failMode.FailModeEnum;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.discovery.EtcdV2Discovery;
import com.heldiam.jrpcx.examples.exampleData.Arith;
import com.heldiam.jrpcx.examples.exampleData.ArithMulRequest;
import com.heldiam.jrpcx.examples.exampleData.ArithMulResponse;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.BasicConfigurator;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author kinwyb
 * @date 2019-06-14 17:38
 **/
public class ClientMain {

    private static final java.util.Random random = new java.util.Random(new Date().getTime());

    private static final AtomicInteger integer = new AtomicInteger();

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        EtcdV2Discovery discovery = new EtcdV2Discovery("118.31.188.131:2379");
        discovery.setBasePath("/jrpcx");
        Client client = Selector.WeightedRountRobin(discovery).buildClient().setFailMode(FailModeEnum.Failbackup);
        Arith arith = client.Proxy(Arith.class);
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        loopGroup.execute(new ArithMul("1", arith));
        loopGroup.execute(new ArithMul("2", arith));
        loopGroup.execute(new ArithMul("3", arith));
        loopGroup.execute(new ArithMul("4", arith));
        loopGroup.execute(new ArithMul("5", arith));
        loopGroup.execute(new ArithMul("6", arith));
        loopGroup.execute(new ArithMul("7", arith));
        loopGroup.execute(new ArithMul("8", arith));
        loopGroup.execute(new ArithMul("9", arith));
        loopGroup.execute(new ArithMul("10", arith));
        loopGroup.shutdownGracefully().awaitUninterruptibly();
        client.Close();
        System.out.println("请求结果异常数量:" + integer.toString());
    }

    static class ArithMul implements Runnable {

        private ArithMulRequest req = new ArithMulRequest();

        private final String threadString;
        private final Arith arith;

        ArithMul(String threadString, Arith arith) {
            this.threadString = threadString;
            this.arith = arith;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                req.setA(ClientMain.random.nextInt(1000));
                req.setB(ClientMain.random.nextInt(1000));
                try {
                    ArithMulResponse rep = arith.Mul(req);
                    Integer result = rep.getC();
                    if (result == null) {
                        System.out.println("Thread " + threadString + "请求结果 => null");
                        continue;
                    }
                    System.out.println("Thread " + threadString + " => " + req.getA() + "*" + req.getB() + " = " + rep.getC());
                    if (req.getA() * req.getB() != result) {
                        System.out.println("Thread " + threadString + " => " + req.getA() + "*" + req.getB() + " = " + rep.getC());
                        integer.incrementAndGet();
                    }
                } catch (Exception ex) {
                    System.out.println("Thread Exception " + threadString + " => " + ex.getMessage());
                }
            }
        }
    }

}
