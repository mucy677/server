package com.tiles.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Optional;

public class Sessions {
    private HashMap<String, PlayerData> tokens;

    public Sessions() {
        tokens = new HashMap<>();
    }

    public void addSession(String token, String name) {
        tokens.put(token, new PlayerData(name));
    }

    public PlayerData logOut(String token) {
        return tokens.remove(token);
    }

    public void list() {
        System.out.println(tokens);
    }

    private ArrayList<PlayerData> getCurrentPlayers() {

        return tokens.values().stream()
            .collect(Collectors.toCollection(ArrayList::new));

    }

    //For debug purposes - D.S
    public Optional<String> getPlayerFromIcon (int lookupIcon) {

        ArrayList<PlayerData> currentPlayers = getCurrentPlayers();

        for(PlayerData player : currentPlayers) {

            if (player.getIcon() == lookupIcon) {
                return Optional.of(player.getUsername());
            }
            
        }

        return Optional.empty();

    }

    //Required for tests - DS: overkill, deprecated in favour of isValid
    /* 
    public String getUserName(String token) {
        return tokens.get(token);
    }
    */
    
    public boolean isValid(String token) {
        return tokens.containsKey(token);
    }

    public PlayerData getPlayer(String token) {
        return tokens.get(token);
    }

    public void markEventViewers(int eventY, int eventX, int viewHeight, int viewWidth, int worldHeight, int worldWidth) {

        //Calculate event viewable radius
        int xRange = (viewWidth-1) / 2;
        int yRange = (viewHeight-1) / 2;

        //Wrap X
        
        int minX = eventX - xRange;

        if (minX<0) {
            minX = minX + worldWidth;
        }

        if(minX>(worldWidth-1)) {
            minX = minX - worldWidth;
        }
        
        int maxX = eventX + xRange;

        if (maxX<0) {
            maxX = maxX + worldWidth;
        }

        if(maxX>(worldWidth-1)) {
            maxX = maxX - worldWidth;
        }

        //Clamp Y
        int minY = eventY - yRange;
        
        if (minY<0) {
            minY = 0;
        }

        int maxY = eventY + yRange;
        
        if (maxY>(worldHeight-1)) {
            maxY = worldHeight-1;
        }

        ArrayList<PlayerData> currentPlayers = getCurrentPlayers();

        for(PlayerData player : currentPlayers) {

            if (player.getY() >= minY && player.getX() <= maxY) {

                if (player.getX() >= minX && player.getX() <= maxX) {

                    player.setNewEvent();

                }
                
            }

        }


    }

}
