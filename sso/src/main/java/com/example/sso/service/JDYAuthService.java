package com.example.sso.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.sso.util.HttpUtil;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Service
@NoArgsConstructor
public class JDYAuthService {
    public static final String GET_USERINFO_URL = "https://htyx.casicloud.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE";

    public String getUserID(String accessToken, String code) throws NoSuchAlgorithmException, KeyManagementException {
        //1.获取请求的url
        String get_userInfo_url = GET_USERINFO_URL.replace("ACCESS_TOKEN", accessToken)
                .replace("CODE", code);

        //2.调用接口，发送请求，获取成员信息
        JSONObject jsonObject = JSON.parseObject(HttpUtil.sendGet(get_userInfo_url));
        return jsonObject.getString("UserId");
    }
}

