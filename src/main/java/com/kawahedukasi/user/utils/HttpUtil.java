package com.kawahedukasi.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.vertx.http.runtime.HttpConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    private static RequestConfig config(int timeout){
        return RequestConfig.custom()
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .setConnectTimeout(timeout * 1000)
                .build();
    }

    public static Object post(String url) throws Exception{
        return post(url, new HashMap<>(), new String(), new HashMap<>(), 30);
    }

    public static Object post(String url, Map<String, Object> req) throws Exception{
        return post(url, req, new String(), new HashMap<>(), 30);
    }

    public static Object post(String url, Map<String, Object> req, String customId) throws Exception{
        return post(url, req, customId, new HashMap<>(), 30);
    }

    public static Object post(String url, Map<String, Object> req, String customId, Map<String, Object> headers, int timeout) throws Exception{
        ObjectMapper om = new ObjectMapper();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config(timeout));
        if(!req.isEmpty()){
            httpPost.setEntity(new StringEntity(om.writeValueAsString(req)));
        }
        if(!customId.isBlank()){
            httpPost.setHeader("X-Consumer-Custom-ID", customId);
        }
        if(!headers.isEmpty()){
            headers.keySet().forEach(
                    s-> httpPost.setHeader(s, headers.get(s).toString())
            );
        }
        httpPost.setHeader("Content-type", "application/json");
        HttpResponse httpResponse = httpClient.execute(httpPost);
        return om.readValue(EntityUtils.toString(httpResponse.getEntity()), Object.class);
    }

    public static Object get(String url) throws Exception{
        return get(url, new HashMap<>(), 30);
    }

    public static Object get(String url, Map<String, Object> headers, int timeout) throws Exception{
        ObjectMapper om = new ObjectMapper();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config(timeout));

        if(!headers.isEmpty()){
            headers.keySet().forEach(
                    s-> httpGet.setHeader(s, headers.get(s).toString())
            );
        }
        HttpResponse httpResponse = httpClient.execute(httpGet);
        return om.readValue(EntityUtils.toString(httpResponse.getEntity()), Object.class);
    }
}
