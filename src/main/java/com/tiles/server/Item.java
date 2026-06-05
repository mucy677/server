package com.tiles.server;

import java.util.Objects;

public class Item {
    
    private String ID;
    private String description;
    private String type;
    private int spawnX;
    private int spawnY;

    //constructor
    public Item(String newID, String newDesc, String newType, int newSpawnY, int newSpawnX) {
        this.ID = newID;
        this.description = newDesc;
        this.type = newType;
        this.spawnY = newSpawnY;
        this.spawnX = newSpawnX;
    }

    @Override
    public boolean equals(Object obj) {

        // 1. Check for reference identity (same memory address)
        if (this == obj) {
            return true;
        }
        
        // 2. Check for null and ensure exact class type matches
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        
        // 3. Cast the object to the item target type
        Item other = (Item) obj;
        
        // 4. Compare meaningful primitive and object fields, we will exclude spawn locations as theyre not critical
        return Objects.equals(this.ID, other.ID) 
            && Objects.equals(this.description, other.description) 
            && Objects.equals(this.type, other.type);
    
    }

    @Override
    public int hashCode() {
        
        // Always override hashCode alongside equals
        return Objects.hash(ID, description, type);

    }

    // Getters
    public String getID() { return ID; }
    public String getDesc() { return description; }
    public String getType() { return type; }
    public int getSpawnX() { return spawnX; }
    public int getSpawnY() { return spawnY; }

}
