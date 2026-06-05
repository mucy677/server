package com.tiles.server;

import java.util.ArrayList;
import java.util.Optional;

public class PlayerData {
    
    private String username;
    private int characterIcon; 
    private int xPos;
    private int yPos;

    private Boolean spawned;
    private Boolean isMoving;

    private ArrayList<Item> inventory = new ArrayList<Item>();
    private static final int maxItems = 2;

    public PlayerData(String name) {
        this.username = name;

        //Setting Default start position
        xPos = 5; 
        yPos = 5;

        characterIcon = asciiSum(name) % 10;

        spawned = false;
        isMoving = false;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean getSpawned() {
        return this.spawned;
    }

    public void hasSpawned() {
        this.spawned = true;
    }

    public synchronized int getX() {
        return this.xPos;
    }
    
    public synchronized int getY() {
        return this.yPos;
    }

    public int getIcon() {
        return this.characterIcon;
    }

    public void setMoving() {
        this.isMoving=true;
    }

    public void setStationary() {
        this.isMoving=false;
    }

    public boolean isMoving() {
        return this.isMoving;
    }

    private int asciiSum(String name) {
        if (name.isEmpty()) {
            return 0;
        }

        return name.charAt(0) + asciiSum(name.substring(1));
    }

    public synchronized void setPos(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    public boolean inventoryFull() {

        if(this.inventory.size()==maxItems) {
            return true;
        } else {
            return false;
        }

    }

    public boolean inventoryEmpty() {

      return this.inventory.isEmpty();
        
    }

    public ArrayList<Item> getCurrentInventory() {

        return this.inventory;

    }

    public void resetInventory() {
        
        this.inventory.clear();
        
    }

    public boolean hasAnyItems() {

        if(this.inventory.size()>0) {
            return true;
        } else {
            return false;
        }

    }

    public Optional<Item> trySwap (Item item) {

        //Check if there is already an identical item class stored
        for (int i = 0; i < this.inventory.size(); i++ ) {

            if(item.getType().equals(this.inventory.get(i).getType())) {
                
                //Effect swap
                Item drop = this.inventory.get(i);
                this.inventory.remove(i);

                this.inventory.add(item);

                return Optional.of(drop);

            } 

        }
        
        return Optional.empty();

    }

    public void add(Item item) {

        this.inventory.add(item);
         
    }

    public Item removeItem() {

        //Remove last item from inventory:
        Item drop = this.inventory.getLast();
        this.inventory.removeLast();
        return drop;
        
    }

    public boolean hasItem(Item item) {
        
        return this.inventory.contains(item);
        
    }

}
