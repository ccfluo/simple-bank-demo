package com.simple.bank.service.other;

import org.springframework.stereotype.Service;

@Service
public class SMSServiceImpl implements SMSService {
    public void sendSms(String mobile, String content){
        System.out.println("SMS is sent to " + mobile + " content: " + content);
    }
}
