package cn.yiming1234.electriccharge.service;

import cn.yiming1234.electriccharge.mapper.UserMapper;
import cn.yiming1234.electriccharge.pojo.Balance;
import cn.yiming1234.electriccharge.properties.MessageProperties;
import cn.yiming1234.electriccharge.properties.message.BaseMessage;
import cn.yiming1234.electriccharge.properties.message.TextMessage;
import cn.yiming1234.electriccharge.util.MessageUtil;
import cn.yiming1234.electriccharge.util.MsgHelpClass;
import cn.yiming1234.electriccharge.util.TypeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class WeixinService {

    private final ObjectMapper jacksonObjectMapper;
    private final ElectricService electricService;
    private final MoneyService moneyService;
    private final UserMapper userMapper;

    public WeixinService(ObjectMapper jacksonObjectMapper, MoneyService moneyService, ElectricService electricService, UserMapper userMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.electricService = electricService;
        this.moneyService = moneyService;
        this.userMapper = userMapper;
    }

    /**
     * 处理来自微信公众号的消息
     */
    public BaseMessage processMessage(Map<String, String> map) throws IllegalAccessException, InstantiationException {
        String fromUserName = map.get("ToUserName");
        String toUserName = map.get("FromUserName");
        String msgType = map.get("MsgType");
        long createTime = Long.parseLong(map.get("CreateTime"));
        long msgId = Long.parseLong(map.get("MsgId"));
        TextMessage baseMessage = null;
        if (msgType.equals(TypeUtil.REQ_MESSAGE_TYPE_TEXT)) {
            log.info("这是文本消息！");
            baseMessage = MsgHelpClass.setAttribute(new MessageProperties(fromUserName, toUserName, createTime, msgType, msgId), TextMessage.class);
            TextMessage textMessage = baseMessage;
            textMessage.setContent(map.get("Content"));
        } else {
            log.info("暂不支持此消息类型！");
        }
        return baseMessage;
    }

    /**
     * 获取消息内容
     */
    public String getContent(Map<String, String> map){
        try{
            log.info("消息内容：" + map.get("Content"));
            return map.get("Content");
        } catch (Exception e) {
            log.error("获取消息内容失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取电费余额
     */
    public double getCharge(String content) {
        try {
            String response = electricService.getCharge(content);
            log.info("Server response: {}", response);
            Balance balance = jacksonObjectMapper.readValue(electricService.getCharge(content), Balance.class);
            return balance.getData().getAmount();
        } catch (Exception e) {
            log.error("获取电费余额失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置余额
     */
    public double setBalance(){
        try {
            Balance balance = jacksonObjectMapper.readValue(electricService.getElectricCharge(), Balance.class);
            double amount = balance.getData().getAmount();
            log.info("balance:{}", amount);
            return amount;
        } catch (Exception e) {
            log.error("获取电费余额失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成回复微信公众号的消息
     */
    public void autoReply(BaseMessage baseMessage, HttpServletResponse response) {
        log.info("-----------------开始回复消息-----------------");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xml; charset=UTF-8");
        if (baseMessage instanceof TextMessage textMessage) {
            try {
                String content = getContent(MessageUtil.xmlToMap(MessageUtil.textMessageToXml(textMessage)));
                double balance = getCharge(content);

                String codeParams = electricService.getCode(content);
                String[] params = codeParams.split("&");

                String buildingCode = params[0].split("=")[1];
                String floorCode = params[1].split("=")[1];
                String roomCode = params[2].split("=")[1];

                if ( userMapper.getByRoom(roomCode)!= null) {
                    electricService.saveChargeByRoom(roomCode, String.valueOf(balance));
                    log.info("房间号存在，余额: {}", balance);
                } else {
                    log.info("房间号不存在，跳过储存");
                }

                String recharge10 = moneyService.getPaymentLink(buildingCode, floorCode, roomCode, "10");
                String recharge20 = moneyService.getPaymentLink(buildingCode, floorCode, roomCode, "20");
                String recharge30 = moneyService.getPaymentLink(buildingCode, floorCode, roomCode, "30");

                textMessage.setContent(String.format(
                        "当前寝室电费余额：%.2f元，\n充值10元：%s\n充值20元：%s\n充值30元：%s\n",
                        balance, recharge10, recharge20, recharge30
                ));
                textMessage.setCreateTime(System.currentTimeMillis());
                textMessage.setFromUserName(baseMessage.getFromUserName());
                textMessage.setToUserName(baseMessage.getToUserName());
                String xml = MessageUtil.textMessageToXml(textMessage);
                log.info("回复消息内容：" + xml);
                response.getWriter().write(xml);
                response.getWriter().flush(); // 确保消息被发送
                log.info("消息已发送到微信服务器");
            } catch (Exception e) {
                log.error("处理消息失败", e);
            }
        } else {
            log.info("暂不支持此消息类型！");
            try {
                response.getWriter().write("success");
                response.getWriter().flush(); // 确保消息被发送
            } catch (Exception e) {
                log.error("发送消息失败", e);
            }
        }
    }
}