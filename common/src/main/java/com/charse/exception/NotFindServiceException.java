package com.charse.exception;

/**
 * description: 找不到服务
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/7 13:54
 */
public class NotFindServiceException extends RuntimeException {

    public NotFindServiceException(String message) {
        super(message);
    }
}
