package com.tiles.server;

public class InfoResponse {
    private int x;
    private int y;
    private int top;
    private int left;
    private int bottom;
    private int right;

    private String[][] info;

    //constructor
    public InfoResponse(int newX, int newY, int newTop, int newLeft, int newBottom, int newRight, String[][] newInfo) {
        this.x = newX;
        this.y = newY;
        this.top = newTop;
        this.left = newLeft;
        this.bottom = newBottom;
        this.right = newRight;
        
        this.info = newInfo;
    }

    // Getters and setters (required by most JSON mapping libraries like Jackson)
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getTop() { return top; }
    public void setTop(int top) { this.top = top; }
    public int getLeft() { return left; }
    public void setLeft(int left) { this.left = left; }
    public int getBottom() { return bottom; }
    public void setBottom(int bottom) { this.bottom = bottom; }
    public int getRight() { return right; }
    public void setRight(int right) { this.right = right; }

    public String[][] getInfo() { return info; }
    public void setInfo(String[][] info) { this.info = info; }
    
}
