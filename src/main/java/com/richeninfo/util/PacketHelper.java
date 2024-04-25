/*
 * Copyright (c) RICHENINFO [2024]
 * Unauthorized use, copying, modification, or distribution of this software
 * is strictly prohibited without the prior written consent of Richeninfo.
 * https://www.richeninfo.com/
 */
package com.richeninfo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.richeninfo.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author: xiaohu.zhou
 * @Created: 2022/8/30 14:30
 */
@Component
public class PacketHelper {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;

    //云MSM短信验证
    public String sendDSMS(String mobiles, String[] params) {
        JSONObject jsonObject = new JSONObject();
        String new_params = "[\"";
        for (int i = 0; i < params.length; i++) {
            new_params += params[i] + "\",\"";
        }
        new_params = new_params.substring(0, new_params.lastIndexOf(","));
        new_params += "]";
        String url = "http://112.35.1.155:1992/sms/tmpsubmit";
        String ecName = "上海锐至信息技术股份有限公司";
        String apId = "shyd";
        String secretkey = "Richen123info.@";
        String addSerial = "";
        String sign = "RhXRSzBTR";
        String templateId = "4f764519aabd43beb336ee6d14dd82f6";
        String mac = ecName + apId + secretkey + templateId + mobiles + new_params + sign;
        jsonObject.put("ecName", ecName);//企业名称
        jsonObject.put("apId", apId);//用户名
        jsonObject.put("mobiles", mobiles);//手机号
        jsonObject.put("addSerial", addSerial);//扩展码
        jsonObject.put("sign", sign);//签名编号RhXRSzBTR
        jsonObject.put("mac", MD5.encrypt32(mac.toString()));//批次号
        jsonObject.put("secretkey", secretkey);//批次号
        jsonObject.put("templateId", templateId);//批次号
        jsonObject.put("params", params);//短信内容
        String result = "";
        try {
            result = HttpClientTool.doPostString(url, Base64.encode(jsonObject.toJSONString()));
        } catch (Exception e) {
            e.getStackTrace();
        }
        return result;
    }

