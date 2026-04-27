package com.tiles.server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin(origins = "*")
public class MyController {
	
	// Simple game state tracking
	private int playerX = 5;
	private int playerY = 5;
	
	// Map dimensions
	//private static final int MAP_WIDTH = 20; - now held in World class - DS
	//private static final int MAP_HEIGHT = 20; - now held in World class - DS
    
    //loading credentials from file not working -- look into loading resources with Spring
    
    // AccountDetails accountDetails = new AccountDetails("AccountDetails.txt");
    // //Create HashMap of account credentials
    // HashMap<String, String> logins = accountDetails.getMap();

    //@Autowired - removed annotation, potential conflict with use of final variable in constructor - DS
    
    private final AccountDetails accountDetails;
    private final Sessions sessions = new Sessions();

    private final World world;

    public MyController(AccountDetails accountDetails, World world) {
        
        this.accountDetails = accountDetails;
        this.world = world;

    }

    //Position Setter (required for tests)
    public void setPosition(int newX, int newY) { 
        this.playerX = newX; 
        this.playerY = newY;
    }

    //Map Getter (required for tests)
    public String[][] getMap() {
        return this.world.getMap();
    }

    //Session verification (required for tests)
    public String verifySession(String token) {
        return this.sessions.getUserName(token);
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
        if(loginData.getEncpswrd().equals(accountDetails.getMap().get(loginData.getName()))) {
            System.out.println(loginData.getName() + " logged in");

            //generate token
            String token = loginData.generateToken();

            //Add token as key to HashMap tracking current sessions
            sessions.addSession(token, loginData.getName());

            //Return response with JSON formatted token
            return new ResponseEntity<>("{\"session\": " + "\"" + token + "\"}", HttpStatus.OK);

        } else if (!loginData.getEncpswrd().equals(accountDetails.getMap().get(loginData.getName()))) {
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
                if (mapY < 0 || mapY >= this.world.getHeight() || mapX < 0 || mapX >= this.world.getWidth()) {
                    mapWindow[row][col] = " ";
                } else {
                    mapWindow[row][col] = this.world.getMap()[mapY][mapX]; //MAP[mapY][mapX] 
                }
            }
        }
        
        // Build response
        InfoResponse response = new InfoResponse(playerX, playerY, top, left, bottom, right, mapWindow);

        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }
    
    @GetMapping("/move")
    public ResponseEntity<MoveResponse>  move(
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
        if (!((proposedNewX >= 0 && proposedNewX < this.world.getWidth()) && (proposedNewY >= 0 && proposedNewY < this.world.getHeight()))) { 
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }  

        //Check for moving into blocking terrain
        if(this.world.isBlocking(proposedNewY,proposedNewX) == true) {
            System.out.println("Movement blocked by: " + this.world.getTileDescription(proposedNewY, proposedNewX));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        //Not sure if this wrapping/clamping logic is needed, but left just in case - DS
        /* 
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
        */

        //Move request is valid, update stored player location on server
        playerX = proposedNewX;
        playerY = proposedNewY;
        
        System.out.println("New player position: x=" + playerX + ", y=" + playerY);
        
        // Build response
        MoveResponse response = new MoveResponse(playerX, playerY);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}