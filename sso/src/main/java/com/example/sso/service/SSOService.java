package com.example.sso.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.sso.config.SSOConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class SSOService {
    @Getter @Setter @Autowired private SSOConfig ssoConfig;

    public String getResponse(String request,String username) {
        Algorithm algorithm = Algorithm.HMAC256(this.ssoConfig.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("com.jiandaoyun")
                .build();
        DecodedJWT decoded = verifier.verify(request);
        if (!"sso_req".equals(decoded.getClaim("type").asString())) {
            return "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return JWT.create()
                .withIssuer("com.jiandaoyun")
                .withClaim("type", "sso_res")
                .withClaim("username", username)
                .withAudience("com.jiandaoyun")
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }
}