    //4147接口 只针对活动编号activityId&itemId
    public Packet getCommitPacket4147(String userId, String activityId, String itemId, String out_order_id) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4147");
        JSONObject busiParams = new JSONObject();
        busiParams.put("create_date", sdf.format(new Date()));
        busiParams.put("out_order_id", out_order_id);
        busiParams.put("bill_id", userId);
        busiParams.put("activity_id", activityId);
        busiParams.put("is_check_out_ord_id", "1");
        JSONObject busiParams1 = new JSONObject();
        busiParams1.put("item_id", itemId);
        JSONArray json_array = new JSONArray();
        json_array.add(busiParams1);
        busiParams.put("param_code", json_array);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4147");
        return packet;
    }

    //4147接口 自定义发放内容 giftId
    public Packet getCommitPacket4147_itemId(String userId, String giftId, String activityId, String itemId, String out_order_id) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4147");
        JSONObject busiParams = new JSONObject();
        busiParams.put("create_date", sdf.format(new Date()));
        busiParams.put("out_order_id", out_order_id);
        busiParams.put("bill_id", userId);
        busiParams.put("activity_id", activityId);
        busiParams.put("is_check_out_ord_id", "1");
        JSONObject busiParams1 = new JSONObject();
        busiParams1.put("amount", giftId);
        busiParams1.put("item_id", itemId);
        JSONArray json_array = new JSONArray();
        json_array.add(busiParams1);
        busiParams.put("param_code", json_array);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4147");
        return packet;
    }

    //3066业务接口 channel_id集团掌厅渠道xOrgId&xOpId
    public Packet getCommitPacket3066(String userId, List<VasOfferInfo> offerList, String channel_id) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3066");
        JSONObject busiParams = new JSONObject();
        busiParams.put("ServiceNum", userId);
        busiParams.put("NeedSendMsg", "Y");
        busiParams.put("VasOfferInfo", offerList);
        if (channel_id.equals("jtzt")) {
            busiParams.put("xOrgId", "yjdq");
            busiParams.put("xOpId", "1000000002110700006");
        }
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM3066");
        packet.getPost().getPubInfo().setInterfaceId("25");
        return packet;
    }

    //3066业务接口  无渠道参数
    public Packet getCommitPacket(String userId, List<VasOfferInfo> offerList) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3066");
        JSONObject busiParams = new JSONObject();
        busiParams.put("ServiceNum", userId);
        busiParams.put("NeedSendMsg", "Y");
        busiParams.put("VasOfferInfo", offerList);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM3066");
        packet.getPost().getPubInfo().setInterfaceId("25");
        return packet;
    }

    /**
     * 发送验证码
     *
     * @param userId
     * @param content
     * @return
     */
    public Packet getCommitPacket1638(String userId, String content) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI1638");
        JSONObject busiParams = new JSONObject();
        busiParams.put("Port", "10658487");
        busiParams.put("OptCode", "1");
        busiParams.put("SMSType", "1");
        busiParams.put("DestNum", userId);
        busiParams.put("SMSSendTime", "");
        busiParams.put("Content", content);
        busiParams.put("Title", "验证码");
        busiParams.put("SmsCode", "11013252");
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM1638");
        packet.getPost().getPubInfo().setInterfaceId("137");
        return packet;
    }

    /**
     * 校验是否实名制、是否入网满6个月的接口
     *
     * @param userId
     * @return
     */
    public Packet getCommitPacket4366(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4366");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4366");
        packet.getPost().getPubInfo().setInterfaceId("139");
        return packet;
    }

    /**
     * 该用户星级
     *
     * @param userId
     * @return
     */
    public Packet getCommitPacket4338(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4338");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        busiParams.put("channelType", "hjswx");
        busiParams.put("ext1", "");
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4338");
        packet.getPost().getPubInfo().setInterfaceId("79");
        return packet;
    }

    public Packet getCommitPacket3027(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3027");
        JSONObject busiParams = new JSONObject();
        busiParams.put("ServiceNum", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM3027");
        return packet;
    }

    public Packet getCommitPacket3063(String[] offerIds, String planId, String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3063");
        JSONObject busiParams = new JSONObject();
        busiParams.put("ServiceNum", userId);
        busiParams.put("PlanId", planId);
        busiParams.put("OfferId", offerIds);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM3063");
        return packet;
    }

    public Packet getCommitPacket3406(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3406");
        JSONObject busiParams = new JSONObject();
        busiParams.put("phone_id", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "BOSS3406");
        return packet;
    }

    //宽带信息查询接口
    public Packet getCommitPacket4193(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4193");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        JSONObject busiParams1 = new JSONObject();
        JSONArray json_array = new JSONArray();
        json_array.add(busiParams1);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4193");
        return packet;
    }

    //携转
    public Packet getCommitPacket2856(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI002856");
        JSONObject busiParams = new JSONObject();
        busiParams.put("BILL_ID", userId);
        JSONObject busiParams1 = new JSONObject();
        JSONArray json_array = new JSONArray();
        json_array.add(busiParams1);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM002856");
        return packet;
    }

    /**
     * 促销品订单查询接口
     *
     * @return
     */
    public Packet getCommitPacket4465(String billId, String queryType, String activtyId, String offerId, String begDate, String endDate, String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4465");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", billId);
        busiParams.put("queryType", queryType);
        busiParams.put("activtyId", activtyId);
        busiParams.put("offerId", offerId);
        busiParams.put("begDate", begDate);
        busiParams.put("endDate", endDate);
        busiParams.put("userId", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4465");
        return packet;
    }

    /***星火订单全流程接入接口
     */
    public static Packet spark(String SYSTEM_ID, String CM_ORDER_ID, String UNI_CHANNEL_ID, String CHANNEL_ID
            , String CHARGE, String BILL_CHARGE, String ORDER_TYPE, String ORDER_SUBTYPE, String PHONE, String NICK_NAME
            , String ASSISTANT_USERINFO, String BIND_CUSID, String BIND_CUSNO, String PAY_TIME, String CLOSE_TIME
            , String PAY_TYPE, String PAY_CHANNEL, String BUSI_STATUS, String PROVINCE, String CARD_TYPE, String CARD_NAME
            , String CARD_VALUE, String CARD_NO, String PLATFORM_CODE, String PHONE_NO, String CHARGE_FEE, String CHARGE_UNIT
            , String DELIVER_WAY, String ORDER_POST_NAME, String ORDER_POST_PHONE, String ORDER_POST_ADDR, String INVOICE_TYPE
            , String INVOICE_CUSTOMER_TYPE, String INVOICE_TITLE, String INVOICE_TAX_NO, String INVOICE_REMARK, String INVOICE_CONTENT
            , String INVOICE_MAIL, String FREIGHT, String CREDIT_AMOUNT, String DISTRIBUTION_ID, String RUNNING_ID, String FLAG, String FORWARDAPP, String COMMODITY_ID
            , String COMMODITY_NAME, String COMMODITY_URL, String COMMODITY_VERSION, String COMMODITY_NUM, String COMMODITY_CODE
            , String COMMODITY_TYPE, String SALE_TYPE, String SKU_ID, String SKU_NAME, String SOURCE_CHANNEL, String UNIT_PRICE
            , String DISCOUNT_PRICE, String PRICE, String WITH_CONTRACT, String SHOP_ID, String SHOP_NAME, String AGG_PAGE_CODE) {
        JSONObject busiParams = new JSONObject();
        busiParams.put("SYSTEM_ID", SYSTEM_ID);//请求系统编码
        busiParams.put("CM_ORDER_ID", CM_ORDER_ID);//一级电渠订单号(由一级渠道创建订单并落地到省份)
        busiParams.put("UNI_CHANNEL_ID", UNI_CHANNEL_ID);//19位统一渠道编码
        busiParams.put("CHANNEL_ID", CHANNEL_ID);//下单渠道
        busiParams.put("CHARGE", CHARGE);//订单金额 （单位：分）
        busiParams.put("BILL_CHARGE", BILL_CHARGE);//实付金额（单位：分），如未支付传0
        busiParams.put("ORDER_TYPE", ORDER_TYPE);//订单类型
        busiParams.put("ORDER_SUBTYPE", ORDER_SUBTYPE);//订单子类型
        busiParams.put("PHONE", PHONE);//用户手机号
        busiParams.put("NICK_NAME", NICK_NAME);//用户昵称
        busiParams.put("ASSISTANT_USERINFO", ASSISTANT_USERINFO);//推荐人附加信息(星火平台调用 必传“（省编码|工号|客户经理ID|客户经理号码）“ 说明：无需解密，直接透传）
        busiParams.put("BIND_CUSID", BIND_CUSID);//推荐人ID
        busiParams.put("BIND_CUSNO", BIND_CUSNO);//推荐人手机号
        busiParams.put("PAY_TIME", PAY_TIME);//订单支付时间 格式：yyyy-MM-dd HH:mm:ss
        busiParams.put("CLOSE_TIME", CLOSE_TIME);//订单完成时间 格式：yyyy-MM-dd HH:mm:ss订单已完成且状态为成功，一次性同步。
        busiParams.put("PAY_TYPE", PAY_TYPE);//支付方式
        busiParams.put("PAY_CHANNEL", PAY_CHANNEL);//支付渠道
        busiParams.put("BUSI_STATUS", BUSI_STATUS);//订单状态
        busiParams.put("PROVINCE", PROVINCE);//省份编码,订单的落地省份
        busiParams.put("CARD_TYPE", CARD_TYPE);//卡券类型话费 hf01流量 ll01实物 sw01套餐 tc01通用 ty01花卡宝藏版 bzk01花卡 hk01合作券 hz01咪咕券 99hz肯德基券 898KFC
        busiParams.put("CARD_NAME", CARD_NAME);//卡券名称
        busiParams.put("CARD_VALUE", CARD_VALUE);//卡券面值
        busiParams.put("CARD_NO", CARD_NO);//卡券编码（卡券支付或卡券兑换时需要传递）
        busiParams.put("PLATFORM_CODE", PLATFORM_CODE);//业务上架平台编码（eg:省份H5通过星火平台上架/分享，传 02即可）00：其他01：在线工具02：星火平台
        busiParams.put("PHONE_NO", PHONE_NO);//被充值号码
        busiParams.put("CHARGE_FEE", CHARGE_FEE);//被充值金额当订单类型是003/004，chargeFee为必传
        busiParams.put("CHARGE_UNIT", CHARGE_UNIT);//充值单位（充值：分/流量：MB）当订单类型是003/004，chargeUnit为必传
        busiParams.put("DELIVER_WAY", DELIVER_WAY);//配送方式 SP：门店自提 ED：物流配送
        busiParams.put("ORDER_POST_NAME", ORDER_POST_NAME);//收货人姓名
        busiParams.put("ORDER_POST_PHONE", ORDER_POST_PHONE);//收货人手机号
        busiParams.put("ORDER_POST_ADDR", ORDER_POST_ADDR);//收货地址（当配送方式为SP，该字段表示 自提地址门店名称）
        busiParams.put("INVOICE_TYPE", INVOICE_TYPE);//发票类型
        busiParams.put("INVOICE_CUSTOMER_TYPE", INVOICE_CUSTOMER_TYPE);//发票客户类型
        busiParams.put("INVOICE_TITLE", INVOICE_TITLE);//发票抬头
        busiParams.put("INVOICE_TAX_NO", INVOICE_TAX_NO);//发票税号
        busiParams.put("INVOICE_REMARK", INVOICE_REMARK);//发票备注
        busiParams.put("INVOICE_CONTENT", INVOICE_CONTENT);//发票内容
        busiParams.put("INVOICE_MAIL", INVOICE_MAIL);//收票人邮箱
        busiParams.put("FREIGHT", FREIGHT);//运费
        busiParams.put("CREDIT_AMOUNT", CREDIT_AMOUNT);//减免金额 单位：分
        busiParams.put("DISTRIBUTION_ID", DISTRIBUTION_ID);//物流单号
        busiParams.put("RUNNING_ID", RUNNING_ID);//请求runningid
        busiParams.put("FLAG", FLAG);//接口传参固定传1
        busiParams.put("FORWARDAPP", FORWARDAPP);//渠道标识，一般为渠道名称大写
        JSONObject busiParams1 = new JSONObject();
        busiParams1.put("COMMODITY_ID", COMMODITY_ID);//	商品id
        busiParams1.put("COMMODITY_NAME", COMMODITY_NAME);//商品名称 （商品名或者商品简要描述信息）
        busiParams1.put("COMMODITY_URL", COMMODITY_URL);//商品链接
        busiParams1.put("COMMODITY_VERSION", COMMODITY_VERSION);//商品版本号
        busiParams1.put("COMMODITY_NUM", COMMODITY_NUM);//商品数量，默认 1
        busiParams1.put("COMMODITY_CODE", COMMODITY_CODE);//商品编码
        busiParams1.put("COMMODITY_TYPE", COMMODITY_TYPE);//商品类型
        busiParams1.put("SALE_TYPE", SALE_TYPE);//商品销售类型 1:普通商品 2：预售商品 3：秒杀商品
        busiParams1.put("SKU_ID", SKU_ID);//skuId
        busiParams1.put("SKU_NAME", SKU_NAME);//sku名称
        busiParams1.put("SOURCE_CHANNEL", SOURCE_CHANNEL);//用户下单渠道（即订单来源渠道），如：京东、天猫引流  通过星火平台同步的订单，来源渠道必传007星火店铺
        busiParams1.put("UNIT_PRICE", UNIT_PRICE);//单价 单位：分
        busiParams1.put("DISCOUNT_PRICE", DISCOUNT_PRICE);//优惠前金额（单位：分）
        busiParams1.put("PRICE", PRICE);//优惠后金额（单位：分）
        busiParams1.put("WITH_CONTRACT", WITH_CONTRACT);//是否合约机Y表示是，N表示不是
        busiParams1.put("SHOP_ID", SHOP_ID);//商户id（或者 19位店铺编码）
        busiParams1.put("SHOP_NAME", SHOP_NAME);//商户名（店铺名称）
        busiParams1.put("AGG_PAGE_CODE", AGG_PAGE_CODE);//记录聚合页面商品
        JSONArray json_array = new JSONArray();
        json_array.add(busiParams1);
        busiParams.put("items", json_array);
        Packet packet = getOUTER0001Packet(busiParams, "createOrder");
        return packet;
    }

    //权益平台 策划ID
    public Packet purchaseNew(String phone, String offerId, String channelCode) {
        JSONObject busiParams = new JSONObject();
        busiParams.put("idType", "1");//默认1
        busiParams.put("phone", phone);//订购手机号码
        busiParams.put("opid", "999990144");//工号
        busiParams.put("offerId", offerId);//策划ID
        busiParams.put("channelOrderNo", sdf.format(new Date()));//接入方（渠道）订单流水号，每笔订单唯一
        busiParams.put("orgid", "0");//组织编号
        busiParams.put("channelCode", channelCode);//渠道号，测试环境CH5，正式环境H5
        Packet packet = getOUTER0001Packet(busiParams, "purchaseNew");
        return packet;
    }

    public Packet QYPT202006101(String mobile, String code) {
        JSONObject busiParams = new JSONObject();
        busiParams.put("mobile", mobile);
        busiParams.put("code", code);
        Packet packet = getOUTER0001Packet(busiParams, "QYPT202006101");
        return packet;
    }

    /**
     * 金币校验
     *
     * @return
     */
    public Packet getCommitPacket5509(String mobile, String provinceCode, String requireBalance) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI5509");
        JSONObject busiParams = new JSONObject();
        busiParams.put("mobile", mobile);//手机号
        busiParams.put("provinceCode", provinceCode);//省编码
        busiParams.put("requireBalance", requireBalance);//所需金币数
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM5509");
        return packet;
    }

    /**
     * 金币兌換
     *
     * @return
     */
    public Packet getCommitPacket5287(String mobile, String provinceCode, String CIPCode, String orderPoints, String goodId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI5287");
        JSONObject busiParams = new JSONObject();
        busiParams.put("CIPCode", CIPCode);//接口编码
        busiParams.put("provinceCode", provinceCode);//省编码
        busiParams.put("reqSeq", sdf.format(new Date()));//请求流水
        busiParams.put("mobile", mobile);//手机号
        busiParams.put("orderPoints", orderPoints);//订单应付总金币数
        JSONObject busiParams1 = new JSONObject();
        busiParams1.put("goodId", goodId);//商品ID
        busiParams1.put("goodCount", "1");//商品数量
        busiParams1.put("goodPoints", "2");//单个商品应扣金币数
        busiParams1.put("actionId", "");//折扣规则ID
        JSONArray json_array = new JSONArray();
        json_array.add(busiParams1);
        busiParams.put("goodInfo", json_array);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM5287");
        return packet;
    }

    /**
     * 金币扣减
     *
     * @return
     */
    public Packet kj5287Packeteds(String mobile, String province, String CIPCode, String actionType, String marketCaseID,
                                  String deductValue, String comments) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI5287");
        JSONObject busiParams = new JSONObject();
        busiParams.put("CIPCode", CIPCode);//接口编码
        busiParams.put("province", province);//用户归属省
        busiParams.put("tradeID", sdf.format(new Date()));//交易ID
        busiParams.put("mobile", mobile);//手机号
        busiParams.put("actionType", actionType);//扣减活动类型
        busiParams.put("marketCaseID", marketCaseID);//省侧营销案ID或产品ID
        busiParams.put("provinceOrderId", sdf.format(new Date()));//省侧订单号/流水号
        busiParams.put("deductValue", deductValue);//扣减金币值
        busiParams.put("tradeTime", sdf.format(new Date()));//交易时间
        busiParams.put("comments", comments);//金币扣减原因
        busiParams.put("isSendSms", "0");
        busiParams.put("isSenstive", "1");
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM5287");
        return packet;
    }

    /**
     * 通用营销信息查询接口
     *
     * @return
     */
    public Packet getHJ1000Packeteds(String billId, String pointId) {
        Request request = new Request();
        request.setBusiCode("YX-SH-HJ1000");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", billId);//手机号
        busiParams.put("pointId", pointId);//触点编码
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "HJ1000");
        return packet;
    }

    /**
     * 促销订单生成
     *
     * @param userId
     * @return
     */
    public Packet getCommitPacket2329(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI002329");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM002329");
        return packet;
    }

    // 权益发放
    public static Packet quanyi(String mobile, String code) {
        JSONObject busiParams = new JSONObject();
        busiParams.put("mobile", mobile);
        busiParams.put("code", code);
        String mqMsg = JSON.toJSONString(busiParams);
        Packet packet = new Packet();
        packet.setApiCode("QYPT202006101");
        packet.setObject(busiParams);
        return packet;
    }

    /**
     * 卡券发放
     **/
    public static Packet CardVoucherIssued(String channelCode, String couponNo, String phone) {
        JSONObject busiParams = new JSONObject();
        busiParams.put("channelCode", channelCode);
        busiParams.put("couponNo", couponNo);
        busiParams.put("phone", phone);
        String mqMsg = JSON.toJSONString(busiParams);
        Packet packet = new Packet();
        packet.setApiCode("exchange");
        packet.setObject(busiParams);
        return packet;
    }

    /**
     * 卡券核销
     **/
    public static Packet CancelAfterVerification(String channelCode, String couponCode, String phone
            , String opid, String orgid, String couponType, String useTime) {
        JSONObject busiParams = new JSONObject();
        busiParams.put("channelCode", channelCode);
        busiParams.put("phone", phone);
        busiParams.put("couponCode", couponCode);
        busiParams.put("opid", opid);
        busiParams.put("orgid", orgid);
        busiParams.put("couponType", couponType);
        busiParams.put("useTime", useTime);
        String mqMsg = JSON.toJSONString(busiParams);
        Packet packet = new Packet();
        packet.setApiCode("channelVerify");
        packet.setObject(busiParams);
        return packet;
    }

    /**
     * 亲情网校验接口
     **/
    public Packet getCommitPacket4995(String userId, String billType, String productCode) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4995");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        busiParams.put("billType", billType);
        busiParams.put("productCode", productCode);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4995");
        return packet;
    }

    /**
     * 亲情网一键办理接口
     **/
    public Packet getCommitPacket5267(String POOrderNumber, String CustomerPhone, String OrderSource, String ProductCode
            , String MemNumber, String MemOrderNumber, String MemType, String Oper) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI5267");
        JSONObject busiParams = new JSONObject();
        busiParams.put("POOrderNumber", POOrderNumber);//--唯一订单号
        busiParams.put("CustomerPhone", CustomerPhone);//--主号码
        busiParams.put("OrderSource", OrderSource);//--订单来源
        busiParams.put("ProductCode", ProductCode);//--产品编码：统付MFC000001，自付MFC000002
        JSONObject new_busiParams = new JSONObject();
        JSONArray newArray = new JSONArray();
        if (MemNumber.equals("")) {
            busiParams.put("ProductOrderMember", newArray);
        } else {
            new_busiParams.put("MemNumber", MemNumber);//--副号码
            new_busiParams.put("MemOrderNumber", MemOrderNumber);// --成员唯一订单号
            new_busiParams.put("MemType", MemType);//--1-移动号码
            new_busiParams.put("Oper", Oper);//--操作类型0立即1次月
            newArray.add(new_busiParams);
            busiParams.put("ProductOrderMember", newArray);
        }

        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM5267");
        return packet;
    }

    /**
     * 周期性分月送券接口
     **/
    public Packet getCommitPacket1003(String loginNo, int loginType, String channelId, String actId, int optType, int ifActive, String serialNumber) {
        Request request = new Request();
        request.setBusiCode("PT-SH-EC-OI1003");
        JSONObject busiParams = new JSONObject();
        busiParams.put("loginNo", loginNo);//用户手机号
        busiParams.put("loginType", loginType);//0-手机号码，1-互联网账号（当前不支持）
        busiParams.put("channelId", channelId);//53-商城能开
        busiParams.put("actId", actId);//运营侧在卡券工作台配置完活动后提供
        busiParams.put("optType", optType);//0-触发，1-取消，取消之后系统不在再给用户发后面的券
        /*busiParams.put("orderId", orderId);*/
        busiParams.put("ifActive", ifActive);//0-立即激活，当前仅支持立即激活方式
        busiParams.put("serialNumber", serialNumber);//每笔调用唯一标识渠道编码+省份编码+YYYYMMDDHHmmssSSS +6位流水如：0010020170705184812000202038
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "ZTXD1003");
        return packet;
    }

    /**
     * 卡券活动领券接口
     ***/
    public Packet getCommitPacket1000(String loginNo, int loginType, String channelId, String batchID, String actId, String serialNumber, String obtainDate) {
        Request request = new Request();
        request.setBusiCode("PT-SH-EC-OI1000");
        JSONObject busiParams = new JSONObject();
        busiParams.put("loginNo", loginNo);//用户手机号
        busiParams.put("loginType", loginType);//0-手机号码，1-互联网账号（当前不支持）
        busiParams.put("channelId", channelId);//53-商城能开
        busiParams.put("batchID", batchID);//批次id，券批次的唯一标示
        busiParams.put("actId", actId);//批次所绑定开展的活动标识
        busiParams.put("serialNumber", serialNumber);//每笔调用唯一标识渠道编码+省份编码+YYYYMMDDHHmmssSSS +6位流水如：0010020170705184812000202038
        busiParams.put("obtainDate", obtainDate);//领取日期，领取对账的时候使用,例如：20171129,
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "ZTXD1000");
        return packet;
    }

    /**
     * Wap20用户判断
     *
     * @param userId
     * @return
     */
    public Packet getCommitPacket0808(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI0808");
        JSONObject busiParams = new JSONObject();
        busiParams.put("strCcsOpId", "");
        busiParams.put("strBillId", userId);
        JSONObject sPrivData = new JSONObject();
        sPrivData.put("m_iOrgId", "");
        sPrivData.put("m_iVestOrgId", "");
        sPrivData.put("m_iOpId", "");
        sPrivData.put("m_iOpEntityId", "");
        busiParams.put("sPrivData", sPrivData);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM0808");
        return packet;
    }

    /**
     * 和你APP登录校验
     *
     * @param userId
     * @return
     */
    public Packet hnAppLoginverify(String userId, String opStarttime, String opEndtime, int pageNum, int pageSize, String clientId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("InterfaceId", "72");
        JSONObject busiParams = new JSONObject();
        busiParams.put("opStarttime", opStarttime);
        busiParams.put("opEndtime", opEndtime);
        busiParams.put("mobileNo", userId);
        busiParams.put("pageNum", pageNum);
        busiParams.put("pageSize", pageSize);
        busiParams.put("clientId", clientId);
        jsonObject.put("body", busiParams);
        Packet packet = getOUTER0001Packet(jsonObject, "OUTER0001");
        return packet;
    }

    /**
     * 和你新人校验
     *
     * @param userId
     * @return
     */
    public Packet hnAppnewuserverify(String userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("InterfaceId", "72");
        JSONObject busiParams = new JSONObject();
        busiParams.put("mobileNo", userId);
        jsonObject.put("body", busiParams);
        Packet packet = getOUTER0001Packet(jsonObject, "PDXYH");
        return packet;
    }

    public Packet getCommitPacket4060(String userId, String fee) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4060");
        JSONObject busiParams = new JSONObject();
        busiParams.put("BILL_ID", userId);
        busiParams.put("SP_CODE", "4567890");
        busiParams.put("OPERATOR_CODE", "4567");
        busiParams.put("FEE", fee);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4060");
        packet.getPost().getPubInfo().setInterfaceId("79");
        return packet;
    }

    public Packet getCommitPacket4157(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4157");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4157");
        packet.getPost().getPubInfo().setInterfaceId("79");
        return packet;
    }

    /**
     * 飞享套餐办理
     *
     * @param OfferId
     * @param effectiveType
     * @param userId
     * @return
     */
    public Packet getCommitPacket0769(String OfferId, String effectiveType, String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI0769");
        JSONObject busiParams = new JSONObject();
        busiParams.put("OfferId", OfferId);
        busiParams.put("EffectiveType", effectiveType);
        busiParams.put("ServiceNum", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM0769");
        packet.getPost().getPubInfo().setInterfaceId("139");
        packet.getPost().getPubInfo().setInterfaceType("06");
        packet.getPost().getPubInfo().setClientIP("121.40.205.162");
        packet.getPost().getPubInfo().setRegionCode("210");
        return packet;
    }


    /**
     * 查询用户当月的套餐是否在指定套餐范围内
     *
     * @param userId
     * @return
     */
    public Packet getCommitPacket4459(String userId) {
        Request request = new Request();
        String timestamp = sdf.format(new Date());
        request.setBusiCode("PT-SH-FS-OI4459");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM4459");
        packet.getPost().getPubInfo().setTransactionId(timestamp);
        packet.getPost().getPubInfo().setInterfaceId("139");
        packet.getPost().getPubInfo().setInterfaceType("06");
        packet.getPost().getPubInfo().setOpId("999990144");
        packet.getPost().getPubInfo().setCountyCode("");
        packet.getPost().getPubInfo().setOrgId("");
        // packet.getPost().getPubInfo().setClientIP("121.40.205.162");
        packet.getPost().getPubInfo().setTransactionTime(timestamp);
        packet.getPost().getPubInfo().setRegionCode("210");
        return packet;
    }


    /**
     * 日流量查询
     *
     * @param userId
     * @return
     */
    public Packet getCommitPacket4723(String userId, String beginDate, String endDate) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4723");
        JSONObject businessParams = new JSONObject();
        businessParams.put("userId", "");
        businessParams.put("phoneId", userId);
        businessParams.put("endDate", endDate);
        businessParams.put("beginDate", beginDate);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "BOSS4723");
        return packet;
    }

    /**
     * 入网时间查询
     *
     * @param userId
     * @return
     */
    public Packet getCommitPacket4154(String userId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4723");
        JSONObject businessParams = new JSONObject();
        businessParams.put("bill_id", userId);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "CRM4154");
        return packet;
    }

    /**
     * 卡券发送
     *
     * @param userId
     * @return
     */
    public Packet sendCardToUser(String userId, String couponId) {
        Request request = new Request();
        JSONObject businessParams = new JSONObject();
        businessParams.put("userId", userId);
        businessParams.put("cityId", "-1");
        businessParams.put("couponId", couponId);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "OUTERKAJUAN");
        return packet;
    }

    /**
     * 2267 上海移动卡套类型查询 -  1：全球通；2：动感地带；3：神州行；0：其他
     *
     * @return
     */
    public Packet getCommitPacket2267(String mobile) {
        // 请求参数
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI2267");
        JSONObject businessParams = new JSONObject();
        businessParams.put("ServiceNum", mobile);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "CRMI2267");
        return packet;
    }

    /**
     * 实名校验
     *
     * @return
     */
    public Packet getCommitPacket3320(String mobile) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3320");
        JSONObject businessParams = new JSONObject();
        businessParams.put("ServiceNum", mobile);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "CRM3320");
        return packet;
    }

    /**
     * 积分校验
     *
     * @param userId        手机号
     * @param exchangeNum   积分兑换数量
     * @param exchangeCfgId 积分兑换项
     * @param cardNo        原因
     * @return
     */
    public Packet valPointsPacket(String userId, String exchangeNum, String exchangeCfgId, String cardNo) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3852");
        JSONObject businessParams = new JSONObject();
        businessParams.put("exchangeNum", exchangeNum);
        businessParams.put("billId", userId);
        businessParams.put("exchangeCfgId", exchangeCfgId);
        businessParams.put("cardNo", cardNo);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "CRM3852");
        return packet;
    }

    /**
     * 积分兑换
     *
     * @param userId        手机号
     * @param exchangeNum   积分兑换数量
     * @param exchangeCfgId 积分兑换项
     * @param orderId       原因
     * @return
     */
    public Packet exchangePointsPacket(String userId, String exchangeNum, String exchangeCfgId, String orderId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI4670");
        JSONObject businessParams = new JSONObject();
        businessParams.put("exchangeNum", exchangeNum);
        businessParams.put("billId", userId);
        businessParams.put("exchangeCfgId", exchangeCfgId);
        businessParams.put("orderId", orderId);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "CRM4670");
        return packet;
    }

    /**
     * 积分扣减
     *
     * @param userId
     * @param score    扣减积分数值
     * @param peerCode 流水号
     * @param reason   原因
     * @return
     */
    public Packet reducePointsPacket(String userId, String score, String peerCode, String reason) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI2239");
        JSONObject businessParams = new JSONObject();
        businessParams.put("ServiceNum", userId);
        businessParams.put("PeerCode", peerCode);
        businessParams.put("Score", score);
        businessParams.put("Reason", reason);
        request.setBusiParams(businessParams);
        Packet packet = getBasePacket(request, "CRM2239");
        return packet;
    }

    //接口入参（Request对象）
    public Packet getBasePacket(Request req, String apiCode) {
        PubInfo pubInfo = new PubInfo();
        String timestamp = sdf.format(new Date());
        pubInfo.setTransactionId(generateTransactionId());
        pubInfo.setTransactionTime(timestamp);
        if ("CRM4147".equals(apiCode)) {
            pubInfo.setInterfaceId("138");
        }
        if ("CRM3066".equals(apiCode)) {
            pubInfo.setOrgId("0");
        }
        if ("CRM4465".equals(apiCode)) {
            pubInfo.setOrgId("0");
            pubInfo.setInterfaceId("25");
            pubInfo.setInterfaceType("05");
        }
        if ("BOSS4723".equals(apiCode)) {
            pubInfo.setInterfaceId("137");
        }
        Post post = new Post();
        post.setPubInfo(pubInfo);
        post.setRequest(req);
        String mqMsg = JSON.toJSONString(post);
        System.out.println("===================================入参--" + mqMsg);
        Packet packet = new Packet();
        packet.setApiCode(apiCode);
        packet.setPost(post);
        return packet;
    }

    /**
     * 业务办理的短信下发
     *
     * @param userId
     * @param offerId
     * @return
     */
    public Packet getCommitPacket5956(String userId, String offerId) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI5956");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billId", userId);
        if(offerId.contains(",")){
            for (int i = 0; i < offerId.split(",").length; i++) {
                busiParams.put("offerId"+(i+1), offerId.split(",")[i]);
            }
        }else{
            busiParams.put("offerId1", offerId);
        }
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM5956");
        return packet;
    }

    /**
     * 业务二次确认办理
     *
     * @param userId
     * @param offerList
     * @param randCode
     * @param channel_id
     * @return
     */
    public Packet getCommitPacket306602(String userId, String randCode, List<VasOfferInfo> offerList, String channel_id) {
        Request request = new Request();
        request.setBusiCode("PT-SH-FS-OI3066");
        JSONObject busiParams = new JSONObject();
        busiParams.put("billid", userId);
        busiParams.put("randCode", randCode);
        busiParams.put("offerId1", offerList.get(0).getOfferId());
        if(offerList.size()>1){
            busiParams.put("offerId2", offerList.get(1).getOfferId());
        }
        busiParams.put("NeedSendMsg", "Y");
        busiParams.put("VasOfferInfo", offerList);
        if (channel_id.equals("leadeon")) {
            busiParams.put("xOrgId", "yjdq");
            busiParams.put("xOpId", "1000000002110700006");
        }
        JSONObject busiParams1 = new JSONObject();
        busiParams1.put("m_iOpEntityId", "0");
        busiParams1.put("m_iVestOrgId", "0");
        busiParams.put("sPrivData", busiParams1);
        busiParams.put("ServiceNum", userId);
        request.setBusiParams(busiParams);
        Packet packet = getBasePacket(request, "CRM306602");
        packet.getPost().getPubInfo().setInterfaceId("25");
        return packet;
    }

    /**
     * 业务办理集团上报
     * @param thirdTradeId
     * @param channelId
     * @param uniChannelId
     * @param purchaseChannelName
     * @param charge
     * @param spayCharge
     * @param billCharge
     * @param orderType
     * @param customerId
     * @param orderSubType
     * @param createTime
     * @param payTime
     * @param closeTime
     * @param discountType
     * @param payType
     * @param busiStatus
     * @param platformCode
     * @param orderItemId
     * @param commodityId
     * @param commodityName
     * @param commodityNum
     * @param commodityType
     * @param skuId
     * @param skuName
     * @param unitPrice
     * @param bossId
     * @param wtAcId
     * @param wtAc
     * @return
     */
    public Packet orderReporting(String thirdTradeId,String channelId,String uniChannelId,String purchaseChannelName,String charge,
                            String spayCharge,String billCharge,String orderType,String customerId,String orderSubType,String createTime,String payTime,
                            String closeTime,String discountType,String payType,String busiStatus,String platformCode,String orderItemId,String commodityId,
                            String commodityName,String commodityNum,String commodityType,String skuId,String skuName,String unitPrice,String bossId,String wtAcId,String wtAc){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("logType","1");//必传 订单类型（1普通 2宽带 3号卡
        JSONObject busiParams = new JSONObject();//
        busiParams.put("thirdTradeId",thirdTradeId);//JYRZ请求渠道原始的订单编号，最长64位同一省份下单渠道ID的交易单编码不能重复
        busiParams.put("systemId", "13");//请求系统编码
        busiParams.put("channelId", channelId);//受理渠道102上海移动商城H5、007星火店铺、008一级云店H5
        busiParams.put("charge", charge);//订单金额
        busiParams.put("spayCharge", spayCharge);//应付金额
        busiParams.put("billCharge",billCharge);//实付金额
        busiParams.put("orderType", orderType);//订单类型
        busiParams.put("orderSubType", orderSubType);//订单子类型
        busiParams.put("customerId", customerId);//用户ID
        busiParams.put("createTime", createTime);//订单创建时间
        busiParams.put("payTime", payTime);//订单支付时间
        busiParams.put("closeTime", closeTime);//订单完成时间
        busiParams.put("discountType", discountType);//优惠类型
        busiParams.put("payType", payType);//支付方式
        busiParams.put("busiStatus", busiStatus);//订单状态
        busiParams.put("province", "");//省份/商户编码
        busiParams.put("platformCode", platformCode);//业务上架平台编码（eg:省份H5通过星火平台上架/分享，传 02即可）
        //00：其他01：在线工具02：星火平台03：中国移动APP（APP整合分省订单同步传此值）
        busiParams.put("phoneNo", "");//被充值号码
        busiParams.put("chargeFee", "");//被充值金额
        busiParams.put("chargeUnit", "");//充值单位
        busiParams.put("payChannel", "");//支付渠道
        busiParams.put("authSourceId", "");//移动认证分配的sourceId，用于订单详情页面跳转的单点登录。
        busiParams.put("cmOrderId", "");//一级电渠订单号(由一级渠道创建订单并落地到省份)
        busiParams.put("uniChannelId",uniChannelId);//19位全网统一渠道编码
        busiParams.put("purchaseChannelName", purchaseChannelName);//用户购买渠道名称，中文
        busiParams.put("assistantUserInfo", "");//推荐人附加信息
        busiParams.put("nickName", "");//用户昵称
        busiParams.put("bindCusId", "");//推荐人ID
        busiParams.put("bindCusNo", "");//推荐人手机号
        busiParams.put("cardType", "");//卡券类型话费 hf01流量 ll01实物 sw01套餐 tc01通用 ty01花卡宝藏版 bzk01花卡 hk01合作券 hz01咪咕券 99hz肯德基券 898KFC
        busiParams.put("cardName", "");//卡券名称
        busiParams.put("cardUnit", "");//卡券单位
        busiParams.put("cardValue", "");//卡券面值
        busiParams.put("cardNo", "");//卡券编码
        busiParams.put("deliverWay", "");//配送方式SP：门店自提ED：物流配送DA：上门激活
        busiParams.put("orderPostName", "");//收货人姓名
        busiParams.put("orderPostPhone", "");//收货人手机号
        busiParams.put("orderPostAddr", "");//收货地址（当配送方式为SP，该字段表示 自提地址门店名称）
        busiParams.put("invoiceType", "");//发票类型
        busiParams.put("invoiceCustomerType", "");//发票客户类型
        busiParams.put("invoiceTitle", "");//发票抬头
        busiParams.put("invoiceTaxNo", "");//发票税号
        busiParams.put("invoiceRemark", "");//发票备注
        busiParams.put("invoiceContent", "");//发票内容
        busiParams.put("invoiceMail", "");//收票人邮箱
        busiParams.put("freight", "");//运费，单位：分，若有运费，必传
        busiParams.put("creditAmount", "");//减免金额 单位：分
        busiParams.put("distributionId", "");//物流单号，配送方式为物流配送时必传
        busiParams.put("deliveryCode", "");//提货码
        busiParams.put("wtAcId", wtAcId);//触点码或者IOP营销码
        busiParams.put("effectiveType", "");//生效方式01：立即生效；02：下月生效
        busiParams.put("orderDetailUrl", "");//订单详情页链接
        JSONObject new_busiParams = new JSONObject();
        JSONArray newArray = new JSONArray();//
        new_busiParams.put("orderItemId", orderItemId);//订单项ID。若一笔订单存在多个订单项时必填。
        new_busiParams.put("commodityId", commodityId);// 商品id
        new_busiParams.put("netExpensesCode", commodityId);//全网统一资费编码
        new_busiParams.put("commodityName", commodityName);//商品名称 （商品名或者商品简要描述信息）
        new_busiParams.put("commodityUrl", "");//商品链接
        new_busiParams.put("commodityVersion", "");//商品版本号
        new_busiParams.put("commodityNum", commodityNum);//商品数量，默认1
        new_busiParams.put("commodityCode", "");//商品编码
        new_busiParams.put("commodityType", commodityType);//商品类型
        new_busiParams.put("saleType", "");//商品销售类型
        new_busiParams.put("skuId", skuId);//skuId，没有传 –
        new_busiParams.put("skuName", skuName);//sku名称，没有传 –
        new_busiParams.put("sourceChannel", "");//引流渠道，星火/云店渠道引流订单必传。星火：007云店：008星火省份自有：017
        new_busiParams.put("unitPrice", unitPrice);//单价 单位：分
        new_busiParams.put("discountPrice", "");//优惠前金额
        new_busiParams.put("price", "");//优惠后金额
        new_busiParams.put("discountCharge", "");//优惠金额
        new_busiParams.put("shopName", "");// 店铺名称（云店平台订单必传）
        new_busiParams.put("withContract", "");//是否合约机Y表示是N表示不是
        new_busiParams.put("contractGearName", "");//
        new_busiParams.put("bossId", bossId);//bossId，多个值以英文逗号分割
        new_busiParams.put("merchantName", "");//商户名称
        new_busiParams.put("imgUrl", "");//商品图片url
        new_busiParams.put("wtAc", wtAc);//来源触点和策略 ID
        new_busiParams.put("merchantId", "");//商户编码（全网统一渠道编码）
        new_busiParams.put("shopId", "");//店铺id（云店平台订单必传）
        new_busiParams.put("aggPageCode", "");//记录聚合页面商品
        JSONObject new_last_busiParams = new JSONObject();
        JSONArray new_newArray = new JSONArray();
        new_last_busiParams.put("specId", "");//规格属性ID
        new_last_busiParams.put("specValue", "");//规格属性值
        new_newArray.add(new_last_busiParams);
        new_busiParams.put("orderItemSpecs",new_newArray);//（订单明细规格）
        newArray.add(new_busiParams);
        busiParams.put("orderItems", newArray);//订单项列表
        jsonObject.put("busInfo",busiParams);
        Packet packet = getOUTER0001Packet(jsonObject, "addOrder");
        return packet;
    }
    /**
     * 权益订购
     *
     * @param phone
     * @param randCode
     * @param offerId
     * @param channelCode
     * @return
     */
    public Packet purchaseNew02(String phone, String randCode, String offerId, String channelCode) {
        JSONObject busiParams = new JSONObject();
        busiParams.put("idType", "1");//默认1
        busiParams.put("randCode", randCode);//验证码
        busiParams.put("phone", phone);//订购手机号码
        busiParams.put("opid", "999990144");//工号
        busiParams.put("offerId", offerId);//策划ID
        busiParams.put("channelOrderNo", sdf.format(new Date()));//接入方（渠道）订单流水号，每笔订单唯一
        busiParams.put("orgid", "0");//组织编号
        busiParams.put("channelCode", channelCode);//渠道号，测试环境CH5，正式环境H5
        Packet packet = getOUTER0001Packet(busiParams, "purchaseNew02");
        return packet;
    }

    //接口入参数（json数据）
    public static Packet getOUTER0001Packet(JSONObject req, String apiCode) {
        String mqMsg = JSON.toJSONString(req);
        System.out.println("===================================入参--" + mqMsg);
        Packet packet = new Packet();
        packet.setApiCode(apiCode);
        packet.setObject(req);
        return packet;
    }

    //TransactionId格式定义
    private String generateTransactionId() {
        String timestamp = sdf.format(new Date());
        SecureRandom rand = new SecureRandom();
        int randNum = rand.nextInt(10000);
        String pattern = "0000";
        DecimalFormat df = new DecimalFormat(pattern);
        return "WXHJS" + timestamp + df.format(randNum);
    }

}
