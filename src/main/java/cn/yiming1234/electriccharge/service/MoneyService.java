package cn.yiming1234.electriccharge.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class MoneyService {

    private final MailService mailService;
    private final ElectricService electricService;
    public MoneyService (MailService mailService, ElectricService electricService){
        this.mailService = mailService;
        this.electricService = electricService;
    }

    /**
     * 获取 token
     */
    public String getToken() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = "platform=WECHAT_H5";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://application.xiaofubao.com/center/common/token/get"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 NetType/WIFI MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x63090c0f) XWEB/11275 Flue")
                .setHeader("x-requested-with", "XMLHttpRequest")
                .setHeader("content-type", "application/x-www-form-urlencoded;charset=UTF-8")
                .setHeader("origin", "https://application.xiaofubao.com")
                .setHeader("sec-fetch-site", "same-origin")
                .setHeader("sec-fetch-mode", "cors")
                .setHeader("sec-fetch-dest", "empty")
                .setHeader("referer", "https://application.xiaofubao.com/")
                .setHeader("accept-language", "zh-CN,zh;q=0.9")
                .setHeader("Cookie", electricService.getCookie())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Response from token API: {}", response.body());

        JSONObject jsonResponse = new JSONObject(response.body());
        if (jsonResponse.getInt("statusCode") == 204) {
            String user = mailService.getUsers().getFirst();
            mailService.sendMail(user, Double.parseDouble("请更新Cookie"));
        }

        return response.body();
    }

    /**
     * 获取付款链接
     */
    public String getPaymentLink(String buildingCode, String floorCode, String roomCode, String money) throws Exception {
        String submitToken = getToken();
        HttpClient client = HttpClient.newHttpClient();
        String requestBody = String.format(
                "areaId=2307499265384382465&buildingCode=%s&floorCode=%s&roomCode=%s&money=%s&submitToken=%s&platform=WECHAT_H5&extJson={\"serialNO\":\"\"}&ymId=2309636064844165132",
                buildingCode, floorCode, roomCode, money, submitToken
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://application.xiaofubao.com/app/electric/recharge"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 NetType/WIFI MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x63090c0f) XWEB/11275 Flue")
                .setHeader("x-requested-with", "XMLHttpRequest")
                .setHeader("content-type", "application/x-www-form-urlencoded;charset=UTF-8")
                .setHeader("origin", "https://application.xiaofubao.com")
                .setHeader("sec-fetch-site", "same-origin")
                .setHeader("sec-fetch-mode", "cors")
                .setHeader("sec-fetch-dest", "empty")
                .setHeader("referer", "https://application.xiaofubao.com/")
                .setHeader("accept-language", "zh-CN,zh;q=0.9")
                .setHeader("Cookie", electricService.getCookie())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("Response from payment API: {}", response.body());

        JSONObject jsonResponse = new JSONObject(response.body());
        if (jsonResponse.getInt("statusCode") == 204) {
            String user = mailService.getUsers().getFirst();
            mailService.sendMail(user, Double.parseDouble("请更新Cookie"));
        }

        return new JSONObject(response.body()).getString("data");
    }
}
