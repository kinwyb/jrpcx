package com.heldiam.jrpcx.examples.server;

import com.heldiam.jrpcx.core.discovery.EtcdV2Discovery;
import com.heldiam.jrpcx.examples.exampleData.Arith;
import com.heldiam.jrpcx.examples.exampleData.ArithMulResponse;
import com.heldiam.jrpcx.server.Server;
import com.heldiam.jrpcx.server.Service;
import org.apache.log4j.BasicConfigurator;

import java.util.Date;

/**
 * @author kinwyb
 * @date 2019-06-15 09:07
 **/
public class ServerMain {

    private static final java.util.Random random = new java.util.Random(new Date().getTime());

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Arith arith = request -> {
            try {
                Thread.sleep(random.nextInt(10000)); //随机模拟请求超时
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArithMulResponse response = new ArithMulResponse();
            response.setC(request.getA() * request.getB());
            return response;
        };
        Service.registerService(arith, Arith.class);
        EtcdV2Discovery discovery = new EtcdV2Discovery("118.31.188.131:2379");
        discovery.setBasePath("/jrpcx");
        Server server = new Server("127.0.0.1:8972", discovery);
//        new Thread(() -> server.Start()).run();
        server.Start();
    }
}
