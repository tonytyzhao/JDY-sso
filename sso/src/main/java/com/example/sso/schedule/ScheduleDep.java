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
public class ScheduleDep {

    @Scheduled(cron = "0 0/30 9-19 * * ?")
    public void SynDep() throws KeyManagementException, NoSuchAlgorithmException {
        String accessToken = WXUtil.getAccessToken("wlb07f8823a2","qGw13PqGCL5-ZQK2mOIri9pUmTBwFODkh2M2F_EJfVY");
        String GET_DEP_URL = "https://htyx.casicloud.com/cgi-bin/department/list?access_token=ACCESS_TOKEN";
        String requestUrl = GET_DEP_URL.replace("ACCESS_TOKEN", accessToken);
        JSONObject jsonObject = JSON.parseObject(HttpUtil.sendGet(requestUrl));
        JSONArray depList = jsonObject.getJSONArray("department");
        StringBuffer JdyDep = new StringBuffer("{\"departments\":[");
        if(depList.size()>0){
            for(int i=0;i<depList.size();i++){
                JSONObject dep = depList.getJSONObject(i);
                String depNo = dep.getString("id");
                String name = dep.getString("name");
                String parentNo = dep.getString("parentid");
                System.out.println(dep);
                JdyDep.append("{\"dept_no\":");
                JdyDep.append(depNo);
                JdyDep.append(",\"name\":\"");
                JdyDep.append(name);
                JdyDep.append("\",\"parent_no\":");
                if (depNo.equals("8")) {
                    JdyDep.append("1");
                }else {
                    JdyDep.append(parentNo);
                }
                JdyDep.append("}");
                if (i < depList.size() - 1) {
                    JdyDep.append(",");
                }
            }
        }
        JdyDep.append("]}");
        System.out.println(JdyDep);
        String SYN_DEP_URL = "https://api.jiandaoyun.com/api/v2/department/import";
        System.out.println(HttpUtil.sendPost(SYN_DEP_URL,JdyDep.toString()));
    }
}
