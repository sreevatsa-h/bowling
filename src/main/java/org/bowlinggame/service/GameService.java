package org.bowlinggame.service;

import org.bowlinggame.dao.Game;
import org.bowlinggame.dao.GameRepo;
import org.bowlinggame.dao.Player;
import org.bowlinggame.dao.PlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GameService {

    private Map<Integer, Game> games;

    @Autowired
    PlayerRepo playerRepo;

    @Autowired
    GameRepo gameRepo;

    public Integer startGame(Game newGame) {
        newGame.startGame();
        newGame = gameRepo.save(newGame);
        return newGame.getGameId();
    }

    public Player addNewPlayer(Player newPlayer) {
        newPlayer = playerRepo.save(newPlayer);
        return newPlayer;
    }

    public Iterable<Game> getAllGames() {
        return gameRepo.findAll();
    }
}
