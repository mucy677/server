package com.tiles.server;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MyController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ServerApplicationTests {

	private final ObjectMapper objectMapper = new ObjectMapper();

	// Hardcoded map data (same as frontend)
	private static final String[][] MAP = {
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", ".", "W", "g", "t", "t", "g", "g", "t", "g", "g", "g"},
		{"S", "S", "S", "S", "S", "S", "g", "g", "g", "g", "W", "g", "t", ";", "t", "t", "t", "g", "g", "g"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "g", "W", "W", "g", "t", "t", "t", "t", "t", "t", "g", "g"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "W", "W", "g", "g", "t", "t", "t", "t", "t", "g", "g", "g"},
		{"S", "S", "S", "D", "S", "S", "g", "g", "W", "g", "g", "t", "t", "t", "g", "g", "g", "g", "g", "g"},
		{"g", "g", ",", "_", "g", "g", "g", "W", "W", "W", "g", "t", "g", "g", "g", "g", "g", "g", "g", "g"},
		{".", "g", "g", "_", "g", "g", "g", "W", "g", "W", ".", "g", "g", "g", "g", "g", "g", "g", "g", "g"},
		{"_", "_", "_", "_", "g", "g", "W", "W", "g", "W", "W", "g", "g", "g", "g", "_", "_", "_", "_", "_"},
		{"_", "g", "t", "t", "g", "W", "W", "W", "g", "g", "W", "g", "g", "g", "g", "_", "g", "g", "g", "g"},
		{"_", "g", "g", "t", "g", "W", "W", "g", "g", "g", "W", "g", "g", "g", "B", "D", "B", "g", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W", "W", "g", "g", "B", "f", "B", ".", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "t", "t", "g", "W", "W", "g", "g", "B", "B", "f", "B", "B", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "t", "g", "g", "W", ",", "g", "g", "B", "f", "f", "f", "B", "g", "g"},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "W", "W", "g", "g", "B", "f", "f", "f", "B", "g", "g"},
		{"_", "_", "g", "g", "g", "g", "g", "g", "W", ".", "W", "g", "g", "B", "f", "f", "f", "B", "g", "g"},
		{"g", "_", "_", "g", "g", "g", "g", "W", "W", "g", "W", "g", "g", "B", "B", "B", "B", "B", "g", "g"},
		{"g", "g", "_", "g", "g", "g", "g", "W", "g", "g", "W", "g", "g", "g", "g", "g", "g", "g", "g", "g"},
		{"g", "g", "_", "g", "g", "g", "W", "W", "g", "g", "W", "W", "g", "g", ":", "g", "g", "g", "g", "g"},
		{"g", "g", "g", "g", "g", "W", "W", "W", "g", "g", "W", "W", "g", "g", "g", "g", "t", "g", "g", "g"},
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W", "g", "g", "g", "g", "g", "g", "g", "g", "g"}
	};

	private static final String[][] DefaultMapWindow = {
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", ".", "W"},
		{"S", "S", "S", "S", "S", "S", "g", "g", "g", "g", "W"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "g", "W", "W"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "W", "W", "g"},
		{"S", "S", "S", "D", "S", "S", "g", "g", "W", "g", "g"},
		{"g", "g", ",", "_", "g", "g", "g", "W", "W", "W", "g"},
		{".", "g", "g", "_", "g", "g", "g", "W", "g", "W", "."},
		{"_", "_", "_", "_", "g", "g", "W", "W", "g", "W", "W"},
		{"_", "g", "t", "t", "g", "W", "W", "W", "g", "g", "W"},
		{"_", "g", "g", "t", "g", "W", "W", "g", "g", "g", "W"},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W"}
	};

	private static final String[][] MovedMapWindow = {
		{"S", "w", "w", "w", "w", "S", "g", "g", "g", "W", "W" },
		{"S", "w", "w", "w", "w", "S", "g", "g", "W", "W", "g" },
		{"S", "S", "S", "D", "S", "S", "g", "g", "W", "g", "g" },
		{"g", "g", ",", "_", "g", "g", "g", "W", "W", "W", "g" },
		{".", "g", "g", "_", "g", "g", "g", "W", "g", "W", "." },
		{"_", "_", "_", "_", "g", "g", "W", "W", "g", "W", "W" },
		{"_", "g", "t", "t", "g", "W", "W", "W", "g", "g", "W" },
		{"_", "g", "g", "t", "g", "W", "W", "g", "g", "g", "W" },
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W" },
		{"_", "g", "g", "g", "g", "g", "t", "t", "g", "W", "W" },
		{"_", "g", "g", "g", "g", "g", "t", "g", "g", "W", "," },
	};

	@Autowired
    private MockMvc mockMvc;

	@Autowired
	private MyController controller;

	private String[][] returnReceivedMapWindow (MvcResult response) throws Exception {

		String jsonString = response.getResponse().getContentAsString();
	
		JsonNode root = objectMapper.readTree(jsonString);
    	JsonNode infoNode = root.get("info");

		String[][] newGrid = objectMapper.treeToValue(infoNode, String[][].class);
		return newGrid;

	}

	@Test
	@Order(1)
	void contextLoads() {
	}

	@Test
	@Order(2)
	void testMapLoad() throws Exception {
		
		assertArrayEquals(MAP, controller.getMap());
		
	}

	@Test
	@Order(3)
	void infoReturnDefaultMapWindow() throws Exception {
    
    	MvcResult result = mockMvc.perform(get("/info")
            .param("x", "5")
            .param("y", "5"))
        .andExpect(status().isOk())
		.andExpect(jsonPath("$.x").value(5))
        .andExpect(jsonPath("$.y").value(5))
		.andExpect(jsonPath("$.top").value(0))
		.andExpect(jsonPath("$.left").value(0))
		.andExpect(jsonPath("$.bottom").value(10))
		.andExpect(jsonPath("$.right").value(10))
		.andReturn();
		
		String[][] receivedMapWindow = returnReceivedMapWindow(result);
		
		System.out.println("Received default map window:");
		Arrays.stream(receivedMapWindow)
      		.map(Arrays::toString)
      		.forEach(System.out::println);
		
		assertArrayEquals(DefaultMapWindow, receivedMapWindow);

	}

	@Test
	@Order(4)
	void infoReturnMovedMapWindow() throws Exception {

		controller.setPosition(5, 7);
    
    	MvcResult result = mockMvc.perform(get("/info")
            .param("x", "5")
            .param("y", "7"))
        .andExpect(status().isOk())
		.andExpect(jsonPath("$.x").value(5))
        .andExpect(jsonPath("$.y").value(7))
		.andExpect(jsonPath("$.top").value(2))
		.andExpect(jsonPath("$.left").value(0))
		.andExpect(jsonPath("$.bottom").value(12))
		.andExpect(jsonPath("$.right").value(10))
		.andReturn();
		
		String[][] receivedMapWindow = returnReceivedMapWindow(result);
		
		System.out.println("Received moved map window:");
		Arrays.stream(receivedMapWindow)
      		.map(Arrays::toString)
      		.forEach(System.out::println);
		
		assertArrayEquals(MovedMapWindow, receivedMapWindow);

	}

	@Test
	@Order(4)
	void infoRequestInvalidCoordinate() throws Exception {

		controller.setPosition(3, 3);
    
    	mockMvc.perform(get("/info")
            .param("x", "6")
            .param("y", "7"))
        .andExpect(status().isNoContent());

	}

}
