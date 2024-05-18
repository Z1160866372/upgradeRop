package com.richeninfo.util;

import org.springframework.core.SpringVersion;

import javax.annotation.Resource;

/**
 * @auth lei
 * @date 2024/3/22 11:02
 */
public class TestUtil {
    @Resource
    private static RSAUtils rsaUtils;

    public static void main(String[] args) throws Exception {
       /* String phone =rsaUtils.decryptByPriKey("gS0qBoxmpWAcf0R1Gu9T7L5P/QgoBV7jxYDleae1dp5n/40Pbp+4gGlY0YBqIgiwfGjH/EnXwkESwKXhw3qJLT4OHRGXeYvCujCm5FroU4Cnxmu/I6y/3jzceZMNSiSvwzLCCBMJBklE6dJqXj9gNN/ar4Qc0B5JXIThRsZLFW4=");
        System.out.println(phone);*/
      String sectoken=  Des3SSL.encodeDC("13564390148","D6Nao9FDoRJ69VavJ4DHXvXJ");
        System.out.println(sectoken);

        System.out.println("Spring Version: " + SpringVersion.getVersion());
    }
}
