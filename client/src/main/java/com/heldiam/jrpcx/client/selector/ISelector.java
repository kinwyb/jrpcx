package com.heldiam.jrpcx.client.selector;


import com.heldiam.jrpcx.core.common.URL;

import java.util.List;

/**
 * 服务器地址选择接口
 *
 * @author heldiam
 */
public interface ISelector {

    /**
     * 获取一个连接地址
     *
     * @return
     */
    URL GetAddress(String serviceName, List<String> address);

    /**
     * 获取选择模式
     *
     * @return
     */
    SelectorMode getMode();

}
