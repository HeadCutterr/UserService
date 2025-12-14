package com.example.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String SITE_NAME = "ваш сайт";

    public void sendWelcomeEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Здравствуйте! Ваш аккаунт на сайте " + SITE_NAME + " был успешно создан.");
        message.setText("Здравствуйте!\n\nВаш аккаунт на сайте " + SITE_NAME + " был успешно создан.\n\nС уважением,\nКоманда поддержки");
        mailSender.send(message);
    }

    public void sendDeleteEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Здравствуйте! Ваш аккаунт был удалён.");
        message.setText("Здравствуйте!\n\nВаш аккаунт был удалён.\n\nС уважением,\nКоманда поддержки");
        mailSender.send(message);
    }
}