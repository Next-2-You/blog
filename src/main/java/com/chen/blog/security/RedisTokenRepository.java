package com.chen.blog.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chen.blog.common.Constant;
import com.chen.blog.entity.User;
import com.chen.blog.redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class RedisTokenRepository implements PersistentTokenRepository {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        String userJson = token.getUsername();
        User user = JSON.parseObject(userJson, User.class);
        String key = Constant.connectRememberToken(token.getSeries());
        JSONObject result = new JSONObject();
        result.put("loginNums",user.getAccount());
        result.put("user", userJson);
        result.put("tokenValue", token.getTokenValue());
        result.put("date", String.valueOf(token.getDate().getTime()));
        redisUtils.setKeyAndTimeOut(key,7*24*3600,result.toJSONString());
    }

    @Override
    public void updateToken(String series, String tokenValue, Date date) {
        String key = Constant.connectRememberToken(series);
        String json = redisUtils.getValue(key);
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("tokenValue", tokenValue);
        jsonObject.put("date", String.valueOf(date.getTime()));
        redisUtils.setKeyAndTimeOut(key,7*24*3600,jsonObject.toJSONString());
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {
        String key = Constant.connectRememberToken(series);
        String json = redisUtils.getValue(key);
        if (json != null && !json.trim().equals("")) {
            JSONObject jsonObject = JSON.parseObject(json);
            String loginNums = jsonObject.getString("loginNums");
            String user = jsonObject.getString("user");
            String tokenValue = jsonObject.getString("tokenValue");
            String date = jsonObject.getString("date");
            if (null == user || null == loginNums || null == tokenValue || null == date) {
                return null;
            }
            Long timestamp = Long.valueOf(date);
            Date time = new Date(timestamp);
            PersistentRememberMeToken token = new PersistentRememberMeToken(loginNums, series, tokenValue, time);
            return token;
        }
        return null;
    }

    @Override
    public void removeUserTokens(String userJson) {
        User user = JSON.parseObject(userJson, User.class);
        String account = user.getAccount();
        String keys = Constant.connectRememberToken("*");
        Set<String> tokens = redisUtils.getKeys(keys);
        for (String token : tokens) {
            String json = redisUtils.getValue(token);
            String saveAccount = JSON.parseObject(json).getString("loginNums");
            if (account.equals(saveAccount)) {
                redisUtils.delKey(token);
                break;
            }
        }
    }
}
