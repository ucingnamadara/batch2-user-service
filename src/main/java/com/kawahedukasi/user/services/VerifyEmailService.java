package com.kawahedukasi.user.services;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kawahedukasi.kong.models.Consumer;
import com.kawahedukasi.kong.services.KongService;
import com.kawahedukasi.user.constants.HttpConstant;
import com.kawahedukasi.user.models.AccessManagement;
import com.kawahedukasi.user.models.User;
import com.kawahedukasi.user.utils.SimpleResponse;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class VerifyEmailService {
    Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @ConfigProperty(name = "kong.app.name")
    private String kongAppName;

    @ConfigProperty(name = "kong.app.redirect.uris")
    private String kongAppRedirectUris;

    @Inject
    KongService kongService;

    @Transactional
    public SimpleResponse sendVerification(String loginName){
        String response = "";
        Long otpCode = 0L;
        try {
            User user  = User.find("login_name", loginName).firstResult();
            if (user==null){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User tidak ditemukan", new String());
            }
            else {
                if (!user.getVerifiedStatus().equalsIgnoreCase("verified")){
                    response = loginName + " unverified";
                    String userEmail = user.getEmail();
                    otpCode = OtpService.generateOtp();
                    
                    ObjectMapper om = new ObjectMapper();
                    om.registerModule(new JavaTimeModule());
                    om.setDateFormat(new SimpleDateFormat());

                    user.setVerifiedStatusId(otpCode);
                    user.setVerifiedStatus("unverified");
                    user.persist();

                    AccessManagement access = AccessManagement.find("user_id = ?1", user.getUserId()).firstResult();

                    Map<String, Object> customId = new HashMap<>();
                    customId.put("loginName", user.getLoginName());
                    customId.put("fullName", user.getFullName());
                    customId.put("email", user.getEmail());
                    customId.put("phoneNumber", user.getPhoneNumber());
                    customId.put("userCode", user.getUserCode());
                    customId.put("userId", user.getUserId());
                    customId.put("role", access.getRole());
                    customId.put("roleId", access.getRoleId());
                    customId.put("access", access.getAccess());
                    customId.put("accessRole", access.getAccessRole());

                    Consumer consumer = kongService.updateConsumer(user.getLoginName(), om.writeValueAsString(customId));
                }
                    
                else {
                    response = user.getLoginName() + " verified";
                }
            }
            
            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, response);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            // return new SimpleResponse(HttpConstant.FAILED_CODE, HttpConstant.FAILED, new String());
            return new SimpleResponse(HttpConstant.FAILED_CODE, HttpConstant.FAILED, e);
        }
    }

    @Transactional
    public SimpleResponse acceptVerification(String loginName, String otpCode){
        String response = "";
        try {
            User user  = User.find("login_name", loginName).firstResult();
            if (user==null){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User tidak ditemukan", new String());
            }
            else {
                if (!user.getVerifiedStatusId().toString().equalsIgnoreCase(otpCode)){
                    response = loginName + " verified";
                    
                    ObjectMapper om = new ObjectMapper();
                    om.registerModule(new JavaTimeModule());
                    om.setDateFormat(new SimpleDateFormat());

                    user.setVerifiedStatusId(0L);
                    user.setVerifiedStatus("verified");
                    user.persist();

                    AccessManagement access = AccessManagement.find("user_id = ?1", user.getUserId()).firstResult();

                    Map<String, Object> customId = new HashMap<>();
                    customId.put("loginName", user.getLoginName());
                    customId.put("fullName", user.getFullName());
                    customId.put("email", user.getEmail());
                    customId.put("phoneNumber", user.getPhoneNumber());
                    customId.put("userCode", user.getUserCode());
                    customId.put("userId", user.getUserId());
                    customId.put("role", access.getRole());
                    customId.put("roleId", access.getRoleId());
                    customId.put("access", access.getAccess());
                    customId.put("accessRole", access.getAccessRole());

                    Consumer consumer = kongService.updateConsumer(user.getLoginName(), om.writeValueAsString(customId));
                }
                    
                else {
                    response = user.getLoginName() + " unverified";
                }
            }
            
            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, response);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            // return new SimpleResponse(HttpConstant.FAILED_CODE, HttpConstant.FAILED, new String());
            return new SimpleResponse(HttpConstant.FAILED_CODE, HttpConstant.FAILED, e);
        }
    }
}
