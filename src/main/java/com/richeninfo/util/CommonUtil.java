package com.richeninfo.util;

import com.richeninfo.entity.mapper.entity.ActivityConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author : zhouxiaohu
 * @create 2022/8/30 17:14
 */
@Slf4j
@Component
public class CommonUtil {
    public static String DATAU_THIRDPART_PWD_KEY = "datauKey2015";
    public static  DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Date current_time = new Date();
    /**
     * 获取uuid
     * @return
     */
    public String getUUID(){
        String uuid = UUID.randomUUID().toString()
                .replaceAll("-", "");
        return uuid;
    }
    /**
     * 生成动态密码
     * @return
     */
    public String generateSmsCode() {
        SecureRandom r = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int n = r.nextInt(10);
            code.append(n);
        }
        return code.toString();
    }
    /**
     * 校验微信访问链接
     * @param userId_v
     * @param nonce_v
     */
    public void checkLink(String userId_v, String nonce_v)
            throws Exception {
        String message = "链接已经失效，请回到微信重新获取。";
        if (StringUtils.isEmpty(userId_v) || StringUtils.isEmpty(nonce_v)) {
            throw new Exception(message);
        }
//        try{
//            if (!CSResponse.getValidation(userId_v, nonce_v)) {
//                throw new Exception(message);
//            }
//        }catch(Exception e){
//            throw new Exception(message);
//        }
    }
    /**
     * 随机生成字符串
     *
     * @return
     */
    public  String getRandomCode(int passLength, int type) {
        StringBuffer buffer = null;
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        r.setSeed(new Date().getTime());
        switch (type) {
            case 0:
                buffer = new StringBuffer("0123456789");
                break;
            case 1:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");
                break;
            case 2:
                buffer = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 3:
                buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
                break;
            case 4:
                buffer = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 5:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 6:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                sb.append(buffer.charAt(r.nextInt(buffer.length() - 10)));
                passLength -= 1;
                break;
            case 7:
                String s = UUID.randomUUID().toString();
                sb.append(s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23)
                        + s.substring(24));
        }

        if (type != 7) {
            int range = buffer.length();
            for (int i = 0; i < passLength; ++i) {
                sb.append(buffer.charAt(r.nextInt(range)));
            }
        }
        return sb.toString();
    }

    /**
     * 加密
     *
     * @param content
     *            需要加密的内容
     * @param password
     *            加密密码
     * @return
     */
    public static String encryptByAES(String content, String password)
    {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            // kgen.init(128, new SecureRandom(password.getBytes()));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            // Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result); // 返回加密串
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 解密
     * @param password
     *            解密密钥
     * @return
     */
    public static String decryptFromAES(byte[] bytes, String password)
    {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(password.getBytes());
            // kgen.init(128, new SecureRandom(password.getBytes()));
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            // Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(bytes);
            return new String(result); // 解密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 随机生成加密密钥
     *
     * @return
     */
    public static String genRandomStrKey()
    {
        String password = UUID.randomUUID().toString();
        try {
            byte[] res = password.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5".toUpperCase());
            byte[] result = md.digest(res);
            for (int i = 0; i < result.length; i++) {
                md.update(result[i]);
            }
            byte[] hash = md.digest();
            StringBuffer d = new StringBuffer("");
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & 0xFF;
                if (v < 16)
                    d.append("0");
                d.append(Integer.toString(v, 16).toUpperCase() + "");
            }
            return d.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[])
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr)
    {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String getNodeValue(String outXML, String xpath) {
        if (outXML == null || outXML.equals("")) {
            return null;
        }
        Node node = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(outXML.getBytes("UTF-8")));
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpathn = factory.newXPath();
            node = (Node) xpathn.evaluate(xpath, doc, XPathConstants.NODE);
        } catch (Exception e) {
            log.error(outXML);
            e.printStackTrace();
        }
        if (node != null) {
            return node.getTextContent();
        } else {
            return null;
        }
    }
    public boolean getDate(Date date){
        boolean date_result=false;
        Calendar start_calendar = Calendar.getInstance();
        start_calendar.setTime(new Date());
        Calendar end_calendar = Calendar.getInstance();
        end_calendar.setTime(date);
        if(start_calendar.before(end_calendar)){
            date_result=true;
        }
        return date_result;
    }
    private static String datePattern = "yyyy-MM-dd";

    /**
     *  时间转时间字符串
     * @param date  日期
     * @param pattern  格式
     * @return String
     */
    public static String format(Date date, String pattern) {
        if (date == null)
            return "";
        return DateFormatUtils.format(date, pattern);
    }

    /**
     *  时间转时间字符串为yyyy-MM-dd HH:mm:ss 格式
     * @param date  日期
     * @return String
     */
    public static String formatDateTime(Date date) {
        if (date == null)
            return "";
        return DateFormatUtils.format(date, datePattern);
    }


    /**
     *  时间字符串转化为yyyy-MM-dd HH:mm:ss 格式
     * @param str  日期
     * @return Date
     */
    public static Date parseDateTime(String str) {
        if (str == null)
            return null;
        try {
            return DateUtils.parseDate(str, datePattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  字符串转时间
     * @param str  字符串
     * @param dateTimePattern  格式
     * @return Date
     */
    public static Date parseDateTime(String str, String dateTimePattern) {
        if (str == null)
            return null;
        try {
            return DateUtils.parseDate(str, Locale.CHINESE, dateTimePattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取当年的第一天
     */
    public static Date getCurrentFirstOfYear(){
        Calendar currCal=Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getFirstOfYear(currentYear);
    }

    /**
     * 获取当年的最后一天
     */
    public static Date getCurrentLastOfYear(){
        Calendar currCal=Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getLastOfYear(currentYear);
    }

    /**
     * 获取某年第一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getFirstOfYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }



    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getLastOfYear(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 24位密钥生成
     * @param length
     * @return
     */
    public static String getRandomChar(int length)
    {
        char[] chr = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	 /* char[] chr = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};*/
        /* char[] chr = {  '1', '2', '3', '4', '5', '6', '7', '8', '9'};*/
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; ++i)
            buffer.append(chr[random.nextInt(62)]);

        return buffer.toString();
    }

    /**
     * 获取今天增加天数后的结束时间23：59：59
     * @return
     */
    public static Date addDayEnd(int day,Date startTime){
        Date date=todayStart(startTime);
        return DateUtils.addMilliseconds(DateUtils.addDays(date,day),-1000);
    }
    /**
     * 获取今天开始时间00：00：00
     * @return
     */
    public static Date todayStart(Date date){
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set( Calendar.HOUR_OF_DAY,0);
        start.set( Calendar.MINUTE, 0);
        start.set( Calendar.SECOND,0);
        start.set( Calendar.MILLISECOND,0);
        return start.getTime();
    }

    /**
     * 概率
     * @param giftList
     * @return
     */
    public static ActivityConfiguration randomGift(List<ActivityConfiguration> giftList){
        double randomNum = RandomUtils.nextDouble();
        log.info("随机数=" + randomNum);
        double startRate = 0;
        double endRate = 0;
        for (int i = 0; i < giftList.size(); i++) {
            log.info("rate=" + giftList.get(i).getProb());
            startRate = endRate;
            log.info("startRate=" + startRate);
            endRate += Double.valueOf(giftList.get(i).getProb());
            log.info("endRate=" + endRate);
            if(randomNum >= startRate && randomNum < endRate){
                return giftList.get(i);
            }
        }
        return null;
    }
    public static String[] insert(String[] strings, String string) {
        if (strings == null) {
            strings = new String[0];
        }
        if (string.isEmpty()) {
            return null;
        }
        String[] resultString = new String[strings.length + 1];
        for (int i = 0; i < strings.length; i++) {
            resultString[i] = strings[i];
        }
        resultString[strings.length] = string;
        return resultString;
    }

    /**
     * 时间校验
     * @param start
     * @param end
     * @return
     * @throws ParseException
     */
    public static  String timeVerify(String start,String end) throws ParseException {
        String msg="";
        Date nowTime = df.parse(df.format(current_time));
        //log.info("当前时间"+nowTime.getTime());
        Date startTime = df.parse(start);
        //log.info("开始时间"+startTime.getTime());
        Date endTime = df.parse(end);
        // log.info("结束时间"+endTime.getTime());
        if(nowTime.getTime()<startTime.getTime()){//活动还未开始
            msg = "NotStarted";
        }else{
            if(nowTime.getTime()<=endTime.getTime()){//活动进行中
                msg = "underway";
            }else{//活动已结束
                msg = "over";
            }
        }
        return msg;
    }
}
