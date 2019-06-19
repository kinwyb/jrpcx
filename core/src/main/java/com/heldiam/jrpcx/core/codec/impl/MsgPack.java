package com.heldiam.jrpcx.core.codec.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.heldiam.jrpcx.core.codec.CoderException;
import com.heldiam.jrpcx.core.codec.ICodec;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kinwyb
 * @date 2019-06-15 15:49
 **/
public class MsgPack implements ICodec {

    private static ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());

    public MsgPack() {
        VisibilityChecker.Std std = VisibilityChecker.Std.defaultInstance();
        std = std.with(JsonAutoDetect.Visibility.ANY);
        std = std.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(std);
    }

    @Override
    public Object decode(byte[] data, Class retType) throws CoderException {
        try {
            return toObject(data, retType);
        } catch (Exception ex) {
            throw new CoderException("MsgPack解码失败");
        }
    }

    @Override
    public byte[] encode(Object params) throws CoderException {
        try {
            return toBytes(params);
        } catch (Exception e) {
            throw new CoderException("MsgPack编码失败");
        }
    }

    /**
     * @param obj
     * @return
     * @Title: toBytes
     * @Description: 对象转byte数组
     * @author Jecced
     */
    public static <T> byte[] toBytes(T obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param bytes
     * @param clazz
     * @return
     * @Title: toList
     * @Description: byte转list集合
     * @author Jecced
     */
    public static <T> List<T> toList(byte[] bytes, Class<T> clazz) {
        List<T> list = null;
        try {
            list = mapper.readValue(bytes, List(clazz));
        } catch (IOException e) {
            list = new ArrayList<>();
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param bytes
     * @param clazz
     * @return
     * @Title: toObject
     * @Description: byte转指定对象
     * @author Jecced
     */
    public static <T> T toObject(byte[] bytes, Class<T> clazz) {
        T value = null;
        try {
            value = mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * @param clazz
     * @return
     * @Title: List
     * @Description: 私有方法, 获取泛型的TypeReference
     * @author Jecced
     */
    private static <T> JavaType List(Class<?> clazz) {
        return mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
    }

}
