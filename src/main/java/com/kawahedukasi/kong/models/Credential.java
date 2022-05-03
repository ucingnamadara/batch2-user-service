package com.kawahedukasi.kong.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Credential {

    @JsonProperty("created_at")
    public Integer createdAt;

    public String id;

    public List<String> tags;

    public String name;

    @JsonProperty("client_secret")
    public String clientSecret;

    @JsonProperty("client_id")
    public String clientId;

    @JsonProperty("redirect_uris")
    public List<String> redirectUris;

    @JsonProperty("hash_secret")
    public Boolean hashSecret;

    @JsonProperty("client_type")
    public String clientType;

    public Consumer consumer;
}
