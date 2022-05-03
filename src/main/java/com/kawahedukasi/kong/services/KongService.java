package com.kawahedukasi.kong.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kawahedukasi.kong.models.Consumer;
import com.kawahedukasi.kong.models.Credential;
import com.kawahedukasi.kong.models.Response;
import com.kawahedukasi.kong.models.Token;
import io.quarkus.runtime.StartupEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class KongService {
    Logger LOGGER = LoggerFactory.getLogger(KongService.class);

    @ConfigProperty(name = "kong.url")
    private String kongUrl;

    @ConfigProperty(name = "kong.admin.url")
    private String kongAdminUrl;

    @ConfigProperty(name = "kong.app.redirect.uris")
    private String kongAppRedirectUris;

    @ConfigProperty(name = "kong.authorize-endpoint")
    private String kongAuthorizeEndpoint;

    @ConfigProperty(name = "kong.token-endpoint")
    private String kongTokenEndpoint;

    @ConfigProperty(name = "kong.app.name")
    private String kongAppName;

    @ConfigProperty(name = "kong.provision.key")
    private String kongProvisionKey;

    @ConfigProperty(name = "environment", defaultValue="LOCAL")
    private String environment;

    private HttpClientBuilder httpClientBuilder;

    private ObjectMapper om = new ObjectMapper();

    void onStart(@Observes StartupEvent ev) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        SSLContext sslContext = SSLContexts
                .custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .build();

        SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        httpClientBuilder = HttpClients.custom().setSSLSocketFactory(sslConSocFactory);
    }


    public Consumer createConsumer(String loginName, String customId) throws Exception{
        return createConsumer(loginName, customId, new ArrayList<>());
    }

    public Consumer createConsumer(String loginName, String customId, List<String> tags) throws Exception {
        if(!environment.equalsIgnoreCase("LOCAL")){
            HttpClient httpClient = httpClientBuilder.build();

            Map<String, Object> req = new HashMap<>();
            req.put("username", loginName);
            if(!customId.isBlank()){
                req.put("custom_id", customId);
            }
            if (!tags.isEmpty()){
                req.put("tags", tags);
            }

            HttpPost httpPost = new HttpPost(kongAdminUrl + "/consumers");
            httpPost.setEntity(new StringEntity(om.writeValueAsString(req)));
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);
            return om.readValue(EntityUtils.toString(httpResponse.getEntity()), Consumer.class);
        } else {
            Consumer consumer = new Consumer();
            consumer.username = loginName;
            consumer.customId = customId;
            consumer.id = "1";
            consumer.message = "";
            return consumer;
        }
    }

    public Credential createCredential(String loginName, String appName, List<String> redirectUris) throws Exception {
        if(!environment.equalsIgnoreCase("LOCAL")){
            HttpClient httpClient = httpClientBuilder.build();

            Map<String, Object> req = new HashMap<>();
            req.put("name", appName);
            req.put("redirect_uris", redirectUris);

            HttpPost httpPost = new HttpPost(kongAdminUrl+"/consumers/"+loginName+"/oauth2");
            httpPost.setEntity(new StringEntity(om.writeValueAsString(req)));
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPost);

            return om.readValue(EntityUtils.toString(httpResponse.getEntity()), Credential.class);
        }
        else {
            return null;
        }

    }

    private Token getToken(String code, String clientId, String clientSecret, String redirectUri) throws Exception{

        HttpClient httpClient = httpClientBuilder.build();

        Map<String, Object> req = new HashMap<>();
        req.put("code", code);
        req.put("client_secret", clientSecret);
        req.put("client_id", clientId);
        req.put("grant_type", "authorization_code");
        req.put("redirect_uri", redirectUri);

        HttpPost httpPost = new HttpPost(kongUrl+kongTokenEndpoint);
        httpPost.setEntity(new StringEntity(om.writeValueAsString(req)));
        httpPost.setHeader("Content-type", "application/json");
        HttpResponse httpResponse = httpClient.execute(httpPost);

        return om.readValue(EntityUtils.toString(httpResponse.getEntity()), Token.class);

    }

    private HttpResponse authorize(String clientId, String clientSecret ,String provisionKey, String loginName) throws Exception{
        HttpClient httpClient = httpClientBuilder.build();
        Map<String, Object> req = new HashMap<>();
        req.put("response_type", "code");
        req.put("authenticated_userid", loginName);
        req.put("client_id", clientId);
        req.put("provision_key", provisionKey);
        req.put("client_secret", clientSecret);

        HttpPost httpPost = new HttpPost(kongUrl+kongAuthorizeEndpoint);
        httpPost.setEntity(new StringEntity(om.writeValueAsString(req)));
        httpPost.setHeader("Content-type", "application/json");

        return httpClient.execute(httpPost);
    }

    public Token getAccessToken(Consumer consumer) throws Exception {
        if(!environment.equalsIgnoreCase("LOCAL")){
            Credential credential = getCredentialByAppName(consumer.username, kongAppName);
            if(credential == null){
                throw new RuntimeException("Credential not found");
            }
            HttpResponse httpResponse;
            httpResponse = authorize(credential.clientId, credential.clientSecret, kongProvisionKey, consumer.username);

            Map<String, Object> authorizeResp = om.readValue(EntityUtils.toString(httpResponse.getEntity()), Map.class);
            String[] splitUri = authorizeResp.get("redirect_uri").toString().split("\\?code=");

            return getToken(splitUri[1], credential.clientId, credential.clientSecret, splitUri[0]);
        }
        else {
            Token token = new Token();
            token.accessToken = "dummy-token";
            return token;
        }


    }

    private List<Credential> getCredentials(String loginName) throws Exception{
        HttpClient httpClient = httpClientBuilder.build();
        HttpGet httpGet = new HttpGet(kongAdminUrl+"/consumers/"+loginName+"/oauth2");
        HttpResponse httpResponse = httpClient.execute(httpGet);
        TypeReference ref = new TypeReference<Response<Credential>>() {};
        Response<Credential> credentials = (Response<Credential>) om.readValue(EntityUtils.toString(httpResponse.getEntity()), ref );
        return credentials.data;
    }

    public Credential getCredentialByAppName(String loginName, String appName) throws Exception {
        if(!environment.equalsIgnoreCase("LOCAL")){
            return getCredentials(loginName).stream()
                    .filter(s -> s.name.equalsIgnoreCase(appName))
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }

    public Consumer getConsumer(String loginName) throws Exception {
        if(!environment.equalsIgnoreCase("LOCAL")){
            HttpClient httpClient = httpClientBuilder.build();
            HttpGet httpGet = new HttpGet(kongAdminUrl+"/consumers/"+loginName);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            LOGGER.info("getConsumer resp : {}", httpResponse.getEntity());
            return om.readValue(EntityUtils.toString(httpResponse.getEntity()), Consumer.class);
        } else {
            Consumer consumer = new Consumer();
            consumer.username = loginName;
            consumer.id = "1";
            consumer.message = "";
            return consumer;
        }
    }

    public Consumer updateConsumer(String loginName, String customId) throws Exception{
        return updateConsumer(loginName, customId, new ArrayList<>());
    }

    public Consumer updateConsumer(String loginName, String customId, List<String> tags) throws Exception {
        if(!environment.equalsIgnoreCase("LOCAL")){
            HttpClient httpClient = httpClientBuilder.build();

            Map<String, Object> req = new HashMap<>();
            req.put("username", loginName);
            if(!customId.isBlank()){
                req.put("custom_id", customId);
            }
            if (!tags.isEmpty()){
                req.put("tags", tags);
            }

            HttpPatch httpPatch = new HttpPatch(kongAdminUrl + "/consumers");
            httpPatch.setEntity(new StringEntity(om.writeValueAsString(req)));
            httpPatch.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpClient.execute(httpPatch);
            return om.readValue(EntityUtils.toString(httpResponse.getEntity()), Consumer.class);
        } else {
            Consumer consumer = new Consumer();
            consumer.username = loginName;
            consumer.customId = customId;
            consumer.id = "1";
            consumer.message = "";
            return consumer;
        }
    }

    public void destroyConsumer(String username){
        try{
            HttpClient httpClient = httpClientBuilder.build();
            HttpDelete httpDelete = new HttpDelete(kongAdminUrl+"/consumers/"+username);
            HttpResponse httpResponse = httpClient.execute(httpDelete);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
    }




}
