package com.heldiam.jrpcx.examples.common;

import java.io.Serializable;

/**
 * 返回结果
 * @author heldiam
 */
public class ArithMulResponse implements Serializable{
    private Integer C;

    public Integer getC() {
        return C;
    }

    public void setC(Integer C) {
        this.C = C;
    }
    
}
