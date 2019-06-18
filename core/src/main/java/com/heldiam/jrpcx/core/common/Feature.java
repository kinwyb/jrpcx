/*
 * @Date @Time.
 * @Author kinwyb<kinwyb@aliyun.com>
 */
package com.heldiam.jrpcx.core.common;

/**
 * 返回结果对象
 *
 * @author kinwyb
 * @date 2019-06-14 16:19
 */
public class Feature {

    /**
     * 返回的结果
     */
    private Object retObject;
    private Class retClass;
    private Exception exception = null;
    private boolean isRet = false;
    private final Object lock = new Object();

    public void setResult(Object retObject) {
        this.retObject = retObject;
        synchronized (lock) {
            lock.notify();
        }
        isRet = true;
    }

    public Object getRetObject() {
        return retObject;
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
            lock.wait();
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
        isRet = false;
        exception = null;
        retObject = null;
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
