package com.kawahedukasi.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kawahedukasi.user.models.Consumer;
import com.kawahedukasi.user.models.Credential;
import com.kawahedukasi.user.models.Token;
import com.kawahedukasi.user.models.User;
import com.kawahedukasi.user.constants.HttpConstant;
import com.kawahedukasi.user.utils.SimpleResponse;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UserService {

    Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @ConfigProperty(name = "kong.app.name")
    private String kongAppName;

    @ConfigProperty(name = "kong.app.redirect.uris")
    private String kongAppRedirectUris;

    @Inject
    KongService kongService;

    public SimpleResponse login(Object param){
        try {
            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            om.setDateFormat(new SimpleDateFormat());

            Map<String,Object> map = om.convertValue(param, Map.class);
            String loginName = (String) map.getOrDefault("loginName", "");

            User user = User.find("loginName", loginName).firstResult();
            if(user == null){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User tidak ditemukan", new String());
            }

            String password = (String) map.getOrDefault("password", "");
            if(!user.getPassword().equals(password)){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "Password salah", new String());
            }

            Consumer consumer = kongService.getConsumer(user.getLoginName());
            Token token = kongService.getAccessToken(consumer);

            Map<String, Object> response = om.convertValue(user, Map.class);
            response.put("token", token);

            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, response);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new SimpleResponse(HttpConstant.FAILED_CODE, HttpConstant.FAILED, new String());
        }
    }

    @Transactional
    public SimpleResponse signup(Object param){
        try {
            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            om.setDateFormat(new SimpleDateFormat());

            Map<String,Object> map = om.convertValue(param, Map.class);
            if(User.count("loginName", map.getOrDefault("loginName", ""))>0){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "Login name sudah digunakan", new String());
            }

            User user = new User();
            user.setFullName((String) map.getOrDefault("fullName", ""));
            user.setLoginName((String) map.getOrDefault("loginName", ""));
            user.setPassword((String) map.getOrDefault("password", ""));
            user.setEmail((String) map.getOrDefault("email", ""));
            user.setPhoneNumber((String) map.getOrDefault("phoneNumber", ""));
            user.setAddress((String) map.getOrDefault("address", ""));
            user.persist();
            user.setUserCode(String.format("%07d", user.getUserId()));
            user.persist();

            Map<String, Object> customId = new HashMap<>();
            customId.put("loginName", user.getLoginName());
            customId.put("fullName", user.getFullName());
            customId.put("email", user.getEmail());
            customId.put("phoneNumber", user.getPhoneNumber());
            customId.put("userCode", user.getUserCode());
            customId.put("userId", user.getUserId());

            Consumer consumer = kongService.createConsumer(user.getLoginName(), om.writeValueAsString(customId));
            Credential credential = kongService.createCredential(consumer.username, kongAppName, List.of(kongAppRedirectUris));
            Token token = kongService.getAccessToken(consumer);

            Map<String, Object> response = om.convertValue(user, Map.class);
            response.put("token", token);

            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, response);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new SimpleResponse(HttpConstant.FAILED_CODE, e.getMessage(), new String());
        }
    }

    public SimpleResponse getUser(Object param){
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String,Object> map = om.convertValue(param, Map.class);

            Long userId = Long.parseLong(map.getOrDefault("userId", -99L).toString());
            if(userId.equals(-99L)){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "Harus menyertakan user id", new String());
            }
            User user = User.findById(userId);
            if(user==null){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User tidak ditemukan", new String());
            }

            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, user);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new SimpleResponse(HttpConstant.FAILED_CODE, HttpConstant.FAILED, new String());
        }
    }


    @Transactional
    public SimpleResponse updateUser(Object param){
        try {
            ObjectMapper om = new ObjectMapper();
            om.registerModule(new JavaTimeModule());
            om.setDateFormat(new SimpleDateFormat());

            Map<String,Object> map = om.convertValue(param, Map.class);
            Long userId = Long.parseLong(map.getOrDefault("userId", -99L).toString());
            if(userId.equals(-99L)){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User id harus disertakan", new String());
            }
            User user = User.findById(userId);
            if (user == null){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User tidak ditemukan", new String());
            }
            if(User.count("loginName", map.getOrDefault("loginName", ""))>0){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "Login name sudah digunakan", new String());
            }
            user.setFullName((String) map.getOrDefault("fullName", ""));
            user.setLoginName((String) map.getOrDefault("loginName", ""));
            user.setPassword((String) map.getOrDefault("password", ""));
            user.setEmail((String) map.getOrDefault("email", ""));
            user.setPhoneNumber((String) map.getOrDefault("phoneNumber", ""));
            user.setAddress((String) map.getOrDefault("address", ""));
            user.setUserCode(String.format("%07d", user.getUserId()));
            user.persist();

            Map<String, Object> customId = new HashMap<>();
            customId.put("loginName", user.getLoginName());
            customId.put("fullName", user.getFullName());
            customId.put("email", user.getEmail());
            customId.put("phoneNumber", user.getPhoneNumber());
            customId.put("userCode", user.getUserCode());
            customId.put("userId", user.getUserId());

            Consumer consumer = kongService.updateConsumer(user.getLoginName(), om.writeValueAsString(customId));

            Map<String, Object> response = om.convertValue(user, Map.class);

            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, response);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new SimpleResponse(HttpConstant.FAILED_CODE, e.getMessage(), new String());
        }
    }
}