package com.example.sso.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.sso.config.SSOConfig;
import com.example.sso.service.*;
import com.example.sso.util.HttpUtil;
import com.example.sso.util.WXUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@RestController
@NoArgsConstructor
@AllArgsConstructor
class SSOController {
    @Getter @Setter @Autowired private SSOConfig ssoConfig;
    @Getter @Setter @Autowired private SSOService ssoService;
    @Getter @Setter @Autowired private JDYAuthService jdyAuthService;

    @GetMapping("/user")
    public void jdyAuth(
            @RequestParam(name = "code",defaultValue = "") String code,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    ) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        String accessToken = WXUtil.getAccessToken("wlb07f8823a2","qGw13PqGCL5-ZQK2mOIri9pUmTBwFODkh2M2F_EJfVY");
        String userID = jdyAuthService.getUserID(accessToken, code);
        StringBuffer stringBuffer = new StringBuffer(userID);
        StringBuffer strUserId = new StringBuffer("");
        if (stringBuffer.length()>12) {
            strUserId.append(stringBuffer.substring(0,12));
        }else {
            strUserId.append(stringBuffer.toString());
        }
        String cutUserId = strUserId.toString();
        System.out.println(cutUserId);
        HttpSession httpSession = httpServletRequest.getSession(true);
        httpSession.setAttribute("user",cutUserId);
        httpServletResponse.sendRedirect(
                String.format("https://www.jiandaoyun.com/sso/custom/5e61bee299f9a30006d7f44b/iss")
        );
    }

    @GetMapping("/sso")
    public void authn(
            @RequestParam(name = "request", defaultValue = "") String request,
            @RequestParam(name = "state", defaultValue = "") String state,
            HttpServletResponse httpServletResponse,
            HttpServletRequest httpServletRequest
    ) throws IOException {
        HttpSession httpSession = httpServletRequest.getSession();
        String username = (String) httpSession.getAttribute("user");
        System.out.println(request);
        System.out.println(state);
        System.out.println(username);
        String response = this.ssoService.getResponse(request,username);
        System.out.println(response);
        String redApp = "https%3A%2F%2Fwww.jiandaoyun.com/dashboard#/app/5e61e999b81e820006212620";
        httpServletResponse.sendRedirect(
                String.format(
                        "%s?response=%s&state=%s&redirect_uri=%s",
                        this.ssoConfig.getAcs(), response, state, redApp
                )
        );
    }

    @PostMapping("/message")
    public void yxMes(@RequestBody JSONObject jsonMes) throws KeyManagementException, NoSuchAlgorithmException {
        String op = jsonMes.getString("op");
        if(op.equals("data_create_message")) {
            JSONObject data = jsonMes.getJSONObject("data");
            JSONArray to = data.getJSONArray("to");
            String notifyText = data.getString("notify_text");
            StringBuffer reqMes = new StringBuffer("{\"touser\" : \"");
            if (to.size() > 0) {
                for (int i=0;i<to.size();i++) {
                    JSONObject touser = to.getJSONObject(i);
                    String username = touser.getString("username");
                    if (i < to.size() - 1) {
                        reqMes.append(username);
                        reqMes.append("|");
                    }else {
                        reqMes.append(username);
                    }
                }
            }
            reqMes.append("\",\"msgtype\" : \"text\",\"agentid\" : 1000226,\"text\" : {\"content\" : \"");
            reqMes.append(notifyText);
            reqMes.append("\"}}");
            System.out.println(reqMes);
            String accessToken = WXUtil.getAccessToken("wlb07f8823a2","qGw13PqGCL5-ZQK2mOIri9pUmTBwFODkh2M2F_EJfVY");
            String SEND_MES_URL = "https://htyx.casicloud.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";
            String sendMesUrl = SEND_MES_URL.replace("ACCESS_TOKEN",accessToken);
            System.out.println(HttpUtil.sendPost(sendMesUrl,reqMes.toString()));
        }
    }
}
