package com.heldiam.jrpcx.examples.exampleData;

import com.heldiam.jrpcx.client.MetaData;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @author heldiam
 */
public class ArithMulResponse extends MetaData implements Serializable {
    private Integer C;

    public Integer getC() {
        return C;
    }

    public void setC(Integer C) {
        this.C = C;
    }

}

