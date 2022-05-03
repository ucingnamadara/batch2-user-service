package com.kawahedukasi.kong.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Consumer {

    @JsonProperty("custom_id")
    public String customId;

    @JsonProperty("created_at")
    public Integer createdAt;

    public String id;

    public List<String> tags;

    public String username;

    public String message;

}
