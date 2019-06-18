package com.heldiam.jrpcx.examples.common;


import java.io.Serializable;

/**
 * @author heldiam
 */
public class ArithMulRequest implements Serializable {

    private Integer A;
    private Integer B;

    public Integer getA() {
        return A;
    }

    public void setA(Integer A) {
        this.A = A;
    }

    public Integer getB() {
        return B;
    }

    public void setB(Integer B) {
        this.B = B;
    }

}
