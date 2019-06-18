package com.heldiam.jrpcx.examples.client;

import com.heldiam.jrpcx.client.Client;
import com.heldiam.jrpcx.client.selector.Selector;
import com.heldiam.jrpcx.core.discovery.EtcdV2Discovery;
import com.heldiam.jrpcx.examples.common.Arith;
import com.heldiam.jrpcx.examples.common.ArithMulRequest;
import com.heldiam.jrpcx.examples.common.ArithMulResponse;
import org.apache.log4j.BasicConfigurator;

/**
 * @author kinwyb
 * @date 2019-06-14 17:38
 **/
public class ClientMain {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        EtcdV2Discovery discovery = new EtcdV2Discovery("118.31.188.131:2379");
        discovery.setBasePath("/jrpcx");
        Client client = Selector.WeightedRountRobin(discovery).buildClient();
        Arith arith = client.Proxy(Arith.class);
        ArithMulRequest req = new ArithMulRequest();
        req.setA(7);
        req.setB(8);
        for (int i = 0; i < 1000; i++) {
            ArithMulResponse rep = arith.Mul(req);
            System.out.println("结果:" + rep.getC());
            Thread.sleep(1000);
        }
        client.Close();
    }

}
