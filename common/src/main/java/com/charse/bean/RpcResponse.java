package com.charse.bean;

import java.io.Serializable;

/**
 * description: 封装 RPC 响应
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/6 17:18
 */
public class RpcResponse implements Serializable {
    /**
     * 请求id
     */
    private Long requestId;
    /**
     * 是否逻辑上调用成功
     */
    private Boolean isSuccess;
    /**
     * 是否出现异常
     */
    private Exception exception;
    /**
     * 返回值
     */
    private Object result;

    public boolean hasException() {
        return exception != null;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() throws Exception {
        if (this.exception != null) {
            throw this.exception;
        }
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", isSuccess=" + isSuccess +
                ", exception=" + exception +
                ", result=" + result +
                '}';
    }
}
