package com.heldiam.jrpcx.examples.server;

import com.heldiam.jrpcx.core.discovery.EtcdV2Discovery;
import com.heldiam.jrpcx.examples.common.Arith;
import com.heldiam.jrpcx.examples.common.ArithMulResponse;
import com.heldiam.jrpcx.server.Server;
import com.heldiam.jrpcx.server.Service;
import org.apache.log4j.BasicConfigurator;

/**
 * @author kinwyb
 * @date 2019-06-15 09:07
 **/
public class ServerMain {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Arith arith = request -> {
            ArithMulResponse response = new ArithMulResponse();
            response.setC(request.getA() * request.getB());
            return response;
        };
        Service.registerService(arith, Arith.class);
        EtcdV2Discovery discovery = new EtcdV2Discovery("118.31.188.131:2379");
        discovery.setBasePath("/jrpcx");
        Server server = new Server("127.0.0.1:8972", discovery);
        new Thread(() -> server.Start()).run();
        System.out.println("定时关闭服务");
        Thread.sleep(30000);
        server.Close();
    }
}
