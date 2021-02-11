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

    @PostConstruct
    private void init() {
        this.gameService.setMaxPlayersPerLane();
    }

    @PostMapping("/start")
    public Object startGame(@RequestBody Game newGame) {
        try {
            return this.gameService.startGame(newGame);
        } catch (Exception e) {
            return ResponseEntity.status(500);
        }
    }

    @GetMapping("/games")
    public Iterable<Game> getOngoingGames() {
        return this.gameService.getAllGames();
    }

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

    @GetMapping("/rules")
    public Iterator<Rules> getRules() {
        return this.gameService.getRules();
    }

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
