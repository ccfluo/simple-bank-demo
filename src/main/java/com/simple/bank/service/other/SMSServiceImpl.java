package com.simple.bank.service.other;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SMSServiceImpl implements SMSService {
    public void sendSms(String mobile, String content){
        //TODO: enhance to send SMS via telcom services
        log.info("[SMS notification] SMS is sent to " + mobile + " content: " + content);
    }
}
