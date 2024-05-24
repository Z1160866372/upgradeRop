package com.richeninfo.util;

import com.alibaba.fastjson.JSONObject;
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
      String sectoken=  Des3SSL.encodeDC("13564390148","m4rUsCQeueVT10Gfk81N4pNp");
        System.out.println(sectoken);

        System.out.println("Spring Version: " + SpringVersion.getVersion());
        /*String result = "{\"status\":\"SUCCESS\",\"result\":\"{\\\"msg\\\":\\\"111\\\",\\\"code\\\":1000}\"}";
        String code = JSONObject.parseObject(result).getString("result");
        System.out.println( JSONObject.parseObject(code).getString("code"));*/
       /*String mobile= rsaUtils.decryptByPriKey("dnuFTcb2xC/28VyPsBeBOl5FK54Wk46gBZMmBL5LwxokcFpq9P+Wg/LB9OLU55Ppujwk1ce/q849hrbNvnZefmYCfmhb9s/hd6PdfOharENvmGFQUoA36iwTKsOKeYtTTESgJ4jur0JrgoH9RwoLZ3U7zkIGLv2t1exZIn5/1Qk=").trim();
        System.out.println("mobile====="+mobile);*/
        /*String source[] = Des3SSL.decodeDC("kTUTNSLqGf4Lmvj9d5gqJ-5mo2311HNVjibObFciVTqvCSPQw2PV_Y9Jg9aDzKYe", "d8IybzY90SsaFGrGUVxSd6C5");
        String  mobile = source[0];
        System.out.println(mobile);*/


    }
}
