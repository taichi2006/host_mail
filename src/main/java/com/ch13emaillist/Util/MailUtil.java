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

        // 1 - Configure Gmail SMTP over STARTTLS (Chuẩn dành cho Cloud/Render)
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp"); // Đổi từ smtps sang smtp
        props.setProperty("mail.smtp.host", "smtp.gmail.com"); // Đổi tất cả tiền tố smtps thành smtp
        props.setProperty("mail.smtp.port", "587"); // Sử dụng cổng 587
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true"); // Bắt buộc để mã hóa qua cổng 587
        props.setProperty("mail.smtp.ssl.checkserveridentity", "true");
        props.setProperty("mail.smtp.connectiontimeout", "10000");
        props.setProperty("mail.smtp.timeout", "10000");
        props.setProperty("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props);

// 2 - Validate the sender
        if (from == null || from.isBlank()) {
            from = GMAIL_USERNAME;
        }

        if (!GMAIL_USERNAME.equalsIgnoreCase(from.trim())) {
            throw new MessagingException(
                    "The sender address must match GMAIL_USERNAME.");
        }

// 3 - Create the message with UTF-8
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(GMAIL_USERNAME));
        message.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(to)
        );

        message.setSubject(subject, "UTF-8");
        message.setText(
                body,
                "UTF-8",
                bodyIsHTML ? "html" : "plain"
        );
        message.saveChanges();

        // 4 - Send and close the connection automatically
        try (Transport transport = session.getTransport("smtp")) {
            transport.connect(
                    "smtp.gmail.com",
                    GMAIL_USERNAME,
                    GMAIL_PASSWORD.replace(" ", "") // Đảm bảo App Password 16 chữ cái không có dấu cách
            );

            transport.sendMessage(
                    message,
                    message.getAllRecipients()
            );

        }
    }
}