package com.kawahedukasi.user.swagger.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLoginRequest {
    @JsonProperty(required = true)
    String username;

    @JsonProperty(required = true)
    String password;
}
