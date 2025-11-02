package com.example.shop.util;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();
    public String createTokenFor(Long userId){ String t = UUID.randomUUID().toString(); tokens.put(t, userId); return t; }
    public Long getUserIdForToken(String token){ return tokens.get(token); }
    public void revoke(String token){ tokens.remove(token); }
}
