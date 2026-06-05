package com.tiles.server;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", ".", "W", "g", "t", "t", "gk","g", "t", "g", "g", "g",},
		{"S", "S", "S", "S", "S", "S", "g", "g", "g", "g", "W", "g", "t", ";", "t", "t", "t", "g", "g", "g",},
		{"S", "wa","w", "w", "w", "S", "g", "g", "g", "W", "W", "g", "t", "t", "t", "t", "t", "t", "g", "g",},
		{"S", "w", "w", "w", "w", "S", "g", "g", "W", "W", "g", "g", "t", "t", "t", "t", "t", "g", "g", "g",},
		{"S", "S", "S", "wd","S", "S", "g", "g", "W", "g", "g", "t", "t", "t", "g", "g", "g", "g", "g", "g",},
		{"g", "g", ",", "_", "g", "g", "g", "W", "W", "W", "g", "t", "g", "g", "g", "g", "g", "g", "g", "g",},
		{".", "g", "g", "_", "g", "g", "g", "W", "g", "Wb",".", "g", "g", "g", "g", "g", "g", "g", "g", "g",},
		{"_", "_", "_", "_", "g", "g", "W", "W", "g", "W", "W", "g", "g", "g", "g", "_", "_", "_", "_", "_",},
		{"_", "g", "t", "t", "g", "W", "W", "W", "g", "g", "W", "g", "g", "g", "g", "_", "g", "g", "g", "g",},
		{"_", "g", "g", "t", "g", "W", "W", "g", "g", "g", "W", "g", "g", "g", "B", "fD","B", "g", "g", "g",},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W", "W", "g", "g", "B", "f", "B", ".", "g", "g",},
		{"_", "g", "g", "g", "g", "g", "t", "t", "g", "W", "W", "g", "g", "B", "B", "f", "B", "B", "g", "g",},
		{"_", "g", "g", "g", "g", "g", "t", "g", "g", "W", ",", "g", "g", "B", "f", "f", "f", "B", "g", "g",},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "W", "W", "g", "g", "B", "f", "fh","f", "B", "g", "g",},
		{"_", "_", "g", "g", "g", "g", "g", "g", "W", ".", "W", "g", "g", "B", "f", "f", "f", "B", "g", "g",},
		{"g", "_", "_", "g", "g", "g", "g", "W", "W", "g", "W", "g", "g", "B", "B", "B", "B", "B", "g", "g",},
		{"g", "g", "_", "g", "g", "g", "g", "W", "gc","g", "W", "g", "g", "g", "g", "g", "g", "g", "g", "g",},
		{"g", "g", "_", "g", "g", "g", "W", "W", "g", "g", "W", "W", "g", "g", ":", "g", "g", "g", "g", "g",},
		{"g", "g", "g", "g", "g", "W", "W", "W", "g", "g", "W", "W", "g", "g", "g", "g", "t", "g", "g", "g",},
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W", "g", "g", "g", "g", "g", "g", "g", "g", "g",},
	};

	//test map windows include character icon for the test account

	private static final String[][] DefaultMapWindow = {
		{"g", "g", "g", "g", "g", "g", "g", "g", "g", ".", "W"},
		{"S", "S", "S", "S", "S", "S", "g", "g", "g", "g", "W"},
		{"S", "wa","w", "w", "w", "S", "g", "g", "g", "W", "W"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "W", "W", "g"},
		{"S", "S", "S", "wd","S", "S", "g", "g", "W", "g", "g"},
		{"g", "g", ",", "_", "g", "g", "g", "W", "W", "W", "g"},
		{".", "g", "g", "_", "g", "g", "g", "W", "g", "Wb","."},
		{"_", "_", "_", "_", "g", "g", "W", "W", "g", "W", "W"},
		{"_", "g", "t", "t", "g", "W", "W", "W", "g", "g", "W"},
		{"_", "g", "g", "t", "g", "W", "W", "g", "g", "g", "W"},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W"}
	};

	private static final String[][] MovedMapWindow = {
		{"S", "wa","w", "w", "w", "S", "g", "g", "g", "W", "W"},
		{"S", "w", "w", "w", "w", "S", "g", "g", "W", "W", "g"},
		{"S", "S", "S", "wd","S", "S", "g", "g", "W", "g", "g"},
		{"g", "g", ",", "_", "g", "g", "g", "W", "W", "W", "g"},
		{".", "g", "g", "_", "g", "g", "g", "W", "g", "Wb","."},
		{"_", "_", "_", "_", "g", "g", "W", "W", "g", "W", "W"},
		{"_", "g", "t", "t", "g", "W", "W", "W", "g", "g", "W"},
		{"_", "g", "g", "t", "g", "W", "W", "g", "g", "g", "W"},
		{"_", "g", "g", "g", "g", "g", "g", "g", "g", "g", "W"},
		{"_", "g", "g", "g", "g", "g", "t", "t", "g", "W", "W"},
		{"_", "g", "g", "g", "g", "g", "t", "g", "g", "W", ","},
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

		// remove sessions to not affect subsequent tests
		controller.getSessions().logOut(token);

	}

	// @Test
	// void testSetPositionX() {
	// 	controller.getSessions().addSession(testToken, "test");

	// 	PlayerData player = controller.getSessions().getPlayer(testToken);

	// 	controller.setPosition(3, 2, testToken);
	// 	assertEquals(player.getX(), 3);

	// 	controller.getSessions().logOut(testToken);
	// }

	@Test
	@Order(5)
	void infoReturnDefaultMapWindow() throws Exception {

		controller.getSessions().addSession(testToken, "test");
		PlayerData player = controller.getSessions().getPlayer(testToken);
		
		String[][] characterDefaultMapWindow = DefaultMapWindow;

		//adding character icon to default map
		characterDefaultMapWindow[5][5] += player.getIcon();
		
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
		
		assertArrayEquals(characterDefaultMapWindow, receivedMapWindow);

		controller.getSessions().logOut(testToken);

	}

	@Test
	@Order(6)
	void infoReturnMovedMapWindow() throws Exception {
		
		controller.getSessions().addSession(testToken, "test");
		
		PlayerData player = controller.getSessions().getPlayer(testToken);
		//adding player character to test map
		String[][] characterMovedMapWindow = MovedMapWindow;
		characterMovedMapWindow[5][5] += player.getIcon();
		
		//move to new position -- changed so info test is not dependent on move
		// mockMvc.perform(get("/move").param("session", testToken).param("dx", "0").param("dy", "1"));
		// mockMvc.perform(get("/move").param("session", testToken).param("dx", "0").param("dy", "1"));
		
		//simulated /move -- move to new position and erase old one
		controller.setPosition(5, 7, testToken);
		String tile = controller.getMap()[5][5].replace(Integer.toString(player.getIcon()), "");
		controller.getMap()[5][5] = tile;

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
		
		assertArrayEquals(characterMovedMapWindow, receivedMapWindow);

		controller.getSessions().logOut(testToken);

	}

	@Test
	@Order(7)
	void infoRequestInvalidCoordinate() throws Exception {

		controller.getSessions().addSession(testToken, "test");
		
		//Any param is valid on first call so that the position resets on log in
		//This allows the test to treat this not as a first call
		controller.getSessions().getPlayer(testToken).hasSpawned();
		
		controller.setPosition(3, 3, testToken);
    
    	mockMvc.perform(get("/info")
            .param("session", testToken)
            .param("x", "6")
            .param("y", "7"))
        .andExpect(status().isNoContent());

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(8)
	void infoRequestWithoutValidSession() throws Exception {
    
    	mockMvc.perform(get("/info")
		 	.param("session", "badtoken")
            .param("x", "5")
            .param("y", "5"))
        .andExpect(status().isUnauthorized());

	}

	@Test
	@Order(8)
	void moveRequestWithoutValidSession() throws Exception {
    
    	mockMvc.perform(get("/move")
			.param("session", "badtoken")
            .param("dx", "1")
            .param("dy", "0"))
        .andExpect(status().isUnauthorized());

	}

	@Test
	@Order(9)
	void moveRequestValidSession() throws Exception {
		controller.getSessions().addSession(testToken, "test");

		mockMvc.perform(get("/move")
			.param("session", testToken)
            .param("dx", "1")
            .param("dy", "0"))
        .andExpect(status().isOk());

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(10)
	void moveRequestLeft() throws Exception {
		controller.getSessions().addSession(testToken, "test");

		PlayerData player = controller.getSessions().getPlayer(testToken);

		mockMvc.perform(get("/move")
			.param("session", testToken)
            .param("dx", "-1")
            .param("dy", "0"))
        .andExpect(status().isOk());

		assertEquals(player.getX(), 4);
		assertEquals(player.getY(), 5);

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(10)
	void moveRequestRight() throws Exception {
		controller.getSessions().addSession(testToken, "test");

		PlayerData player = controller.getSessions().getPlayer(testToken);

		mockMvc.perform(get("/move")
			.param("session", testToken)
            .param("dx", "1")
            .param("dy", "0"))
        .andExpect(status().isOk());

		assertEquals(player.getX(), 6);
		assertEquals(player.getY(), 5);

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(10)
	void moveRequestUp() throws Exception {
		controller.getSessions().addSession(testToken, "test");

		PlayerData player = controller.getSessions().getPlayer(testToken);
		//move player so that the wall isn't blocking
		player.setPos(6, 5);

		mockMvc.perform(get("/move")
			.param("session", testToken)
            .param("dx", "0")
            .param("dy", "-1"))
        .andExpect(status().isOk());

		assertEquals(player.getX(), 6);
		assertEquals(player.getY(), 4);

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(10)
	void moveRequestDown() throws Exception {
		controller.getSessions().addSession(testToken, "test");

		PlayerData player = controller.getSessions().getPlayer(testToken);

		mockMvc.perform(get("/move")
			.param("session", testToken)
            .param("dx", "0")
            .param("dy", "1"))
        .andExpect(status().isOk());

		assertEquals(player.getX(), 5);
		assertEquals(player.getY(), 6);

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(10)
	void moveRequestBlocking() throws Exception {
		controller.getSessions().addSession(testToken, "test");

		mockMvc.perform(get("/move")
			.param("session", testToken)
            .param("dx", "0")
            .param("dy", "-1"))
        .andExpect(status().isNoContent());

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(10)
	void moveRequestOutofMap() throws Exception {
		controller.getSessions().addSession(testToken, "test");

		PlayerData player = controller.getSessions().getPlayer(testToken);
		player.setPos(5, 0);

		mockMvc.perform(get("/move")
			.param("session", testToken)
            .param("dx", "0")
            .param("dy", "-1"))
        .andExpect(status().isNoContent());

		controller.getSessions().logOut(testToken);		
	}

	@Test
	@Order(11)
	void useRequestWithoutValidSession() throws Exception {

		mockMvc.perform(get("/use")
			.param("session", "badtoken")
            .param("dx", "1")
            .param("dy", "0"))
        .andExpect(status().isUnauthorized());
	}

	@Test
	@Order(11)
	void useRequestWithNoKey() throws Exception {

		controller.getSessions().addSession(testToken, "test");
		PlayerData player = controller.getSessions().getPlayer(testToken);
		player.setPos(3, 5);

		mockMvc.perform(get("/use")
			.param("session", testToken)
            .param("dx", "0")
            .param("dy", "-1"))
        .andExpect(status().isNoContent());

		controller.getSessions().logOut(testToken);	
	}

	@Test
	@Order(12)
	void useRequestWithKey() throws Exception {

		controller.getSessions().addSession(testToken, "test");
		PlayerData player = controller.getSessions().getPlayer(testToken);
		player.setPos(3, 5);
		player.add(new Item("k", "key", "artifact", 0, 0));

		mockMvc.perform(get("/use")
			.param("session", testToken)
            .param("dx", "0")
            .param("dy", "-1"))
        .andExpect(status().isOk());

		player.resetInventory();
		controller.getSessions().logOut(testToken);	
		
	}

	@Test
	@Order(12)
	void useRequestInvalidLocation() throws Exception {

		controller.getSessions().addSession(testToken, "test");
		PlayerData player = controller.getSessions().getPlayer(testToken);
		player.setPos(5, 5);

		mockMvc.perform(get("/use")
			.param("session", testToken)
            .param("dx", "0")
            .param("dy", "-1"))
        .andExpect(status().isNoContent());

		controller.getSessions().logOut(testToken);	
	}

	@Test
	@Order(13)
	void badLogout() throws Exception {

		mockMvc.perform(get("/logout" )
				.queryParam("session", "badtoken"))
			.andExpect(status().isUnauthorized());
		
	}

	@Test
	@Order(13)
	void goodLogout() throws Exception {

		controller.getSessions().addSession(testToken, "test");

		mockMvc.perform(get("/logout" )
				.queryParam("session", testToken))
			.andExpect(status().isOk());

		assertFalse(controller.sessionValid(testToken));
	
	}

}
