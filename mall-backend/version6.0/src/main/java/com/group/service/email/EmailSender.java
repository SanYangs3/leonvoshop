package com.group.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 通过 javax.mail 发送验证码邮件，适配本地 libs 下的 javax.mail/activation。
 */
@Component
public class EmailSender {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private boolean startTls;

    @Value("${spring.mail.default-encoding:UTF-8}")
    private String encoding;

    public void sendVerificationMail(String to, String code, String purpose) {
        Session session = Session.getInstance(buildProps(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("验证码验证 - " + purpose, encoding);
            message.setText(buildContent(code, purpose), encoding);
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException("发送邮件失败: " + e.getMessage(), e);
        }
    }

    private Properties buildProps() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", String.valueOf(smtpAuth));
        props.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));
        // QQ SMTP 常用配置
        props.put("mail.smtp.ssl.trust", host);
        return props;
    }

    private String buildContent(String code, String purpose) {
        return "您好，\n\n"
                + "您正在进行 " + purpose + " 操作。\n"
                + "本次验证码： " + code + "\n"
                + "有效期：60 秒，请尽快完成验证。\n\n"
                + "如果非本人操作，请忽略本邮件。";
    }
}

