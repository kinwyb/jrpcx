package com.heldiam.jrpcx.examples.spring;

import com.heldiam.jrpcx.annotation.RpcxReference;
import com.heldiam.jrpcx.examples.exampleData.Arith;
import com.heldiam.jrpcx.examples.exampleData.ArithMulRequest;
import com.heldiam.jrpcx.examples.exampleData.ArithMulResponse;
import org.springframework.stereotype.Component;

/**
 * @author kinwyb
 * @date 2019-06-21 07:57
 **/
@Component(value = "ddddd")
public class SimpleComponent {

    private String data = "Default Data";

    @RpcxReference
    private Arith arith;

    public String getData() {
        ArithMulRequest req = new ArithMulRequest();
        req.setA(9);
        req.setB(9);
        ArithMulResponse resp = arith.Mul(req);
        return String.valueOf(resp.getC());
    }

    public void setData(String data) {
        this.data = data;
    }

}
