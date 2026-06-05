package com.tiles.server;

import java.io.IOException;
import java.io.InputStreamReader;

import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import com.tiles.server.ItemSpawnPoint;

@Component
public class World {

    // Map dimensions
	private static final int MAP_WIDTH = 20;
	private static final int MAP_HEIGHT = 20;
    
    //Map to be loaded from text file
    private String[][] MAP = new String[MAP_HEIGHT][MAP_WIDTH];

    private Map<String, Terrain> terrains; //terrains register
    private ArrayList<Terrain> useableTerrains = new ArrayList<Terrain>(); //useable terrains list
    private ArrayList<Item> items = new ArrayList<Item>(); //world items, eg key, axe
    
    public World() {
       
        loadMap();
        loadTerrainLegend();
        loadItems();

    }

    private void loadMap() {

        ClassPathResource resource = new ClassPathResource("Map.txt"); 
        Pattern pattern = Pattern.compile("'([^']*)'"); //Take contents between each: ' ' 
    
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            //MAP = Files.lines(mapPath)
            MAP = reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty()) // skip empty lines
                .filter(line -> line.matches("\\[.+\\],")) //matches general expected line structure
                .map(line -> parseLine(line, pattern))   
                .filter(parts -> parts.length == MAP_WIDTH) //Skip lines missing entries.
                .toArray(String[][]::new);

        } catch (IOException e) {

            throw new RuntimeException("Unable to load map file!", e);  

        }

        System.out.println("Map File:");
        Arrays.stream(MAP)
      		.map(Arrays::toString)
      		.forEach(System.out::println);

    }

    private void loadTerrainLegend() {

        ClassPathResource resource = new ClassPathResource("Terrains.txt"); 

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            terrains = reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty()) // skip empty lines
                .map(line -> line.split("\\s*\\|\\s*"))  //split regex handles '|' delimeter with optional padding on either side.
                .filter(parts -> parts.length == 4) //Skip lines missing entries.
                .collect(Collectors.toMap(
                        parts -> parts[0].substring(0,1), //single character key (as string)
                        parts -> new Terrain(
                                parts[0].substring(0,1),
                                parts[1], //Tile description
                                parts[2].equalsIgnoreCase("blocking"), //true if "blocking", otherwise false
                                parts[3].equalsIgnoreCase("useable") //true if "useable", otherwise false
                        )));

        } catch (IOException e) {

            throw new RuntimeException("Unable to load terrain legend!", e);

        }
        
        System.out.println("Terrain key:");
        terrains.forEach((k, v) ->
            System.out.println(v.getKey() + " | " + v.getDesc() + " | " + v.isBlocking() + " | " + v.isUseable()));

        useableTerrains = terrains.values().stream()
            .filter(terrain -> terrain.isUseable()==true) 
            .collect(Collectors.toCollection(ArrayList::new));

        System.out.println("Useable terrains:");
        for(Terrain terrain : this.useableTerrains) {
            System.out.println(terrain.getDesc());
        }

    }

    private void loadItems() {

        ClassPathResource resource = new ClassPathResource("Items.txt"); 

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            items = reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty()) // skip empty lines
                .map(line -> line.split("\\s*\\|\\s*"))  //split regex handles '|' delimeter with optional padding on either side.
                .filter(parts -> parts.length == 3) //Skip lines missing entries.
                .map(parts -> new Item(parts[0], parts[1], parts[2], this.getSpawn(parts[0]).orElseThrow().spawnY(), this.getSpawn(parts[0]).orElseThrow().spawnX()))
                .collect(Collectors.toCollection(ArrayList::new));

        } catch (IOException e) {

            throw new RuntimeException("Unable to load items list!", e);

        }
        
        System.out.println("Items list:");
        for(Item item : this.items) {
            System.out.println(item.getID() + " | " + item.getDesc() + " | " + item.getType() + " | " + item.getSpawnY() + " | " + item.getSpawnX());
        }

    }

    //Helper method, applies regex to load map
    private static String[] parseLine(String line, Pattern pattern) {

        Matcher matcher = pattern.matcher(line);
        ArrayList<String> tilesList = new ArrayList<>();

        while (matcher.find()) {
            tilesList.add(matcher.group(1));
        }

        return tilesList.toArray(new String[tilesList.size()]);

    }

    //On startup used to search the map for starting location of an Item
    private Optional<ItemSpawnPoint> getSpawn(String ID) {

        for (int y = 0; y < MAP_HEIGHT; y++) {

            for (int x = 0; x < MAP_WIDTH; x++) { 

                 String tile = this.MAP[y][x];
                 
                 if(tile.contains(ID)) {

                    ItemSpawnPoint spawn = new ItemSpawnPoint(y, x);
                    return Optional.of(spawn);

                 }
                    
            }

        }

        return Optional.empty();

    }
    
    public synchronized String[][] getMap() {
        return this.MAP;
    }

    // Returns string representing a particular map location
    public synchronized String getTile(int Y, int X) {
        return this.MAP[Y][X];
    }

    //Adds a player's character to a particular map location
    public synchronized void drawIcon(int Y, int X, int icon){
        String tile = getTile(Y,X) + icon;
        this.MAP[Y][X] = tile;
    }

    //Removes a player's character from a particular map location
    public synchronized void eraseIcon(int Y, int X, int icon){
        //replace instead of substring to not assume the player will always be the final char
        String tile = getTile(Y,X).replace(Integer.toString(icon), "");
        this.MAP[Y][X] = tile;
    }

    public Map<String, Terrain> getTerrains() {
        return this.terrains;
    }

    public synchronized int getWidth() {
        return MAP_WIDTH;
    }
    
    public synchronized int getHeight() {
        return MAP_HEIGHT;
    }

    //Returns an item from the world's item list. 
    public Optional<Item> getItem(String ID) {
        
        for (Item item : this.items) {
            
            if (item.getID().equals(ID)) {
                
                return Optional.of(item);

            }

        }

        return Optional.empty();

    }

    //Checks if a player is at a particular map location
    public Optional<Integer> checkIfPlayerOnTile (int Y, int X) {
        
        String tile = this.MAP[Y][X];

        for (int i = 0; i < 10; i++) {
        
            if (tile.contains(String.valueOf(i))) {
                return Optional.of(i);
            }
        }
        
        return Optional.empty();

    }
    
    //Returns terrain element at a given map location, that determines right of passage/blockage.
    public Terrain getTerrainOfPassagePriority(int Y, int X) {

        String tile = this.MAP[Y][X];
      
        //Check for unit tile
        if (tile.length() == 1) {
            return this.terrains.get(tile);
        } 
        
        //Check for bridge case
        if (tile.charAt(1) == 'b') {
            return this.terrains.get("b");
        }

        //Check for closed door case
        if (tile.charAt(1) == 'D') {
            return this.terrains.get("D");
        }

        return this.terrains.get(tile.substring(0,1));

    }
    
    public void lockDoor(int Y, int X){

        String tile = this.MAP[Y][X];
        this.MAP[Y][X] = tile.replace("d", "D");
    
    }

    public void unlockDoor(int Y, int X){

        String tile = this.MAP[Y][X];
        this.MAP[Y][X] = tile.replace("D", "d");
    
    }

    //Checks whether a map location contains a useable terrain feature, eg door.
    public Optional<Terrain> containsUsable(int Y, int X) {

        String tile = this.MAP[Y][X];

        for (Terrain useableTerrain : this.useableTerrains) {
        
            if (tile.contains(useableTerrain.getKey())) {

                return Optional.of(useableTerrain);

            }

        }

        return Optional.empty();

    }

    //Removes item from game map
    public void take(int Y, int X, Item item) {
        
        String tile = this.MAP[Y][X];
        this.MAP[Y][X] = tile.replace(item.getID(), ""); //update map with removed item

    }

    //Finds first available Item spawnpoint, to which an item can be returned from a player's inventory, on logout. 
    public Optional<ItemSpawnPoint> getFreeSpawnPoint() {

        for (Item item : this.items) {
            
            if (this.containsItems(item.getSpawnY(), item.getSpawnX()).isEmpty()) {

                ItemSpawnPoint freeSpawnPoint = new ItemSpawnPoint(item.getSpawnY(), item.getSpawnX());
                return Optional.of(freeSpawnPoint);

            }

        }

        //All occupied - should never happen in regular usage
        return Optional.empty();

    }

    //Checks whether map location contains an existing item
    public Optional<Item> containsItems(int Y, int X) {

        String tile = this.MAP[Y][X];

        //Check if an item already exists at this tile
        for (Item item : this.items) {
            
            if (tile.contains(item.getID())) {
                
                return Optional.of(item);

            }

        }

        //No items found
        return Optional.empty();

    }

    //Places an item onto game map
    public void place(int Y, int X, Item item) {

        String tile = this.MAP[Y][X];
    
        //First check if there is player recorded at last character of tile string:
        String tileLastChar = tile.substring(tile.length()-1,tile.length()); //peration works with unit strings
        if (tileLastChar.matches("[0-9]")) { 

            //You can assume that if there is a player number present, the tile string contains at least 2 chars
            tile = tile.substring(0,tile.length()-1) + item.getID() + tileLastChar;

        } else {

            tile = tile + item.getID();

        }

        this.MAP[Y][X] = tile; //update map with new item

    }

}