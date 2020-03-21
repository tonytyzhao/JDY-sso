package com.example.sso.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.sso.util.HttpUtil;
import com.example.sso.util.WXUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Component
public class ScheduleUser {
    @Scheduled(cron = "0 0/30 9-19 * * ?")
    public void SynDep() throws KeyManagementException, NoSuchAlgorithmException {
        String accessToken = WXUtil.getAccessToken("wlb07f8823a2","qGw13PqGCL5-ZQK2mOIri9pUmTBwFODkh2M2F_EJfVY");
        String GET_USER_URL = "https://htyx.casicloud.com/cgi-bin/user/simplelist?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD";
        String requestUrl = GET_USER_URL.replace("ACCESS_TOKEN", accessToken)
                .replace("DEPARTMENT_ID","8")
                .replace("FETCH_CHILD","1");
        JSONObject jsonObject = JSON.parseObject(HttpUtil.sendGet(requestUrl));
        StringBuffer userInfo = new StringBuffer("{\"users\": [");
        JSONArray userList = jsonObject.getJSONArray("userlist");
        if (userList.size() > 0) {
            for(int i=0;i<userList.size();i++){
                JSONObject user = userList.getJSONObject(i);
                String username = user.getString("userid");
                StringBuffer stringBuffer = new StringBuffer(username);
                StringBuffer strUserId = new StringBuffer("");
                if (stringBuffer.length()>12) {
                    strUserId.append(stringBuffer.substring(0,12));
                }else {
                    strUserId.append(stringBuffer.toString());
                }
                String cutUserId = strUserId.toString();
                String name = user.getString("name");
                String dep = user.getString("department");
                System.out.println(cutUserId);
                System.out.println(name);
                System.out.println(dep);
                userInfo.append("{\"username\": \"");
                userInfo.append(cutUserId);
                userInfo.append("\",\"name\": \"");
                userInfo.append(name);
                userInfo.append("\",\"departments\": ");
                userInfo.append(dep);
                userInfo.append("}");
                if (i < userList.size() - 1) {
                    userInfo.append(",");
                }
            }
        }
        userInfo.append("]}");
        System.out.println(userInfo);
        String ADD_USER_URL = "https://api.jiandaoyun.com/api/v2/user/import";
        System.out.println(HttpUtil.sendPost(ADD_USER_URL,userInfo.toString()));
    }
}
