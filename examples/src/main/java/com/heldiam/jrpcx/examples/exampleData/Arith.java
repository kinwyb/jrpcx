package com.heldiam.jrpcx.examples.exampleData;

/**
 * 测试接口
 *
 * @author heldiam
 */
public interface Arith {

    /**
     * 乘法
     *
     * @param request 请求参数
     * @return
     */
    ArithMulResponse Mul(ArithMulRequest request);

}
