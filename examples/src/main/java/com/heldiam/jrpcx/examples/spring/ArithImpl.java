package com.heldiam.jrpcx.examples.spring;

import com.heldiam.jrpcx.annotation.RpcxService;
import com.heldiam.jrpcx.examples.exampleData.Arith;
import com.heldiam.jrpcx.examples.exampleData.ArithMulRequest;
import com.heldiam.jrpcx.examples.exampleData.ArithMulResponse;

/**
 * @author kinwyb
 * @date 2019-06-21 15:15
 **/
@RpcxService(Interface = Arith.class)
public class ArithImpl implements Arith {

    @Override
    public ArithMulResponse Mul(ArithMulRequest request) {
        ArithMulResponse resp = new ArithMulResponse();
        resp.setC(request.getA() * request.getB());
        return resp;
    }

}
