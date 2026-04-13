package com.tiles.server;

public class MoveResponse {
    
    private int x;
    private int y;

    //constructor
    public MoveResponse(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    // Getters and setters (required by most JSON mapping libraries like Jackson)
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
}
