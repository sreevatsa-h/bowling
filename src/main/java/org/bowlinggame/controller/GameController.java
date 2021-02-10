package org.bowlinggame.controller;

/* Main class to handle APIs */

import org.bowlinggame.dao.Game;
import org.bowlinggame.dao.Player;
import org.bowlinggame.dao.PlayerRepo;
import org.bowlinggame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@CrossOrigin
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/")
    public String test() {
        return "Hello";
    }

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

    @PostMapping("/test")
    public Player addPlayer(@RequestBody Player newPlayer) {
        return this.gameService.addNewPlayer(newPlayer);
    }

    @GetMapping("/games")
    public Iterable<Game> getOngoingGames() {
        return this.gameService.getAllGames();
    }

    @GetMapping("/roll")
    public Object rollBall(@RequestParam Integer gameId) {
        try {
            return this.gameService.rollBall(gameId);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(500);
        }
    }
}
