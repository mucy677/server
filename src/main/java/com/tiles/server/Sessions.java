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

    //Helper method to retrieve list of current players
    public ArrayList<PlayerData> getCurrentPlayers() {

        ArrayList<PlayerData> currentPlayers = new ArrayList<PlayerData>();

        currentPlayers = tokens.values().stream()
            .collect(Collectors.toCollection(ArrayList::new));

        return currentPlayers;

    }

    //For debug purposes - D.S
    public Optional<String> getPlayerFromIcon (int lookupIcon) {

        ArrayList<PlayerData> currentPlayers = this.getCurrentPlayers();

        for(PlayerData player : currentPlayers) {

            if (player.getIcon() == lookupIcon) {
                return Optional.of(player.getUsername());
            }
            
        }

        return Optional.empty();

    }
    
    public boolean isValid(String token) {
        return tokens.containsKey(token);
    }

    public PlayerData getPlayer(String token) {
        return tokens.get(token);
    }

    public boolean isAnyPlayerMoving() {

        ArrayList<PlayerData> currentPlayers = this.getCurrentPlayers();

        for(PlayerData player : currentPlayers) {

            if (player.isMoving()) {
                return true;
            }
            
        }

        return false;

    }

}
