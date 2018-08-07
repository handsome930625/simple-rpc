package com.charse.client.handler;

import com.charse.bean.RpcResponse;

/**
 * description: 返回值持有类
 * 作用是等待返回值返回
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/8/7 11:06
 */
public class ResponseHolder {
    /**
     * 实际返回值
     */
    private RpcResponse response;

    public void setResponse(RpcResponse response) {
        this.response = response;
        synchronized (this) {
            notify();
        }
    }

    public RpcResponse getResponse() throws Exception {
        synchronized (this) {
            // 最长等待3s
            wait(3000);
        }
        return response;
    }

    public RpcResponse getResponse(long timeout) throws Exception {
        synchronized (this) {
            wait(timeout);
        }
        return response;
    }
}
