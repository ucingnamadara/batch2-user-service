package com.kawahedukasi.user.swagger.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class UserSignUpRequest {
    String fullName;
    String nickName;
    String loginName;
    String password;
    String email;
    String phoneNumber;
    String idCardNumber;
    String idCardType;
    Long idCardTypeId;
    LocalDate dateOfBirth;
    String placeOfBirth;
    String nationality;
    Long nationalityId;
    String mainAddress;
    String country;
    Long countryId;
    String province;
    Long provinceId;
    String city;
    Long cityId;
    String kecamatan;
    Long kecamatanId;
    String kelurahan;
    Long kelurahanId;
    String rw;
    String rt;
    String descAddress;
    String longitude;
    String latitude;
}
