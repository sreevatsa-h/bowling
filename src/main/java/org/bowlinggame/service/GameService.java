package org.bowlinggame.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.bowlinggame.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

    private Map<Integer, Game> games;

    @Autowired
    PlayerRepo playerRepo;

    @Autowired
    GameRepo gameRepo;

    @Autowired
    RulesRepo rulesRepo;

    public Object startGame(Game newGame) throws JsonProcessingException {

        Integer maxGames = rulesRepo.findByRuleName("max_games").getValue();
        if (gameRepo.count() == maxGames) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Number of games exceeding allowed number of games (" + maxGames + ")");
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        if (newGame.checkDuplicates() == false) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Names cannot be duplicate");
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        Integer maxAllowedPlayers = rulesRepo.findByRuleName("max_players").getValue();

        if (newGame.checkMaxPlayers(maxAllowedPlayers) == false) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Max players cannot be greater than " + maxAllowedPlayers);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        newGame.startGame();
        newGame = gameRepo.save(newGame);
        return newGame;

    }

    public Player addNewPlayer(Player newPlayer) {
        newPlayer = playerRepo.save(newPlayer);
        return newPlayer;
    }

    public Iterable<Game> getAllGames() {
        return gameRepo.findAll();
    }

    public void setMaxPlayersPerLane() {
        Integer maxPlayersInLane = rulesRepo.findByRuleName("max_players_lane").getValue();
        Game.setMaxPlayersInLane(maxPlayersInLane);
    }

    public Object rollBall(Integer gameId) throws JsonProcessingException {
        Game gameObject = gameRepo.findById(gameId).get();
        if (gameObject == null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Game not found with ID " + gameId);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        Map<Integer, Integer> generatedRolls = gameObject.generateRoll();
        gameRepo.save(gameObject);

        return generatedRolls;
    }
}
