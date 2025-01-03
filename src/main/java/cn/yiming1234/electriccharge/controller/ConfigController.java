package cn.yiming1234.electriccharge.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@Slf4j
public class ConfigController {

    public static final String TOKEN = "token";

    /**
     * 微信公众号服务器推送服务器配置
     */
    @GetMapping("/signature")
    public String signature(HttpServletRequest req, HttpServletResponse resp) {
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        boolean result = check(timestamp, nonce, signature);
        if (result) {
            log.info("校验成功，返回echostr");
            return echostr;
        }
        return "error!";
    }

    /**
     * 校验微信服务器传入的参数
     */
    public static boolean check(String timestamp, String nonce, String signature) {
        String[] arr = new String[]{TOKEN,timestamp,nonce};
        Arrays.sort(arr);
        String str = arr[0]+arr[1]+arr[2];
        str = DigestUtils.sha1Hex(str);
        System.out.println(signature);
        return str.equalsIgnoreCase(signature);
    }
}
