package org.bowlinggame.dao;

import javax.persistence.*;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gameId;

    @OneToMany(mappedBy="game", cascade = {CascadeType.ALL})
    private List<Player> players;

    private Integer lanes;

    private Integer nPlayers;

    private boolean isOngoing;

    public Integer getGameId() {
        return gameId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        this.nPlayers = players.size();
        for (Player player: players) {
            player.setGame(this);
        }
    }

    public Integer getLanes() {
        return lanes;
    }

    public void setLanes(Integer lanes) {
        this.lanes = lanes;
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

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", players=" + players.toString() +
                ", lanes=" + lanes +
                '}';
    }
}
