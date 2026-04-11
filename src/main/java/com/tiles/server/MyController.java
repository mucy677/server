package com.tiles.server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class MyController {
	
	// Simple game state tracking
	private int playerX = 5;
	private int playerY = 5;
	
	// Map dimensions
	private static final int MAP_WIDTH = 20;
	private static final int MAP_HEIGHT = 20;
	
	// Hardcoded map data (same as frontend)
	private static final String[][] MAP = {
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", ".", "W", "g", "t", "t", "g", "g", "t", "g", "g", "g"},
		{"S", "S", "S", "S", "S", "S", "g", "g", "g", "g", "W", "g", "t", ";", "t", "t", "t", "g", "g", "g"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "g", "W", "W", "g", "t", "t", "t", "t", "t", "t", "g", "g"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "W", "W", "g", "g", "t", "t", "t", "t", "t", "g", "g", "g"},
		{"S", "S", "S", "D", "S", "S", "g", "g", "W", "g", "g", "t", "t", "t", "g", "g", "g", "g", "g", "g"},
		{"g", "g", ",", "_", "g", "g", "g", "W", "W", "W", "g", "t", "g", "g", "g", "g", "g", "g", "g", "g"},
		{".", "g", "g", "_", "g", "g", "g", "W", "g", "W", ".", "g", "g", "g", "g", "g", "g", "g", "g", "g"},
		{"_", "_", "_", "_", "g", "g", "W", "W", "g", "W", "W", "g", "g", "g", "g", "_", "_", "_", "_", "_"},
		{"_", "g", "t", "t", "g", "W", "W", "W", "g", "g", "W", "g", "g", "g", "g", "_", "g", "g", "g", "g"},
		{"_", "g", "g", "t", "g", "W", "W", "g", "g", "g", "W", "g", "g", "g", "B", "D", "B", "g", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W", "W", "g", "g", "B", "f", "B", ".", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "t", "t", "g", "W", "W", "g", "g", "B", "B", "f", "B", "B", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "t", "g", "g", "W", ",", "g", "g", "B", "f", "f", "f", "B", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "W", "W", "g", "g", "B", "f", "f", "f", "B", "g", "g"},
		{"_", "_", "g", "g", "g", "g", "g", "g", "W", ".", "W", "g", "g", "B", "f", "f", "f", "B", "g", "g"},
		{"g", "_", "_", "g", "g", "g", "g", "W", "W", "g", "W", "g", "g", "B", "B", "B", "B", "B", "g", "g"},
		{"g", "g", "_", "g", "g", "g", "g", "W", "g", "g", "W", "g", "g", "g", "g", "g", "g", "g", "g", "g"},
		{"g", "g", "_", "g", "g", "g", "W", "W", "g", "g", "W", "W", "g", "g", ":", "g", "g", "g", "g", "g"},
		{"g", "g", "g", "g", "g", "W", "W", "W", "g", "g", "W", "W", "g", "g", "g", "g", "t", "g", "g", "g"},
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W", "g", "g", "g", "g", "g", "g", "g", "g", "g"}
	};
    
    //loading credentials from file not working -- look into loading resources with Spring
    
    AccountDetails accountDetails = new AccountDetails("AccountDetails.txt");
    //Create HashMap of account credentials
    HashMap<String, String> logins = accountDetails.getMap();

    
    Sessions sessions = new Sessions();

    //Position Setter (required for tests)
    public void setPosition(int newX, int newY) { 
        this.playerX = newX; 
        this.playerY = newY;
    }
    
    @PostMapping("/test")
    public ResponseEntity<RequestData> handleJsonRequest(@RequestBody RequestData requestData) {
        System.out.println("Received name: " + requestData.getName());
        System.out.println("Received gold: " + requestData.getGold());
        System.out.println("Received silver: " + requestData.getSilver());
        System.out.println("Received bronze: " + requestData.getBronze());
        return new ResponseEntity<>(requestData, HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<String> handleJsonRequest(@RequestBody LoginData loginData) { 

        //Checking if sent password matches password stored against sent name
        if(loginData.getEncpswrd().equals(logins.get(loginData.getName()))) {
            System.out.println(loginData.getName() + " logged in");

            //generate token
            String token = loginData.generateToken();

            //Add token as key to HashMap tracking current sessions
            sessions.addSession(token, loginData.getName());

            //Return response with JSON formatted token
            return new ResponseEntity<>("{\"session\": " + "\"" + token + "\"}", HttpStatus.OK);

        } else if (!loginData.getEncpswrd().equals(logins.get(loginData.getName()))) {
            System.out.println("Invalid Credentials");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //TODO
        //Get AccountDetails file read working
        //      maybe try? https://www.geeksforgeeks.org/advance-java/read-file-from-resources-folder-in-spring-boot/
        
    }

    @GetMapping("/logout")
    public ResponseEntity<String> handleLogOut(@RequestParam String session) {
        
        //sessions.list();

        //If session key is currently in use and valid, remove it
        if (sessions.logOut(session) != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/info")
    public ResponseEntity<InfoResponse> info(
    		@RequestParam(defaultValue = "5") int y,
    		@RequestParam(defaultValue = "5") int x) {
        
        System.out.println("Info request: x=" + x + ", y=" + y);
        
        //Return status 204 and exit early, if received coordinates do not match current player location stored on server
        if (x!=playerX||y!=playerY) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        // Define view window (11x11 centered on player)
        int viewWidth = 11;
        int viewHeight = 11;
        int viewMiddleX = viewWidth / 2;
        int viewMiddleY = viewHeight / 2;
        
        // Calculate window bounds
        int top = y - viewMiddleY;
        int left = x - viewMiddleX;
        int bottom = top + viewHeight - 1; //corrected off by 1 error (DS)
        int right = left + viewWidth - 1; //corrected off by 1 error (DS)
        
        // Extract map window
        String[][] mapWindow = new String[viewHeight][viewWidth];
        for (int row = 0; row < viewHeight; row++) {
            for (int col = 0; col < viewWidth; col++) {
                int mapY = top + row;
                int mapX = left + col;
                
                // Check bounds
                if (mapY < 0 || mapY >= MAP_HEIGHT || mapX < 0 || mapX >= MAP_WIDTH) {
                    mapWindow[row][col] = " ";
                } else {
                    mapWindow[row][col] = MAP[mapY][mapX];
                }
            }
        }
        
        // Build response
        InfoResponse response = new InfoResponse(playerX, playerY, top, left, bottom, right, mapWindow);

        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    
    @GetMapping("/move")
    public String move(
    		@RequestParam(defaultValue = "0") int dy,
    		@RequestParam(defaultValue = "0") int dx) {
        
        System.out.println("Move request: dy=" + dy + ", dx=" + dx);

        //Check for valid request
        if((Math.abs(dy)+Math.abs(dx)) > 1) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        //Record proposed new player position
        int proposedNewX = playerX + dx;
        int proposedNewY = playerY + dy;

        //Check for going beyond map boundary
        if (!((proposedNewX >= 0 && proposedNewX < MAP_WIDTH) && (proposedNewY >= 0 && proposedNewY < MAP_HEIGHT))) { 
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }  
        
        // Wrap x coordinate
        if (newX < 0) {
            newX += MAP_WIDTH;
        } else if (newX >= MAP_WIDTH) {
            newX -= MAP_WIDTH;
        }
        
        // Clamp y coordinate
        if (newY < 0) {
            newY = 0;
        } else if (newY >= MAP_HEIGHT) {
            newY = MAP_HEIGHT - 1;
        }
        
        playerX = newX;
        playerY = newY;
        
        System.out.println("Player position: x=" + playerX + ", y=" + playerY);
        
        return "ok";
    }
}
