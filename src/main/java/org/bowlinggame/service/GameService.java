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

    /**
     * @param newGame : Game object that needs to be created
     * @return Returns game object created, or error
     * @throws JsonProcessingException This is thrown in case json parsing for error messages happens (This should never occur)
     */
    public Object startGame(Game newGame) throws JsonProcessingException {

        // Check if max games limit has reached
        Integer maxGames = rulesRepo.findByRuleName("max_games").getValue();
        if (gameRepo.count() == maxGames) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Number of games exceeding allowed number of games (" + maxGames + ")");
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        // Check if any name is null value or blank string ("")
        if (newGame.checkNullNames() == false) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Names cannot be null value");
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        // Check if any name is duplicate
        if (newGame.checkDuplicates() == false) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Names cannot be duplicate");
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        Integer maxAllowedPlayers = rulesRepo.findByRuleName("max_players").getValue();

        // Check if game exceeds maximum allowed players
        if (newGame.checkMaxPlayers(maxAllowedPlayers) == false) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Max players cannot be greater than " + maxAllowedPlayers);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        // If all conditions match, start game
        newGame.startGame();
        newGame = gameRepo.save(newGame);
        return newGame;

    }


    /**
     * @return Returns all games in database
     */
    public Iterable<Game> getAllGames() {
        return gameRepo.findAll();
    }


    /**
     * This will be run initially on Post startup of server to set max players per lane rule
     */
    public void setMaxPlayersPerLane() {
        Integer maxPlayersInLane = rulesRepo.findByRuleName("max_players_lane").getValue();
        Game.setMaxPlayersInLane(maxPlayersInLane);
    }


    /**
     * @param gameId : Game id for the ball to be rolled in
     * @return Returns the rolls of all players in that game or error
     * @throws JsonProcessingException This is thrown in case json parsing for error messages happens (This should never occur)
     */
    public Object rollBall(Integer gameId) throws JsonProcessingException {

        Optional<Game> gameObject = gameRepo.findById(gameId);

        // Game not found in database
        if (gameObject.isEmpty()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Game not found with ID " + gameId);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        Game actualGame = gameObject.get();

        Map<Integer, Integer> generatedRolls = actualGame.generateRoll();

        // Game has already ended
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


    /**
     * @param playerId : Player ID whose details have to be fetched
     * @return Returns player object on success or error object on failure
     * @throws JsonProcessingException This is thrown in case json parsing for error messages happens (This should never occur)
     */
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


    /**
     * @return Gets all the rules in DB
     */
    public Iterator<Rules> getRules() {
        return rulesRepo.findAll().iterator();
    }


    /**
     * @param updatedRules : Rules to be upadted
     * @return Returns all the rules
     * @throws JsonProcessingException This is thrown in case json parsing for error messages happens (This should never occur)
     */
    public Object updateRules(List<Rules> updatedRules) throws JsonProcessingException {
        for(Rules rule : updatedRules) {
            Optional<Rules> ruleExistCheck = rulesRepo.findById(rule.getId());

            // If rule ID does not exist
            if (ruleExistCheck.isEmpty()) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("success", false);
                payload.put("reason", "Rule not found with ID " + rule.getId());
                String json = new ObjectMapper().writeValueAsString(payload);
                return json;
            }

            Rules oldRule = ruleExistCheck.get();

            // If the rule is max players per lane (The lower and upper limit is different only for this rule)
            if (oldRule.getRuleName().equals("max_players_lane")) {

                // If max players per lane is given anything less than 1
                if (rule.getValue() <= 0) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be less than 1 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }

                // If max players per lane is greater than 10
                if (rule.getValue() > 10) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be more than 10 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }
            }

            // For every other rule
            else {

                // If value is given less than 10
                if (rule.getValue() <= 10) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be less than 10 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }

                // If value is more than 60
                if (rule.getValue() >= 60) {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("success", false);
                    payload.put("reason", "Value cannot be more than 60 for rule " + oldRule.getRuleName());
                    String json = new ObjectMapper().writeValueAsString(payload);
                    return json;
                }

            }

            // Check if value supplied is null, if it's null, then skip for that rule
            if (rule.getValue() != null) {
                oldRule.setValue(rule.getValue());
            }

            rulesRepo.save(oldRule);
        }

        // Update max players per lane
        this.setMaxPlayersPerLane();

        // Return all rules
        return this.getRules();
    }


    /**
     * @param gameId : Game ID to be deleted
     * @return Returns success object on successful deletion, or failure
     * @throws JsonProcessingException This is thrown in case json parsing for error messages happens (This should never occur)
     */
    public Object deleteGame(Integer gameId) throws JsonProcessingException {
        Optional<Game> checkForGame = gameRepo.findById(gameId);

        // Check if game ID exists in database
        if (checkForGame.isEmpty()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("success", false);
            payload.put("reason", "Game not found with ID " + gameId);
            String json = new ObjectMapper().writeValueAsString(payload);
            return json;
        }

        // If it is, then delete it
        gameRepo.delete(checkForGame.get());

        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);
        payload.put("gameId", gameId);
        String json = new ObjectMapper().writeValueAsString(payload);
        return json;
    }
}
