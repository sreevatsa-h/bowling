package org.bowlinggame.controller;

/* Main class to handle APIs */

import org.bowlinggame.dao.Game;
import org.bowlinggame.dao.Player;
import org.bowlinggame.dao.PlayerRepo;
import org.bowlinggame.dao.Rules;
import org.bowlinggame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

@RestController
@CrossOrigin
public class GameController {

    @Autowired
    private GameService gameService;


    /**
     * Init function to initialize maximum players per lane rule after app starts
     */
    @PostConstruct
    private void init() {
        this.gameService.setMaxPlayersPerLane();
    }


    /**
     * @param newGame : The new game object that needs to be created
     * @return : Game object with all player details
     */
    @PostMapping("/start")
    public Object startGame(@RequestBody Game newGame) {
        try {
            return this.gameService.startGame(newGame);
        } catch (Exception e) {
            return ResponseEntity.status(500);
        }
    }


    /**
     * @return Returns all the games as an array of games
     */
    @GetMapping("/games")
    public Iterable<Game> getAllGames() {
        return this.gameService.getAllGames();
    }


    /**
     * @param gameId : ID of the game for the ball to be bowled
     * @return Returns rolls of all players in that game, or an error, the rolls will be of the format { "id" : <ROLL_NUMBER> }
     *         The ROLL_NUMBER will be -1 if they haven't rolled (This will happen on the final frame when only players
     *         with spare/strike can roll)
     */
    @GetMapping("/roll/{gameId}")
    public Object rollBall(@PathVariable Integer gameId) {
        try {
            return this.gameService.rollBall(gameId);
        } catch (Exception e) {
            System.out.println("Exception in rolling ball for game ID " + gameId);
            System.out.println(e);
            return ResponseEntity.status(500);
        }
    }


    /**
     * @param playerId : The player ID whose details is needed
     * @return Returns the player object or an error if player ID is not found
     */
    @GetMapping("/players/{playerId}")
    public Object getPlayer(@PathVariable Integer playerId) {
        try {
            return this.gameService.getPlayer(playerId);
        } catch (Exception e) {
            System.out.println("Exception in getting player with ID " + playerId);
            System.out.println(e);
            return ResponseEntity.status(500);
        }
    }


    /**
     * @return Returns all the rules in the DB (By default, 3 rules, can be found in data.sql file)
     */
    @GetMapping("/rules")
    public Iterator<Rules> getRules() {
        return this.gameService.getRules();
    }


    /**
     * @param updatedRules : List of rules to be updated in the format [{"id" : <id> , "value" : <value>}]
     * @return Returns updated rules or error
     */
    @PutMapping("/rules")
    public Object updateRules(@RequestBody List<Rules> updatedRules) {
        try {
            return this.gameService.updateRules(updatedRules);
        } catch (Exception e) {
            System.out.println("Exception in updating rules, the list is: ");
            System.out.println(updatedRules);
            System.out.println(e);
            return ResponseEntity.status(500);
        }
    }


    /**
     * @param gameId : Game ID to delete
     * @return Returns {"success": true, "gameId": <gameId>} if the game is deleted successfully, or error if it's not
     */
    @DeleteMapping("/games/{gameId}")
    public Object deleteGame(@PathVariable Integer gameId) {
        try {
            return this.gameService.deleteGame(gameId);
        } catch (Exception e) {
            System.out.println("Exception in deleting game with ID " + gameId);
            System.out.println(e);
            return ResponseEntity.status(500);
        }
    }
}
