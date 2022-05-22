package com.kawahedukasi.user.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;

public class EmailUtil {

    public static Map<String, String> verifyEmailMessage(String loginName, String otpCode, String email){
        String baseUrl = ConfigProvider.getConfig().getValue("web.url.base", String.class);

        String subject = "Verifikasi Email Demodi App";

        String to = email;

        String body = "Tekan link berikut untuk verifikasi email: \n"
            + String.format("%s/api/v1/user/%s/acceptVerification?otpCode=%s",
            baseUrl, loginName, otpCode);
        
        Map<String, String> message = new HashMap<>();
        message.put("to", to);
        message.put("subject", subject);
        message.put("body", body);

        return message;
    }
}
