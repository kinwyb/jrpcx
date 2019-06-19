/*
 * @Date @Time.
 * @Author kinwyb<kinwyb@aliyun.com>
 */
package com.heldiam.jrpcx.client;

import com.heldiam.jrpcx.core.codec.CoderException;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 返回结果对象
 *
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public class Feature {

    public static long TimeOut = 0; //超时时间,默认没有超时时间
    public static int MaxRetryNUM = 5; //最大重试次数

    private static final AtomicLong seqAtomic = new AtomicLong();

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Feature.class.getName());

    /**
     * 返回的结果
     */
    private Object retObject;
    private Class retClass;
    private Map<String, String> metaData;
    private Exception exception = null;
    private boolean isRet = false;
    private final Object lock = new Object();
    public LinkedList<String> ignoreAddress = new LinkedList();
    public Client client;
    public String seviceName;
    public String methodName;
    public Object params;
    public int retryNum = 0;
    public long seq;

    public void Call(Client client, String serviceName, String methodName, Object params) {
        this.client = client;
        this.seviceName = serviceName;
        this.methodName = methodName;
        this.params = params;
        this.seq = seqAtomic.incrementAndGet();
        FeaturePool.AddUseFeature(String.valueOf(seq), this);
        Call();
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    /**
     * 调用
     */
    public void Call() {
        if (client == null) { //没设置客户端的直接返回
            return;
        }
        retryNum++;
        try {
            client.Call(this);
        } catch (CoderException e) {
            setException(e);
            setResult(null);
        } catch (Exception e) { //失败重试
            client.getFailMode().getMode().Retry(this, e.getMessage());
        }
    }

    public void setResult(Object retObject) {
        this.retObject = retObject;
        isRet = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * 获取结果,如果结果还没返回该方法将堵塞
     *
     * @return
     * @throws Exception 异常结果
     */
    public Object Done() throws Exception {
        if (isRet) {
            if (exception != null) {
                throw exception;
            }
            return this.retObject;
        }
        synchronized (lock) {
            lock.wait(TimeOut);
            if (!isRet) { //超时了还没有结果,按错误处理方式重试请求
                LOG.warn("[" + seq + "]请求超时,准备重试");
                client.getFailMode().getMode().Retry(this, "服务请求超时");
                return Done();
            }
            if (exception != null) {
                throw exception;
            }
            return this.retObject;
        }
    }

    /**
     * 将对象放入缓存池
     */
    public void Close() {
        FeaturePool.GetUseFeature(String.valueOf(seq)); //从结果集中移除
        isRet = false;
        exception = null;
        retObject = null;
        retClass = null;
        client = null;
        seviceName = null;
        methodName = null;
        params = null;
        metaData = null;
        retryNum = 0;
        seq = -1;
        ignoreAddress.clear();
        FeaturePool.push(this);
    }

    public Class getRetClass() {
        return retClass;
    }

    public void setRetClass(Class retClass) {
        this.retClass = retClass;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

}
