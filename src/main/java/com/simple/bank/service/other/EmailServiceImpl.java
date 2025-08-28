package com.simple.bank.service.other;

import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    public void sendEmail(String email, String content){
        System.out.println("Email is sent to " + email + " content: " + content);
    }
}
