package cn.yiming1234.electriccharge.controller;

import cn.yiming1234.electriccharge.properties.message.BaseMessage;
import cn.yiming1234.electriccharge.service.WeixinService;
import cn.yiming1234.electriccharge.util.MessageUtil;
import cn.yiming1234.electriccharge.util.TypeUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class WeixinController {

    private final WeixinService weixinService;

    public WeixinController(WeixinService weixinService) {
        this.weixinService = weixinService;
    }

    /**
     * 自动回复微信公众号消息
     */
    @PostMapping("/signature")
    public void message(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("接收到微信公众号消息");
            Map<String, String> paramMap = MessageUtil.parseXml(request);
            log.info("解析后的参数: " + paramMap);
            String type = paramMap.get("MsgType");
            if (TypeUtil.REQ_MESSAGE_TYPE_TEXT.equals(type)) {
                log.info("-----------------进入消息处理-----------------");
                BaseMessage baseMessage = weixinService.processMessage(paramMap);
                weixinService.autoReply(baseMessage, response);
            } else {
                log.info("不是文本消息");
            }
        } catch (Exception e) {
            log.error("处理微信公众号消息时发生错误", e);
        }
    }
}
