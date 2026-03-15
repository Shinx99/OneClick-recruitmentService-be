package com.onceClick.recruitmentService.shared.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class TokenHashUtil {

    public String hash(String rawToken){

        if(rawToken == null){
            throw new IllegalArgumentException("Token must not be null!");
        }

        try{
            // Get implementation of SHA-265 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hashing: convert string -> bytes -> hash bytes
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            // Convert bytes array -> hex (Ex: "a3f1b2c4...")
            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
