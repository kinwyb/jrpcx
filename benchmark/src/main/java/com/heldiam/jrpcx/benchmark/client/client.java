package com.heldiam.jrpcx.benchmark.client;

import com.heldiam.jrpcx.benchmark.api.BenchmarkReq;
import com.heldiam.jrpcx.benchmark.api.BenchmarkService;
import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.discovery.SimpleDiscovery;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.apache.log4j.BasicConfigurator;

/**
 * @author kinwyb
 * @date 2019-06-20 11:25
 **/
public class client {

    public static void main(String[] args) {
        BasicConfigurator.configure();
//        EtcdV2Discovery discovery = new EtcdV2Discovery("118.31.188.131:2379");
//        discovery.setBasePath("/jrpcx");
        Client client = Selector.WeightedRountRobin(new SimpleDiscovery("127.0.0.1:8972")).buildClient();
        BenchmarkService service = client.Proxy(BenchmarkService.class);
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        loopGroup.execute(new BenchmarkServiceRunnable("1", service));
//        loopGroup.execute(new ArithMul("2", arith));
//        loopGroup.execute(new ArithMul("3", arith));
//        loopGroup.execute(new ArithMul("4", arith));
//        loopGroup.execute(new ArithMul("5", arith));
//        loopGroup.execute(new ArithMul("6", arith));
//        loopGroup.execute(new ArithMul("7", arith));
//        loopGroup.execute(new ArithMul("8", arith));
//        loopGroup.execute(new ArithMul("9", arith));
//        loopGroup.execute(new ArithMul("10", arith));
        loopGroup.shutdownGracefully().awaitUninterruptibly();
        client.Close();
//        System.out.println("请求结果异常数量:" + integer.toString());
    }

    static class BenchmarkServiceRunnable implements Runnable {

        private BenchmarkReq req;

        private final String threadString;
        private final BenchmarkService service;

        BenchmarkServiceRunnable(String threadString, BenchmarkService service) {
            this.service = service;
            this.threadString = threadString;
            req = new BenchmarkReq();
            req.setWords("heldiam");
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                try {
//                    BenchmarkResp rep = service.say(req);
//                    System.out.println(rep.getWords());
//                    service.sayNoBack(req);
                    byte[] result = service.sayByte("ok".getBytes("UTF-8"));
                    System.out.println(new String(result, "UTF-8"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Thread Exception " + threadString + " => " + ex.getMessage());
                }
            }
        }
    }

}
