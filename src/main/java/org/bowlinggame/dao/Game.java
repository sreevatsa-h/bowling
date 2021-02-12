package org.bowlinggame.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer lanes;

    @Column(name = "n_players")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer nPlayers;

    @Column(name = "is_ongoing")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isOngoing = false;

    @Column(name = "current_sub_frame")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer currentSubFrame = 0;

    @Column(name = "extra_frame")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean extraFrame = false;

    private static Integer maxPlayersInLane;

    public Game() {

    }

    public Game(List<Player> players) {
        this.players = players;
    }

    public Integer getGameId() {
        return gameId;
    }

    public List<Player> getPlayers() {
        return players;
    }


    /**
     * @param players Gets list of players as input and sets their default values
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
        this.nPlayers = players.size();

        // Calculate maximum lanes that are to be in this game
        this.calculateLanes();

        // Allocate lane to each player
        Integer currentLane = 1;
        Integer currentPlayerInLane = 0;
        for (Player player: players) {

            // Set foreign key of player object (This reflects on DB)
            player.setGame(this);
            player.setLaneNumber(currentLane);
            currentPlayerInLane+=1;
            if (currentPlayerInLane % Game.maxPlayersInLane == 0) {
                currentLane+=1;
                currentPlayerInLane = 0;
            }
        }
    }


    /**
     * Given the number of players in the game and the maximum people allowed per lane, calculate number of lanes needed
     */
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

    public Integer getCurrentSubFrame() {
        return currentSubFrame;
    }


    /**
     * @param maxAllowedPlayers : Maximum number of players in a game
     * @return Returns true if number of players are under the allowed limit, false otherwise
     */
    public boolean checkMaxPlayers(Integer maxAllowedPlayers) {
        if (this.players.size() > maxAllowedPlayers) {
            return false;
        }

        return true;
    }


    /**
     * @return Returns true if no names are duplicates, false otherwise
     */
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

    // Runs when server is launched or rules are changed
    public static void setMaxPlayersInLane(Integer maxPlayersInLane) {
        Game.maxPlayersInLane = maxPlayersInLane;
    }

    public Integer getMaxPlayersInLane() {
        return Game.maxPlayersInLane;
    }


    /**
     * @return Returns a Map with <PlayerID, Roll>, null if any error
     */
    public Map<Integer, Integer> generateRoll() {

        // If the game is not ongoing return false
        if (this.isOngoing == false) {
            return null;
        }
        // Generate roll for each player and add it to the map
        Map<Integer, Integer> rollMap = new HashMap<Integer, Integer>();
        for(Player player: this.players) {
            rollMap.put(player.getId(), player.rollBall());
        }

        this.currentSubFrame+=1;

        if (this.currentSubFrame == 21) {
            this.isOngoing = false;
        }

        // Add the ongoing status with key -1 and value 1 for true and -1 for false
        rollMap.put(-1, this.isOngoing ? 1 : -1);

        return rollMap;
    }

    // Set if extra sub frame needs to be thrown
    public void throwExtraFrame() {
        this.extraFrame = true;
    }

    // End game logic checks if extra sub frame needs to be thrown (On the last frame), if not the game is ended
    // Otherwise, the game remains active for 1 more turn
    public void endGame() {
        if (this.extraFrame == true) {
            this.isOngoing = true;
        }

        else {
            this.isOngoing = false;
        }
    }


    /**
     * @return Returns true if no names are empty, false otherwise
     */
    public Boolean checkNullNames() {
        for(Player player: this.players) {
            if (player.getName() == null || player.getName() == "") {
                return false;
            }
        }

        return true;
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
