package com.tiles.server;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import io.micrometer.common.util.StringUtils;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class MyController {
	
	// Simple game state tracking -- changing in favour of player specific tracking -- 
    //      maybe leave for testing or should be able to modify tests because of the testToken

	// private int playerX = 5;
	// private int playerY = 5;
	
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

    private final Item key;

    private static final int viewWidth = 11;
    private static final int viewHeight = 11;

    private static final String ENDPOINT_MOVE = "/move";  //prometheus constants
    
    private static final Counter GAME_REQUESTS = Counter.build()
            .name("game_requests_total")
            .help("Total game requests")
            .labelNames("endpoint", "status")
            .register();

    private static final Histogram GAME_LATENCY = Histogram.build()
            .name("game_request_duration_seconds")
            .help("Game request latency in seconds")
            .labelNames("endpoint")
            .register();

    public MyController(AccountDetails accountDetails, World world) {
        
        //Load external class dependencies
        this.accountDetails = accountDetails;
        this.world = world;

        //Load reference objects
        this.key = this.world.getItem("k").orElseThrow(); //Key should always exist, if not something bad has happened, throw exception

    }

    //Position Setter (required for tests)
    public void setPosition(int newX, int newY, String token) { 
        // this.playerX = newX; 
        // this.playerY = newY;

        //modifying method to account for player tracking
        PlayerData player = sessions.getPlayer(token);
        player.setPos(newX, newY);
    }

    //Session Getter (required for tests)
    public Sessions getSessions(){
        return this.sessions;
    }

    //Map Getter (required for tests)
    public String[][] getMap() {
        return this.world.getMap();
    }

    //Session verification (required for tests)
    public Boolean sessionValid(String token) {
        return this.sessions.isValid(token);
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
        Histogram.Timer timer = GAME_LATENCY.labels("/login").startTimer();

        try {

        //Check for bad request
        
        if (StringUtils.isBlank(loginData.getName())) {
            System.out.println("Bad username field - Bad Request");
            GAME_REQUESTS.labels("/login", "400").inc();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        if  (StringUtils.isBlank(loginData.getEncpswrd())) {
            System.out.println("Bad password field - Bad Request");
            GAME_REQUESTS.labels("/login", "400").inc();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        //Checking if sent password matches password stored against sent name
        if(loginData.getEncpswrd().equals(accountDetails.getMap().get(loginData.getName()))) {
            System.out.println(loginData.getName() + " logged in");

            //generate token
            String token = loginData.generateToken();

            //Add token as key to HashMap tracking current sessions
            sessions.addSession(token, loginData.getName());
            System.out.println("character icon = " + sessions.getPlayer(token).getIcon());

            // PlayerData player = sessions.getPlayer(token);
            // world.drawIcon(player.getY(), player.getX(), player.getIcon());

            //Return response with JSON formatted token
            GAME_REQUESTS.labels("/login", "200").inc();
            return new ResponseEntity<>("{\"session\": " + "\"" + token + "\"}", HttpStatus.OK);

        } else {
            System.out.println("Invalid Credentials");
            GAME_REQUESTS.labels("/login", "401").inc();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } 

        } finally {
        timer.observeDuration(); //end of the timer
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<String> handleLogOut(@RequestParam String session) {

        if (sessions.isValid(session)){
            PlayerData player = sessions.getPlayer(session);

            System.out.println(player.getUsername() + " logged out");
            sessions.markEventViewers(player.getY(), player.getX(), viewHeight, viewWidth, world.getHeight(),world.getWidth());
            world.eraseIcon(player.getY(), player.getX(), player.getIcon());

            sessions.logOut(session);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/info")
    public ResponseEntity<InfoResponse> info(
    		@RequestParam String session,
    		@RequestParam(defaultValue = "5") int y,
    		@RequestParam(defaultValue = "5") int x) {
        Histogram.Timer timer = GAME_LATENCY.labels("/info").startTimer();
        try {
        // Validate session token
        if (!sessions.isValid(session)) {
            GAME_REQUESTS.labels("/info", "401").inc();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //Player specific location
        PlayerData player = sessions.getPlayer(session);

        
        //--Depecrecated bandaid fix for /info desync when relogging --
        //--Replaced with position reset        
        //Position seems to persist on client side after log out which can cause issues when having a default location for PlayerData objects
        //This sets the player's location to wherever it is at login
        //The other option is to reset the map window to the default on logout
        // if ((player.getX() == 100) && (player.getY() == 100)){ // these could also be == null but would need Integer wrapping
        //     player.setPos(x, y);
        // }

        int playerX = player.getX();
        int playerY = player.getY();
        
        System.out.println("Info request: x=" + x + ", y=" + y);

        //reset position on first login
        if (!player.getSpawned()) {
            x = playerX;
            y = playerY;

            player.hasSpawned();
        }
        
        //Return status 204 and exit early, if received coordinates do not match current player location stored on server
        if (x!=playerX||y!=playerY) {
            GAME_REQUESTS.labels("/info", "204").inc();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        //Fast Info
        if (!player.viewedNewEvent()) {

            System.out.println(player.getUsername() + " --> " + "map view unchanged!");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
        }
        
        //draw new icon
        //drawing in /info seems to be the most responsive but its still not always perfect
        world.drawIcon(playerY, playerX, player.getIcon());
        
        // Define view window (11x11 centered on player)
        //int viewWidth = 11;
        //int viewHeight = 11;
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

                // Check vertically for black boundary squares
                if (mapY < 0 || mapY >= this.world.getHeight()) { 

                    mapWindow[row][col] = " ";

                } else {

                    //Check for and apply x left wrap
                    if (mapX < 0) {
                        mapX = this.world.getWidth() + mapX; 
                    }

                    //Check for and apply x right wrap
                    if (mapX >= this.world.getWidth()) {
                        mapX = mapX - this.world.getWidth(); 
                    }

                    mapWindow[row][col] = this.world.getTile(mapY,mapX); //MAP[mapY][mapX] 
                    
                }
            }
        }
        
        // Build response
        InfoResponse response = new InfoResponse(playerX, playerY, top, left, bottom, right, mapWindow);
        GAME_REQUESTS.labels("/info", "200").inc();
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    
    } finally {
        timer.observeDuration();
        }
    }
    
    @GetMapping("/move")
    public ResponseEntity<MoveResponse>  move(
    		@RequestParam String session,
    		@RequestParam(defaultValue = "0") int dy,
    		@RequestParam(defaultValue = "0") int dx) {
    Histogram.Timer timer = GAME_LATENCY.labels(ENDPOINT_MOVE).startTimer();
    try {        
        // Validate session token
        if (!sessions.isValid(session)) {
            GAME_REQUESTS.labels("/move", "401").inc();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //Player specific location
        PlayerData player = sessions.getPlayer(session);
        int playerX = player.getX();
        int playerY = player.getY();
        
        System.out.println("Move request: dy=" + dy + ", dx=" + dx);

        //Check for valid request
        if((Math.abs(dy)+Math.abs(dx)) > 1) {
            GAME_REQUESTS.labels("/move", "204").inc();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        int prevX = playerX;
        int prevY = playerY;
        
        //Record proposed new player position
        int proposedNewX = playerX + dx;
        int proposedNewY = playerY + dy;

        //Check for going beyond map height boundary
        if ((proposedNewY >= 0 && proposedNewY < this.world.getHeight()) == false) { 
            System.out.println("Movement blocked by map height boundary");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }  

        //Check and adjust for left wrapping
        if (proposedNewX < 0) {
                proposedNewX = this.world.getWidth() - 1;
                System.out.println("Left wrap");
        } 
        
        //Check and adjust for right wrapping
        if (proposedNewX >= this.world.getWidth()) {
                proposedNewX = 0;
                System.out.println("Right wrap");
        }

        //Check for other player as blocking element:
        Optional<Integer> tilePlayerIcon = this.world.checkIfPlayerOnTile(proposedNewY, proposedNewX);

        if(tilePlayerIcon.isPresent()) { //Someone else is there
            
            String blockerUsername = this.sessions.getPlayerFromIcon(tilePlayerIcon.get()).orElseThrow();
            System.out.println(player.getUsername() + " --> " + "Movement blocked by other player: " + blockerUsername);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
        }
        
        //Check for moving into blocking terrain
        Terrain priorityTerrain = this.world.getTerrainOfPassagePriority(proposedNewY,proposedNewX);

        if(priorityTerrain.isBlocking()) {
            System.out.println("Movement blocked by " + priorityTerrain.getDesc());
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

        sessions.markEventViewers(proposedNewY, proposedNewX, viewHeight, viewWidth, world.getHeight(),world.getWidth());
        player.setPos(proposedNewX, proposedNewY);

        sessions.markEventViewers(prevY, prevX, viewHeight, viewWidth, world.getHeight(),world.getWidth());
        world.eraseIcon(prevY, prevX, player.getIcon());
        //world.drawIcon(playerY, playerX, player.getIcon());
        
        System.out.println("New player position: x=" + playerX + ", y=" + playerY);
        
        // Build response
        MoveResponse response = new MoveResponse(playerX, playerY);
        GAME_REQUESTS.labels("/move", "200").inc();
        return new ResponseEntity<>(response, HttpStatus.OK);

    } finally {
        // Always fires — captures latency for every request regardless of outcome
        timer.observeDuration();
        }
    }

    @GetMapping("/use")
    public ResponseEntity<String> use(
        @RequestParam String session, 
        @RequestParam(defaultValue = "0") int dy,
    	@RequestParam(defaultValue = "0") int dx) {
        Histogram.Timer timer = GAME_LATENCY.labels("/use").startTimer();
        try {
        // Validate session token
        if (!sessions.isValid(session)) {
            GAME_REQUESTS.labels("/use", "401").inc();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        int y = Math.abs(dy);
        int x = Math.abs(dx);

        PlayerData player = sessions.getPlayer(session);

        //Check if use request has valid range
        //if either is 1 or both 0 
        if (((y == 0 && x == 0) || (y == 1 && x == 0) || (y == 0 && x == 1)) == false) {

            System.out.println("Use request is outside valid range!");
            GAME_REQUESTS.labels("/use", "204").inc();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            
        }

        Optional<Terrain> useableTerrain = world.containsUsable(player.getY() + dy, player.getX() + dx);

        if (useableTerrain.isEmpty()){

            System.out.println("No useable terrain at requested location y: " + (player.getY() + dy) + ", x: " + (player.getX() + dx));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
        }

        String useableTerrainKey = useableTerrain.get().getKey();

        switch(useableTerrainKey) {

            //Handle open door
            case "d" -> {
                
                if (player.hasItem(key)) {

                    sessions.markEventViewers(player.getY() + dy, player.getX() + dx, viewHeight, viewWidth, world.getHeight(),world.getWidth());
                    world.lockDoor(player.getY() + dy, player.getX() + dx);
                    System.out.println(player.getUsername() + " has locked door.");
                    GAME_REQUESTS.labels("/use", "200").inc();
                    return new ResponseEntity<>(HttpStatus.OK);   
            
                } else {

                    System.out.println(player.getUsername() + " does not have key, cannot lock door!");

                }

            }

            //Handle closed door
            case "D" -> {
                
                if (player.hasItem(key)) {

                    sessions.markEventViewers(player.getY() + dy, player.getX() + dx, viewHeight, viewWidth, world.getHeight(),world.getWidth());
                    world.unlockDoor(player.getY() + dy, player.getX() + dx);
                    System.out.println(player.getUsername() + " has unlocked door.");
                    GAME_REQUESTS.labels("/use", "200").inc();
                    return new ResponseEntity<>(HttpStatus.OK);   
            
                } else {

                    System.out.println(player.getUsername() + " does not have key, cannot unlock door!");

                }

            }

            default -> System.out.println("Unhandled interaction type!");
        
        }

        //For all unsuccessful interactions, return:
        GAME_REQUESTS.labels("/use", "204").inc();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 

        } finally {
        timer.observeDuration();
        }
        
    }

    @GetMapping("/take")
    public ResponseEntity<String> take(@RequestParam String session) {
    Histogram.Timer timer = GAME_LATENCY.labels("/take").startTimer();
    try {
        
        // Validate session token
        if (!sessions.isValid(session)) {
            GAME_REQUESTS.labels("/take", "401").inc();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        PlayerData player = sessions.getPlayer(session);
        
        /* 
        if (player.inventoryFull()) {
            System.out.println("Unable to take item: " + player.getUsername() + " inventory is full!");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        */

        Optional<Item> tileItem = world.containsItems(player.getY(),player.getX());

        if (tileItem.isEmpty()) {
            System.out.println("No moveable item at current location Y: " + player.getY() + ", X: " + player.getX());
            GAME_REQUESTS.labels("/take", "204").inc();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Item takenItem = tileItem.get();
        Optional<Item> swapResult = player.trySwap(takenItem);

        if (swapResult.isPresent()) {

            world.take(player.getY(),player.getX(), takenItem); //remove from map

            Item droppedItem = swapResult.get();
            world.place(player.getY(),player.getX(), droppedItem); 
            
            sessions.markEventViewers(player.getY(), player.getX(), viewHeight, viewWidth, world.getHeight(),world.getWidth());

            System.out.println("Successfully stored: " + takenItem.getDesc() + ", dropped: " + droppedItem.getDesc());
            GAME_REQUESTS.labels("/take", "200").inc();
            return new ResponseEntity<>(HttpStatus.OK);
            
        }

        if (player.inventoryFull()) {

            System.out.println("Unable to take item: " + player.getUsername() + " inventory is full!");
            GAME_REQUESTS.labels("/take", "204").inc();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        }

        player.add(takenItem);
        world.take(player.getY(),player.getX(), takenItem); //remove from map
        sessions.markEventViewers(player.getY(), player.getX(), viewHeight, viewWidth, world.getHeight(),world.getWidth());

        System.out.println("Successfully stored: " + takenItem.getDesc());
        GAME_REQUESTS.labels("/take", "200").inc();
        return new ResponseEntity<>(HttpStatus.OK);
        } finally {
        timer.observeDuration();
        }

    }

    @GetMapping("/place")
    public ResponseEntity<String> place(@RequestParam String session) {
        Histogram.Timer timer = GAME_LATENCY.labels("/place").startTimer();
        try {
        
        // Validate session token
        if (!sessions.isValid(session)) {
            GAME_REQUESTS.labels("/place", "401").inc();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        PlayerData player = sessions.getPlayer(session);
        
        if (player.inventoryEmpty()) {
            System.out.println("Unable to place item: " + player.getUsername() + " inventory is empty!");
            GAME_REQUESTS.labels("/place", "204").inc();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Optional<Item> tileItem = world.containsItems(player.getY(),player.getX()); 

        if (tileItem.isPresent()) {

            //Some item is already there, can't place
            System.out.println("Unable to place item at location Y: " + player.getY() + ", X: " + player.getX() 
                + ", as another item: " + tileItem.get().getDesc() + " already exists at this location!");
            GAME_REQUESTS.labels("/place", "204").inc();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
        }

        //Can place item, free tile
        Item droppedItem = player.removeItem(); //safe to call, inventory known to be non-empty
        sessions.markEventViewers(player.getY(), player.getX(), viewHeight, viewWidth, world.getHeight(),world.getWidth());
        world.place(player.getY(),player.getX(), droppedItem); 
        System.out.println("Successfully placed: " + droppedItem.getDesc());
        GAME_REQUESTS.labels("/place", "200").inc();
        return new ResponseEntity<>(HttpStatus.OK);

    
    } finally {
    timer.observeDuration();
    }
    }
}