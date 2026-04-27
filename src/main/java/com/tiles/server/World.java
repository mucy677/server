package com.tiles.server;

//import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
//import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
//import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class World {

    // Map dimensions
	private static final int MAP_WIDTH = 20;
	private static final int MAP_HEIGHT = 20;
    
    //Map to be loaded from text file
    private String[][] MAP = new String[MAP_HEIGHT][MAP_WIDTH];

    // Record to store the terrain details (second + third columns from terrain text file)
    private record tileInfo(String description, boolean blocking) {}

    private Map<String, tileInfo> terrains;
    
    public World() {
       
        loadMap();
        loadTerrainLegend();

    }

    private void loadMap() {
            
        //Old approach:
        //Path mapPath = getFilePath("Map.txt");

        //Austin's approach:
        //InputStream is = resource.getInputStream();
        //String map = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        ClassPathResource resource = new ClassPathResource("Map.txt"); 
    
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            //MAP = Files.lines(mapPath)
            MAP = reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty()) // skip empty lines
                .map(line -> line.split(", "))  
                .filter(parts -> parts.length == MAP_WIDTH) //Skip lines missing entries.
                .map(parts -> Arrays.stream(parts)
                                    .map(element -> element.substring(1,2))
                                    .toArray(String[]::new))
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

        //Old approach:
        //Path terrainsPath = getFilePath("Terrains.txt");
        //terrains = Files.lines(terrainsPath)
            
        //Austin's approach:
        //ClassPathResource resource = new ClassPathResource("Terrains.txt"); 
        //InputStream is = resource.getInputStream();
        //String terrainString = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        ClassPathResource resource = new ClassPathResource("Terrains.txt"); 

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            terrains = reader.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty()) // skip empty lines
                .map(line -> line.split("\\s*\\|\\s*"))  //split regex handles '|' delimeter with optional padding on either side.
                .filter(parts -> parts.length == 3) //Skip lines missing entries.
                .collect(Collectors.toMap(
                        parts -> parts[0].substring(0,1), //single character key (as string)
                        parts -> new tileInfo(
                                parts[1], //Tile description
                                parts[2].equalsIgnoreCase("blocking") //true if "blocking", otherwise false
                        )));
        

        } catch (IOException e) {

            throw new RuntimeException("Unable to load terrain legend!", e);

        }
        
        System.out.println("Terrain key:");
        terrains.forEach((k, v) ->
                    System.out.println(k + " | " + v.description + " | " + v.blocking));

    }

    private Path getFilePath(String asset) throws IOException {

        ClassPathResource resource = new ClassPathResource(asset);
        File file = resource.getFile();
        String absolutePath = file.getAbsolutePath();
        return Paths.get(absolutePath);

    }

    public String[][] getMap() {
        return this.MAP;
    }

    public Map<String, tileInfo> getTerrains() {
        return this.terrains;
    }

    public int getWidth() {
        return MAP_WIDTH;
    }
    
    public int getHeight() {
        return MAP_HEIGHT;
    }

    public boolean isBlocking(int Y, int X) {

         return this.terrains.get(this.MAP[Y][X]).blocking;

    }

    public String getTileDescription(int Y, int X) {

        return this.terrains.get(this.MAP[Y][X]).description;
    
    }

}

//Austins approach:
 /*
        try {
            ClassPathResource resource = new ClassPathResource("AccountDetails.txt"); //loads the file from the classpath
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) { //reads the file using BufferedReader
                String line;
                while ((line = br.readLine()) != null) {    //reads the file line by line until the end of the file is reached
                    System.out.println("Account details:");
                    System.out.println(line);
                    String[] parts = line.split(":"); //splits the line into two parts using ":" as the delimiter, where parts[0] is the username and parts[1] is the password
                    if (parts.length == 2) {
                        map.put(parts[0].trim(), parts[1].trim());  //splits the line into username and password and stores in map
                        System.out.println("Acc: " +parts[0]);
                        System.out.println("PW: " + parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
         */
