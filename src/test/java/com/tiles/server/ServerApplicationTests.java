package com.tiles.server;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
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

	private static final LoginData valid = new LoginData("john", "c9765b38a8ded4d7f4286cbab7c104e95208a911b189beaf3c88182376e6bf32");
	private static final LoginData invalidPassword = new LoginData("john", "d9765b38a8ded4d7f4286cbab7c104e95208a911b189beaf3c88182376e6bf33");
	private static final LoginData invalidUsername = new LoginData("jhn", "c9765b38a8ded4d7f4286cbab7c104e95208a911b189beaf3c88182376e6bf32");
	private static final LoginData blankUsername = new LoginData("", "c9765b38a8ded4d7f4286cbab7c104e95208a911b189beaf3c88182376e6bf32");
	private static final LoginData blankPassword = new LoginData("john", "");
	private static final LoginData spaceUsername = new LoginData(" ", "c9765b38a8ded4d7f4286cbab7c104e95208a911b189beaf3c88182376e6bf32");
	private static final LoginData spacePassword = new LoginData("john", " ");

	private static String testToken;

	@Autowired
    private MockMvc mockMvc;

	@Autowired
	private MyController controller;

	private String[][] returnReceivedMapWindow (MvcResult result) throws Exception {

		String jsonString = result.getResponse().getContentAsString();
	
		JsonNode root = objectMapper.readTree(jsonString);
    	JsonNode infoNode = root.get("info");

		String[][] newGrid = objectMapper.treeToValue(infoNode, String[][].class);
		return newGrid;

	}

	private String returnReceivedToken (MvcResult result) throws Exception {

		String jsonString = result.getResponse().getContentAsString();
	
		JsonNode root = objectMapper.readTree(jsonString);
    	JsonNode sessionNode = root.get("session");

		String newToken = objectMapper.treeToValue(sessionNode, String.class);
		return newToken;

	}

	//Helper method for tests to login to endpoints, use only after valid login has been tested first
	private String testLogin (LoginData loginData) throws Exception {

		MvcResult result = mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginData)))
			.andReturn();
		
		return returnReceivedToken(result);
		
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
	void unauthorizedLogins() throws Exception {

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUsername)))
        	.andExpect(status().isUnauthorized());

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidPassword)))
        	.andExpect(status().isUnauthorized());

	}

	@Test
	@Order(3)
	void badLoginRequests() throws Exception {

		//Works by default exception handler
		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(""))
        	.andExpect(status().isBadRequest());

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\""+valid.getName()+"\"}"))
        	.andExpect(status().isBadRequest());

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content("{\"encpswrd\":\""+valid.getEncpswrd()+"\"}"))
        	.andExpect(status().isBadRequest());

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankUsername)))
        	.andExpect(status().isBadRequest());

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(blankPassword)))
        	.andExpect(status().isBadRequest());

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(spaceUsername)))
        	.andExpect(status().isBadRequest());

		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(spacePassword)))
        	.andExpect(status().isBadRequest());
	}

	@Test
	@Order(4)
	void validLogin() throws Exception {

		MvcResult result = mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(valid)))
        	.andExpect(status().isOk())
			.andReturn();

		System.out.println(result.getResponse().getContentAsString());
		
		String token = returnReceivedToken(result);
		
		assertTrue(controller.sessionValid(token));

		testToken = token;

	}

	@Test
	@Order(5)
	void infoReturnDefaultMapWindow() throws Exception {
		
    	MvcResult result = mockMvc.perform(get("/info")
            .param("session", testToken)
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
	@Order(6)
	void infoReturnMovedMapWindow() throws Exception {
		
		controller.setPosition(5, 7);
    
    	MvcResult result = mockMvc.perform(get("/info")
            .param("session", testToken)
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
	@Order(7)
	void infoRequestInvalidCoordinate() throws Exception {
		
		controller.setPosition(3, 3);
    
    	mockMvc.perform(get("/info")
            .param("session", testToken)
            .param("x", "6")
            .param("y", "7"))
        .andExpect(status().isNoContent());

	}

	@Test
	@Order(8)
	void infoRequestWithoutValidSession() throws Exception {
    
    	mockMvc.perform(get("/info")
            .param("x", "5")
            .param("y", "5"))
        .andExpect(status().isUnauthorized());

	}

	@Test
	@Order(8)
	void moveRequestWithoutValidSession() throws Exception {
    
    	mockMvc.perform(get("/move")
            .param("dx", "1")
            .param("dy", "0"))
        .andExpect(status().isUnauthorized());

	}

}
