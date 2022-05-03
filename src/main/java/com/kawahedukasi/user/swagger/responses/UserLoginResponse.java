package com.kawahedukasi.user.swagger.responses;

import com.kawahedukasi.user.models.UserAddress;
import com.kawahedukasi.user.utils.SimpleResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLoginResponse {
    String loginName;
    String fullName;
    String email;
    String phoneNumber;
    String role;
    Long roleId;
    String[] access;
    String[] accessRole;
    String token;
}
