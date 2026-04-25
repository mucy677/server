package com.tiles.server;

import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.InputStreamReader;

@Component
public class AccountDetails {
    
    private final HashMap<String, String> map = new HashMap<>(); //HashMap to store username and password pairs, where the key is the username and the value is the password

    public AccountDetails() {
        try {
            ClassPathResource resource = new ClassPathResource("AccountDetails.txt"); //loads the file from the classpath
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) { //reads the file using BufferedReader
                String line;
                while ((line = br.readLine()) != null) {    //reads the file line by line until the end of the file is reached
                    System.out.println("Account details:");
                    System.out.println(line);
                    String[] parts = line.split(":"); //splits the line into two parts using ":" as the delimiter, where parts[0] is the username and parts[1] is the password
                    if (parts.length == 2) {
                        map.put(parts[0].trim(), parts[1].trim());  //splits the line into username and password and stores in map
                        System.out.println("Acc: " +parts[0]);
                        System.out.println("PW: " + parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public HashMap<String, String> getMap() {
        //harcoded credentials for testing because file read won't work
        //this.map.put("john", "c9765b38a8ded4d7f4286cbab7c104e95208a911b189beaf3c88182376e6bf32");
        return map; //returns the map containing username and password pairs
    }

}
