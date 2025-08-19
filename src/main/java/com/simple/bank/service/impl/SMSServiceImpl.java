package com.simple.bank.service.impl;

import com.simple.bank.service.SMSService;
import org.springframework.stereotype.Service;

@Service
public class SMSServiceImpl implements SMSService {
    public void sendSms(String mobile, String content){
        System.out.println("SMS is sent to " + mobile + " content: " + content);
    }
}
