package com.tiles.server;

import java.security.SecureRandom;
import java.util.Base64;

public class LoginData {
    private String name;
    private String encpswrd; 

    
    //Constructor
    public LoginData(String newName, String newEncpswrd) {
        this.name = newName;
        this.encpswrd = newEncpswrd;
    }

    // Getters and setters (required by most JSON mapping libraries like Jackson)
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public String getEncpswrd() { return this.encpswrd; }
    public void setEncpswrd(String encpswrd) { this.encpswrd = encpswrd; }

    private static final SecureRandom Token = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    
    public String generateToken() {
        
        byte[] randomBytes = new byte[24];
        Token.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
