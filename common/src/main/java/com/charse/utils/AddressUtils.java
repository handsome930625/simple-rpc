package com.charse.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * description:
 *
 * @author 王亦杰
 * @version 1.0
 * @date 2018/8/7 9:14
 */
public class AddressUtils {
    private AddressUtils() {
    }

    /**
     * description: 获取本机ip
     *
     * @author 王亦杰
     * @date 2018/8/7 9:17
     */
    public static String getLocalHost() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(AddressUtils.getLocalHost());
    }
}
