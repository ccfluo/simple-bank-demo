package com.simple.bank.service.impl;

import com.simple.bank.service.EmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    public void sendEmail(String email, String content){
        System.out.println("Email is sent to " + email + " content: " + content);
    }
}
