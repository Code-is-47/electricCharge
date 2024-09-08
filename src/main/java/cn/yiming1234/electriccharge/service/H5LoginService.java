package cn.yiming1234.electriccharge.service;

import cn.yiming1234.electriccharge.properties.H5LoginProperties;
import cn.yiming1234.electriccharge.properties.WeChatProperties;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class H5LoginService {

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private H5LoginProperties h5LoginProperties;

    /**
     * 获取微信公众号的code
     */
    public String getCode() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String encodedUrl = URLEncoder.encode(h5LoginProperties.getHost(), StandardCharsets.UTF_8.toString());
            log.info("encodedUrl: " + encodedUrl);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(
                            "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base#wechat_redirect",
                            weChatProperties.getAppid(), encodedUrl)))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("response: " + response.body());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get code", e);
        }
    }

    /**
     * 获取用户的openid
     */
    public String getOpenId(String code) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(
                            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                            weChatProperties.getAppid(), weChatProperties.getAppsecret(), code)))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("openid");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get openid", e);
        }
    }

    /**
     * 储存用户的openid
     */
    public void saveOpenId(String openId) {
        // TODO
        log.info("TODO");
    }
}