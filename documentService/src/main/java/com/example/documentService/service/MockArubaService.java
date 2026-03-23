package com.example.documentService.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MockArubaService {

    public String sign(String doc) {
        return "SIGNED-" + UUID.randomUUID();
    }
}