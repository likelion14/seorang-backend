package com.likelion.seorang.common;

import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

public class ApiError {
    public static ResponseEntity<Map<String, Object>> of(int status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("code", code);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}

