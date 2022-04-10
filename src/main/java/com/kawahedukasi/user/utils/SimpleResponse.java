package com.kawahedukasi.user.utils;

import lombok.*;

@AllArgsConstructor
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class SimpleResponse {
    public Long status;
    public String message;
    public Object payload;
}
