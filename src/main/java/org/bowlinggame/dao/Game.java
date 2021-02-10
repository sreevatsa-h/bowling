package org.bowlinggame.dao;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.*;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gameId;

    @OneToMany(mappedBy="game", cascade = {CascadeType.ALL})
    private List<Player> players;

    @Column(name = "lanes")
    private Integer lanes;

    @Column(name = "n_players")
    private Integer nPlayers;

    @Column(name = "is_ongoing")
    private boolean isOngoing = false;

    @Column(name = "current_frame")
    private Integer currentFrame = 0;

    private static Integer maxPlayersInLane;

    public Integer getGameId() {
        return gameId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        this.nPlayers = players.size();
        this.calculateLanes();
        Integer currentLane = 1;
        Integer currentPlayerInLane = 0;
        for (Player player: players) {
            player.setGame(this);
            player.setLaneNumber(currentLane);
            currentPlayerInLane+=1;
            if (currentPlayerInLane % 3 == 0) {
                currentLane+=1;
                currentPlayerInLane = 0;
            }
        }
    }

    private void calculateLanes() {
        this.lanes = (int)Math.ceil((float) this.nPlayers / (float)this.maxPlayersInLane);
    }

    public Integer getLanes() {
        return lanes;
    }

    public void startGame() {
        this.isOngoing = true;
    }

    public Integer getnPlayers() {
        return nPlayers;
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public boolean checkMaxPlayers(Integer maxAllowedPlayers) {
        if (this.players.size() > maxAllowedPlayers) {
            return false;
        }

        return true;
    }

    public boolean checkDuplicates() {
        Set<String> uniqueCheck = new HashSet<String>();
        Integer prevSize = 0;

        for(Player player: this.players) {
            uniqueCheck.add(player.getName());
            if (uniqueCheck.size() == prevSize) {
                return false;
            }

            prevSize = uniqueCheck.size();
        }

        return true;
    }

    public static void setMaxPlayersInLane(Integer maxPlayersInLane) {
        Game.maxPlayersInLane = maxPlayersInLane;
    }

    public Integer getMaxPlayersInLane() {
        return Game.maxPlayersInLane;
    }

    public Map<Integer, Integer> generateRoll() {
        Map<Integer, Integer> rollMap = new HashMap<Integer, Integer>();
        for(Player player: this.players) {
            rollMap.put(player.getId(), player.rollBall());
        }

        return rollMap;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", players=" + players.toString() +
                ", lanes=" + lanes +
                '}';
    }
}
