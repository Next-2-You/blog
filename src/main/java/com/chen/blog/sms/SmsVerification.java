package com.chen.blog.sms;


import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信验证服务
 */
public class SmsVerification {
	protected static final Logger log = LoggerFactory.getLogger(SmsVerification.class);

    static final String accessKeyId = "";
    static final String accessKeySecret = "";

    //官方参数
    static final String product = "Dysmsapi";
    static final String domain = "dysmsapi.aliyuncs.com";


    public static boolean sendSms(String phoneNumber,String code) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "60000");
        System.setProperty("sun.net.client.defaultReadTimeout", "60000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("博客系统");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_185211187");
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        request.setTemplateParam(JSON.toJSONString(map));


        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if("OK".equals(sendSmsResponse.getCode())) {
            return true;
        }else {
            log.error("短信服务发生错误,错误code: "+sendSmsResponse.getCode());
            return false;
        }
    }

}
