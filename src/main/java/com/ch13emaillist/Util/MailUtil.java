package com.ch13emaillist.Util;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class MailUtil {

    private static final String GMAIL_USERNAME =
            "haohan123ajaja@gmail.com";

    private static final String GMAIL_PASSWORD =
            "zcgj mvjx hzbu xvkh";

    public static void sendMail(String to, String from,
                                String subject, String body,
                                boolean bodyIsHTML)
            throws MessagingException {

        // 1 - Configure Gmail SMTP over STARTTLS (Cổng 587)
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp"); // Chú ý: bỏ chữ 's'
        props.put("mail.smtp.host", "smtp.gmail.com"); // Đổi tất cả thành mail.smtp.*
        props.put("mail.smtp.port", "587"); // Đổi cổng
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // BẮT BUỘC PHẢI CÓ DÒNG NÀY ĐỂ MÃ HÓA

        Session session = Session.getInstance(props);

// 2 & 3 - Validate và Create Message (Giữ nguyên code cũ của bạn)
        if (from == null || from.isBlank()) {
            from = GMAIL_USERNAME;
        }
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(GMAIL_USERNAME));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject, "UTF-8");
        message.setText(body, "UTF-8", bodyIsHTML ? "html" : "plain");
        message.saveChanges();

// 4 - Send qua cổng smtp
        try (Transport transport = session.getTransport("smtp")) { // Chú ý: truyền vào "smtp", không phải "smtps"
            transport.connect("smtp.gmail.com", GMAIL_USERNAME, GMAIL_PASSWORD.replace(" ", ""));
            transport.sendMessage(message, message.getAllRecipients());

        }
    }
}