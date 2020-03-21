package com.example.sso.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class WXUtil {
    public static String ACCESS_TOKEN_URL = "https://htyx.casicloud.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRET";

    public static String getAccessToken(String appid, String appsecret) throws NoSuchAlgorithmException, KeyManagementException {
        String requestUrl = ACCESS_TOKEN_URL.replace("ID", appid)
                .replace("SECRET", appsecret);
        JSONObject jsonObject = JSON.parseObject(HttpUtil.sendGet(requestUrl));
        return jsonObject.getString("access_token");
    }
}
