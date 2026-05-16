package com.stock.market.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendAlertEmail(String to, String stockName, String alertType,
                                BigDecimal targetPrice, BigDecimal triggeredPrice) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("[股票预警] " + stockName + " 价格预警触发");

            String alertTypeDisplay = "PRICE_ABOVE".equals(alertType) ? "上涨突破" : "下跌突破";
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String html = "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2 style='color: #e74c3c;'>股票价格预警</h2>"
                    + "<table style='border-collapse: collapse; width: 100%; max-width: 500px;'>"
                    + "<tr><td style='padding: 8px; border: 1px solid #ddd; background: #f5f5f5;'><b>股票名称</b></td>"
                    + "<td style='padding: 8px; border: 1px solid #ddd;'>" + stockName + "</td></tr>"
                    + "<tr><td style='padding: 8px; border: 1px solid #ddd; background: #f5f5f5;'><b>预警类型</b></td>"
                    + "<td style='padding: 8px; border: 1px solid #ddd;'>" + alertTypeDisplay + " (" + alertType + ")</td></tr>"
                    + "<tr><td style='padding: 8px; border: 1px solid #ddd; background: #f5f5f5;'><b>目标价格</b></td>"
                    + "<td style='padding: 8px; border: 1px solid #ddd;'>" + targetPrice + "</td></tr>"
                    + "<tr><td style='padding: 8px; border: 1px solid #ddd; background: #f5f5f5;'><b>触发价格</b></td>"
                    + "<td style='padding: 8px; border: 1px solid #ddd; color: #e74c3c; font-weight: bold;'>" + triggeredPrice + "</td></tr>"
                    + "<tr><td style='padding: 8px; border: 1px solid #ddd; background: #f5f5f5;'><b>触发时间</b></td>"
                    + "<td style='padding: 8px; border: 1px solid #ddd;'>" + time + "</td></tr>"
                    + "</table>"
                    + "<p style='margin-top: 20px; color: #999; font-size: 12px;'>此邮件由股票投资平台自动发送，请勿回复。</p>"
                    + "</body></html>";

            helper.setText(html, true);
            mailSender.send(message);
            log.info("Alert email sent to {} for stock {} ({})", to, stockName, alertType);
        } catch (MessagingException e) {
            log.error("Failed to send alert email to {}: {}", to, e.getMessage(), e);
        }
    }
}
