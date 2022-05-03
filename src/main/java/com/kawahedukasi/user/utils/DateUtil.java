package com.kawahedukasi.user.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateUtil {
    private static Logger LOGGER(){
        return LoggerFactory.getLogger(DateUtil.class);
    };

    public static LocalDateTime convertStringToLocalDateTime(String input) throws Exception{
        return convertStringToLocalDateTime(input, "yyyy-MM-dd hh:mm:ss");
    }

    public static LocalDateTime convertStringToLocalDateTime(String input, String format) throws Exception {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(input, df);
        }
        catch (Exception e){
            LOGGER().error(e.getMessage());
            throw new Exception("Error at method convertStringToLocalDateTime", e.getCause());
        }
    }

    public static String convertLocalDateTimeToString(LocalDateTime input) throws Exception{
        return convertLocalDateTimeToString(input, "yyyy-MM-dd hh:mm:ss");
    }

    public static String convertLocalDateTimeToString(LocalDateTime input, String format) throws Exception{
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
            return df.format(input);
        } catch (Exception e){
            LOGGER().error(e.getMessage());
            throw new Exception("Error at method convertLocalDateTimeToString", e.getCause());
        }
    }

    public static LocalDate convertStringtoLocalDate(String input) throws Exception{
        return convertStringtoLocalDate(input, "yyyy-MM-dd");
    }

    public static LocalDate convertStringtoLocalDate(String input, String format) throws Exception{
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
            return LocalDate.parse(input, df);
        } catch (Exception e){
            LOGGER().error(e.getMessage());
            throw new Exception("Error at method convertLocalDateTimeToString", e.getCause());
        }
    }
}
