package com.kawahedukasi.user.services;

public class OtpService {

    // Membuat OTP random terdiri dari 6 digit angka
    public static String generateOtp(){
        int min = 100000;
        int max = 999999;
        Double otpCalculation = Math.floor(Math.random()*(max-min+1)+min);
        Integer otpCalculationInt = otpCalculation.intValue();
        String otp = otpCalculationInt.toString();
        return otp;
    }
    
}
