package com.kawahedukasi.user.utils;

public class SimpleResponse {
    public Long status;
    public String message;
    public Object payload;

    public SimpleResponse(Long status, String message, Object payload) {
        this.status = status;
        this.message = message;
        this.payload = payload;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
