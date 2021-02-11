package org.bowlinggame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bowlinggame.dao.Game;
import org.bowlinggame.dao.Player;
import org.bowlinggame.dao.Rules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("This test case checks if names are duplicate in a game")
    void namesShouldNotBeDuplicate() throws Exception {
        List<Player> players = new ArrayList<Player>();
        Player player1 = new Player("Test",30.0,-1,20,20,20);
        Player player2 = new Player("Test",30.0,-1,20,20,20);
        players.add(player1);
        players.add(player2);
        Game game = new Game(players);
        String json = new ObjectMapper().writeValueAsString(game);

        ResultActions result = this.mockMvc.perform(post("/start").contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.reason", is("Names cannot be duplicate")));
    }

    @Test
    @DisplayName("This test case is to check if the user provided score, number of strikes, number of spares etc.. is taken by the system")
    void userProvidedScoreShouldNotBeTaken() throws Exception {
        List<Player> players = new ArrayList<Player>();
        Player player = new Player("Test",30.0,-1,20,20,20);
        players.add(player);
        Game game = new Game(players);
        String json = new ObjectMapper().writeValueAsString(game);

        ResultActions result = this.mockMvc.perform(post("/start").contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.players[0].score", is(0.0)));
        result.andExpect(jsonPath("$.players[0].laneNumber", not(0)));
        result.andExpect(jsonPath("$.players[0].nStrikes", is(0)));
        result.andExpect(jsonPath("$.players[0].nMissedStrikes", is(0)));
        result.andExpect(jsonPath("$.players[0].nSpares", is(0)));
    }

    @Test
    @DisplayName("Name of a player cannot be null")
    void playerNameCannotBeNull() throws Exception {
        List<Player> players = new ArrayList<Player>();
        Player player = new Player(null,30.0,-1,20,20,20);
        players.add(player);
        Game game = new Game(players);
        String json = new ObjectMapper().writeValueAsString(game);

        ResultActions result = this.mockMvc.perform(post("/start").contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.reason", is("Names cannot be null value")));
    }

    @Test
    @DisplayName("Test to check if other aspects of a rule like it's name are affected if they are not supplied by the request")
    void checkIfRuleValuesChange() throws Exception {
        List<Rules> rule = new ArrayList<Rules>();
        rule.add(new Rules(1,null,null,20));
        String json = new ObjectMapper().writeValueAsString(rule);

        ResultActions result = this.mockMvc.perform(put("/rules").contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$[0].ruleName", notNullValue()));
        result.andExpect(jsonPath("$[1].ruleName", notNullValue()));
        result.andExpect(jsonPath("$[2].ruleName", notNullValue()));
    }

    @Test
    @DisplayName("Rule values cannot be changed to negative values")
    void rulesCannotBeUpdatedToNegativeValues() throws Exception {
        List<Rules> rule = new ArrayList<Rules>();
        rule.add(new Rules(1,null,null,-2));
        String json = new ObjectMapper().writeValueAsString(rule);

        ResultActions result = this.mockMvc.perform(put("/rules").contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Rule values cannot be changed to absurd values")
    void rulesCannotBeUpdatedToAbsurdValues() throws Exception {
        List<Rules> rule = new ArrayList<Rules>();
        rule.add(new Rules(2,null,null,100000));
        String json = new ObjectMapper().writeValueAsString(rule);

        ResultActions result = this.mockMvc.perform(put("/rules").contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Rolling a ball for a game that does not exist")
    void ballRollForGameThatDoesNotExist() throws Exception {
        ResultActions result = this.mockMvc.perform(get("/roll/-1")).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Get player details for a player that does not exist")
    void getPlayerDetailsForANonExistentPlayer() throws Exception {
        ResultActions result = this.mockMvc.perform(get("/players/-1")).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Delete a game that does not exist")
    void deleteAGameThatDoesNotExist() throws Exception {
        ResultActions result = this.mockMvc.perform(delete("/games/-1")).andDo(print()).andExpect(status().isOk());

        result.andExpect(jsonPath("$.success", is(false)));
    }
}