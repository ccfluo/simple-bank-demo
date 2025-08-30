package com.simple.bank.service.other;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    public void sendEmail(String email, String content){
        //TODO: enhance to send email via email service
        log.info("[Email notification] Email is sent to " + email + " content: " + content);
    }
}
