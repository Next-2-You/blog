package com.chen.blog.utils;



import com.alibaba.druid.sql.visitor.functions.Hex;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.aspectj.weaver.ast.Var;
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OthersUtils {


    private static String DATE_PATTERN = "yyyy-MM-dd";

    private static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static DateTimeFormatter formatterNoTime = DateTimeFormatter.ofPattern(DATE_PATTERN);


    /**
     * 创建手机验证码
     *
     * @param digit
     * @return
     */
    public static String createCode(Integer digit){
        String random = Math.random() + "";
        random = random.substring(random.indexOf(".") + 1, random.indexOf(".") + digit + 1);
        return random;
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
       return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    /**
     * 生成taken
     *
     * 4fb22316c04a4e6baf7f364e5131215f
     *
     * @return
     */
    public static String createCodeToken() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 得到当天剩余秒数
     *
     * @return
     */
    public static long getCurrentDateRemainSeconds() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        return ChronoUnit.SECONDS.between(now, tomorrow);
    }


    /**
     * 得到创建时间
     * @return
     */
    public static LocalDateTime getCreateTime(){
        return LocalDateTime.now();
    }

    /**
     * 得到日期
     * @return
     */
    public static LocalDate getBirthday(){
        return LocalDate.now();
    }





    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DATE_TIME_PATTERN);
    }

    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    /**
     * str字符串转List
     *
     * str字符串使用“，”分隔
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> changeStrToList(String str,Class<T> clazz){
        if (str == null || str.trim().equals("")) {
            JSONArray array = new JSONArray();
            return JSON.parseArray(array.toString(),clazz);
        }
        String[] data = str.split(",");
        String array = JSON.toJSONString(data);
        return JSON.parseArray(array, clazz);
    }

    public static boolean isBlank(String s){
        return s != null && !s.trim().equals("");
    }


    public static void main(String[] args) {
        List<Long> idLong = changeStrToList("1,2,4", Long.class);
        List<String> idStr = changeStrToList("1,2,3,4", String.class);
        List<Long> test = changeStrToList(null, Long.class);

    }


}
