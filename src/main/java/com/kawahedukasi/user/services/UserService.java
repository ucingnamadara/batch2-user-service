package com.kawahedukasi.user.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kawahedukasi.kong.models.Consumer;
import com.kawahedukasi.kong.models.Credential;
import com.kawahedukasi.kong.models.Token;
import com.kawahedukasi.kong.services.KongService;
import com.kawahedukasi.user.models.AccessManagement;
import com.kawahedukasi.user.models.User;
import com.kawahedukasi.user.constants.HttpConstant;
import com.kawahedukasi.user.models.UserAddress;
import com.kawahedukasi.user.utils.DateUtil;
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

            Consumer consumer = kongService.createConsumer(user.getLoginName(), om.writeValueAsString(customId));
            Credential credential = kongService.createCredential(consumer.username, kongAppName, List.of(kongAppRedirectUris));
            Token token = kongService.getAccessToken(consumer);

            Map<String, Object> response = new HashMap<>();
            response.put("loginName", user.getLoginName());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("role", access.getRole());
            response.put("roleId", access.getRoleId());
            response.put("access", access.getAccess());
            response.put("accessRole", access.getAccessRole());
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

            Map<String,Object> req = om.convertValue(param, Map.class);
            if(User.count("loginName", req.getOrDefault("loginName", ""))>0){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "Login name sudah digunakan", new String());
            }

            User user = new User();
            user.setFullName((String) req.getOrDefault("fullName", ""));
            user.setNickName((String) req.getOrDefault("nickName", ""));
            user.setLoginName((String) req.getOrDefault("loginName", ""));
            user.setPassword((String) req.getOrDefault("password", ""));
            user.setEmail((String) req.getOrDefault("email", ""));
            user.setPhoneNumber((String) req.getOrDefault("phoneNumber", ""));
            user.setIdCardNumber((String) req.getOrDefault("idCardNumber", ""));
            user.setIdCardType((String) req.getOrDefault("idCardType", ""));
            user.setIdCardTypeId(Long.parseLong(req.getOrDefault("idCardTypeId", -99L).toString()));
            user.setDateOfBirth(DateUtil.convertStringtoLocalDate(req.getOrDefault("dateOfBirth", "9999-12-28").toString()));
            user.setPlaceOfBirth((String) req.getOrDefault("placeOfBirth", ""));
            user.setNationality((String) req.getOrDefault("nationality", ""));
            user.setNationalityId(Long.parseLong(req.getOrDefault("nationalityId", -99L).toString()));

            user.persist();
            user.setUserCode(String.format("%07d", user.getUserId()));
            user.persist();

            UserAddress userAddress = new UserAddress();
            userAddress.setUserId(user);
            userAddress.setMainAddress((String) req.getOrDefault("mainAddress", ""));
            userAddress.setCountry((String) req.getOrDefault("country", ""));
            userAddress.setCountryId(Long.parseLong(req.getOrDefault("countryId", -99L).toString()));
            userAddress.setProvince((String) req.getOrDefault("province", ""));
            userAddress.setProvinceId(Long.parseLong(req.getOrDefault("provinceId", -99L).toString()));
            userAddress.setCity((String) req.getOrDefault("city", ""));
            userAddress.setCityId(Long.parseLong(req.getOrDefault("cityId", -99L).toString()));
            userAddress.setKecamatan((String) req.getOrDefault("kecamatan", ""));
            userAddress.setKecamatanId(Long.parseLong(req.getOrDefault("kecamatanId", -99L).toString()));
            userAddress.setKelurahan((String) req.getOrDefault("kelurahan", ""));
            userAddress.setKelurahanId(Long.parseLong(req.getOrDefault("kelurahanId", -99L).toString()));
            userAddress.setRw((String) req.getOrDefault("rw", ""));
            userAddress.setRt((String) req.getOrDefault("rt", ""));
            userAddress.setDescAddress((String) req.getOrDefault("descAddress", ""));
            userAddress.setLongitude((String) req.getOrDefault("longitude", ""));
            userAddress.setLatitude((String) req.getOrDefault("latitude", ""));
            userAddress.persist();

            AccessManagement accessManagement = new AccessManagement();
            accessManagement.setUserId(user);
            accessManagement.persist();

            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, "");
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
            UserAddress userAddress = UserAddress.find("user_id = ?1", userId).firstResult();

            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("userAddress", userAddress);

            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, response);
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

            Map<String,Object> req = om.convertValue(param, Map.class);
            Long userId = Long.parseLong(req.getOrDefault("userId", -99L).toString());
            if(userId.equals(-99L)){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User id harus disertakan", new String());
            }
            User user = User.findById(userId);
            if (user == null){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "User tidak ditemukan", new String());
            }
            if(User.count("loginName", req.getOrDefault("loginName", ""))>0){
                return new SimpleResponse(HttpConstant.VALIDATION_CODE, "Login name sudah digunakan", new String());
            }

            user.setFullName((String) req.getOrDefault("fullName", ""));
            user.setNickName((String) req.getOrDefault("nickName", ""));
            user.setLoginName((String) req.getOrDefault("loginName", ""));
            user.setEmail((String) req.getOrDefault("email", ""));
            user.setPhoneNumber((String) req.getOrDefault("phoneNumber", ""));
            user.setIdCardNumber((String) req.getOrDefault("idCardNumber", ""));
            user.setIdCardType((String) req.getOrDefault("idCardType", ""));
            user.setIdCardTypeId(Long.parseLong(req.getOrDefault("idCardTypeId", -99L).toString()));
            user.setDateOfBirth(DateUtil.convertStringtoLocalDate(req.getOrDefault("dateOfBirth", "9999-12-28").toString()));
            user.setPlaceOfBirth((String) req.getOrDefault("placeOfBirth", ""));
            user.setNationality((String) req.getOrDefault("nationality", ""));
            user.setNationalityId(Long.parseLong(req.getOrDefault("nationalityId", -99L).toString()));
            user.persist();

            UserAddress userAddress = UserAddress.find("user_id = ?1", userId).firstResult();
            userAddress.setMainAddress((String) req.getOrDefault("mainAddress", ""));
            userAddress.setCountry((String) req.getOrDefault("country", ""));
            userAddress.setCountryId(Long.parseLong(req.getOrDefault("countryId", -99L).toString()));
            userAddress.setProvince((String) req.getOrDefault("province", ""));
            userAddress.setProvinceId(Long.parseLong(req.getOrDefault("provinceId", -99L).toString()));
            userAddress.setCity((String) req.getOrDefault("city", ""));
            userAddress.setCityId(Long.parseLong(req.getOrDefault("cityId", -99L).toString()));
            userAddress.setKecamatan((String) req.getOrDefault("kecamatan", ""));
            userAddress.setKecamatanId(Long.parseLong(req.getOrDefault("kecamatanId", -99L).toString()));
            userAddress.setKelurahan((String) req.getOrDefault("kelurahan", ""));
            userAddress.setKelurahanId(Long.parseLong(req.getOrDefault("kelurahanId", -99L).toString()));
            userAddress.setRw((String) req.getOrDefault("rw", ""));
            userAddress.setRt((String) req.getOrDefault("rt", ""));
            userAddress.setDescAddress((String) req.getOrDefault("descAddress", ""));
            userAddress.setLongitude((String) req.getOrDefault("longitude", ""));
            userAddress.setLatitude((String) req.getOrDefault("latitude", ""));
            userAddress.persist();

            AccessManagement access = AccessManagement.find("user_id = ?1", userId).firstResult();

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

            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, new String());
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new SimpleResponse(HttpConstant.FAILED_CODE, e.getMessage(), new String());
        }
    }

    public SimpleResponse logout(String header){
        try {
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> req = om.readValue(header, Map.class);
            String loginName = (String) req.get("loginName");

            kongService.destroyConsumer(loginName);
            return new SimpleResponse(HttpConstant.SUCCESS_CODE, HttpConstant.SUCCESS, new String());
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            return new SimpleResponse(HttpConstant.FAILED_CODE, e.getMessage(), new String());
        }
    }
}
