package org.bowlinggame.controller;

/* Main class to handle APIs */

import org.bowlinggame.dao.Game;
import org.bowlinggame.dao.Player;
import org.bowlinggame.dao.PlayerRepo;
import org.bowlinggame.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/start")
    public Integer startGame(@RequestBody Game newGame) {
        return this.gameService.startGame(newGame);
    }

    @PostMapping("/test")
    public Player addPlayer(@RequestBody Player newPlayer) {
        return this.gameService.addNewPlayer(newPlayer);
    }

    @GetMapping("/games")
    public Iterable<Game> getOngoingGames() {
        return this.gameService.getAllGames();
    }
}
