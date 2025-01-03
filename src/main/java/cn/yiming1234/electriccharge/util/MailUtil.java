package cn.yiming1234.electriccharge.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailUtil {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送文本邮件
     */
    public void sendMail(String to, String subject, double balance) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("pleasurecruise@qq.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText("您的电费余额不足，当前余额为：" + balance + "元。"); // 使用传入的参数 `balance`
        mailSender.send(message);
    }
}
