package com.tiles.server;

import java.util.HashMap;

public class Sessions {
    private HashMap<String, String> tokens;

    public Sessions() {
        tokens = new HashMap<>();
    }

    public void addSession(String token, String name) {
        tokens.put(token, name);
    }

    public String logOut(String token) {
        return tokens.remove(token);
    }

    public void list() {
        System.out.println(tokens);
    }

    //Required for tests
    public String getUserName(String token) {
        return tokens.get(token);
    }
}
