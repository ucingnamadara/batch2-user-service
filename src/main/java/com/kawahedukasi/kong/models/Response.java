package com.kawahedukasi.kong.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {
    public String next;
    public List<T> data;
}
