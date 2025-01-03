package cn.yiming1234.electriccharge.util;

import cn.yiming1234.electriccharge.properties.message.TextMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MessageUtil {

    /**
     * 将xml字符串转换为文本对象
     * （解析微信发来的请求）
     */
    public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        InputStream inputStream = request.getInputStream();
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> elementList = root.elements();
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
            log.info("name：" + e.getName() + "   value："+map.get(e.getName()));
        }
        inputStream.close();
        return map;
    }

    /**
     * 将文本对象转换为xml字符串
     */

    public static String textMessageToXml(TextMessage textMessage) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("xml");

        root.addElement("ToUserName").addCDATA(textMessage.getToUserName());
        root.addElement("FromUserName").addCDATA(textMessage.getFromUserName());
        root.addElement("CreateTime").addText(String.valueOf(textMessage.getCreateTime()));
        root.addElement("MsgType").addCDATA(textMessage.getMsgType());
        root.addElement("Content").addCDATA(textMessage.getContent());

        return document.asXML();
    }

    /**
     * 将xml字符串转换为文本对象
     */
    public static Map<String, String> xmlToMap(String s) {
        Map<String, String> map = new HashMap<>();
        try {
            Document document = DocumentHelper.parseText(s);
            Element root = document.getRootElement();
            List<Element> elementList = root.elements();
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
        } catch (Exception e) {
            log.error("xml转换失败", e);
        }
        return map;
    }
}
