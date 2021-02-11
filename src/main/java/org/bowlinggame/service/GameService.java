package org.bowlinggame.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.bowlinggame.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.*;

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

        if (newGame.checkNullNames() == false) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Names cannot be null value");
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

    public Iterable<Game> getAllGames() {
        return gameRepo.findAll();
    }

    public void setMaxPlayersPerLane() {
        Integer maxPlayersInLane = rulesRepo.findByRuleName("max_players_lane").getValue();
        Game.setMaxPlayersInLane(maxPlayersInLane);
    }

    public Object rollBall(Integer gameId) throws JsonProcessingException {

        Optional<Game> gameObject = gameRepo.findById(gameId);
        if (gameObject.isEmpty()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Game not found with ID " + gameId);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        Game actualGame = gameObject.get();

        Map<Integer, Integer> generatedRolls = actualGame.generateRoll();

        if (generatedRolls == null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "The game with ID " + gameId + " has already ended");
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }
        gameRepo.save(actualGame);

        return generatedRolls;
    }

    public Object getPlayer(Integer playerId) throws JsonProcessingException {

        if (playerId == null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Player ID cannot be null");
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }
        Optional<Player> playerObject = playerRepo.findById(playerId);
        if (playerObject.isEmpty()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Player not found with ID " + playerId);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        return playerObject.get();
    }

    public Iterator<Rules> getRules() {
        return rulesRepo.findAll().iterator();
    }

    public Object updateRules(List<Rules> updatedRules) throws JsonProcessingException {
        for(Rules rule : updatedRules) {
            Optional<Rules> ruleExistCheck = rulesRepo.findById(rule.getId());
            if (ruleExistCheck.isEmpty()) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("success", false);
                payload.put("reason", "Rule not found with ID " + rule.getId());
                String json = new ObjectMapper().writeValueAsString(payload);
                return json;
            }

            Rules oldRule = ruleExistCheck.get();

            if (oldRule.getRuleName().equals("max_players_lane")) {
                if (rule.getValue() <= 0) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be less than 1 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }

                if (rule.getValue() > 10) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be more than 10 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }
            }

            else {

                if (rule.getValue() <= 10) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be less than 10 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }

                if (rule.getValue() >= 60) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be more than 70 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }

            }

            if (rule.getValue() != null) {
                oldRule.setValue(rule.getValue());
            }

            rulesRepo.save(oldRule);
        }

        this.setMaxPlayersPerLane();

        return this.getRules();
    }

    public Object deleteGame(Integer gameId) throws JsonProcessingException {
        Optional<Game> checkForGame = gameRepo.findById(gameId);
        if (checkForGame.isEmpty()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Game not found with ID " + gameId);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        gameRepo.delete(checkForGame.get());

        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);
        payload.put("gameId", gameId);
        String json = new ObjectMapper().writeValueAsString(payload);
        return json;
    }
}
