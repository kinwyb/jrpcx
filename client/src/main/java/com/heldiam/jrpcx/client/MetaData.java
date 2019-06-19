package com.heldiam.jrpcx.client;

import java.util.Map;

/**
 * 元数据
 *
 * @author kinwyb
 * @date 2019-06-19 14:00
 **/
public class MetaData {

    private transient Map<String, String> metaData;

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

}
