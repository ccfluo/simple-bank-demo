package com.simple.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.simple.bank.config.JacksonConfig;

@RestController
public class DebugController {

    @Autowired
    private ObjectMapper objectMapper;
//    public DebugController(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }

    @GetMapping("/debug/jackson")
    public String debugJackson() {
        // 打印已注册的模块;
        objectMapper.getRegisteredModuleIds().forEach(module -> {
            System.out.println("Registered Module: " + module.getClass().getName());
        });
        return "Check console for Jackson modules.";
    }
}