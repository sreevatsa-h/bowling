package org.bowlinggame.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "score")
    private Double score = 0.0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "lane_number")
    private Integer laneNumber;

    @ManyToOne
    @JoinColumn(name="game_id")
    private Game game;

    @Column(name = "rolls")
    private String rolls;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "n_strikes")
    private Integer nStrikes = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "n_missed_strikes")
    private Integer nMissedStrikes = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "n_spares")
    private Integer nSpares = 0;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "eligible_for_extra_frame")
    private Boolean eligibleForExtraFrame = false;

    public Player(String name, Double score, Integer laneNumber, Integer nStrikes, Integer nMissedStrikes, Integer nSpares) {
        this.name = name;
        this.score = score;
        this.laneNumber = laneNumber;
        this.nStrikes = nStrikes;
        this.nMissedStrikes = nMissedStrikes;
        this.nSpares = nSpares;
    }

    public Player() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getScore() {
        return score;
    }

    public void setScore() {
        this.score = 0.0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getLaneNumber() {
        return laneNumber;
    }

    public void setLaneNumber(Integer laneNumber) {
        this.laneNumber = laneNumber;
    }

    public Integer getnStrikes() {
        return nStrikes;
    }

    public Integer getnMissedStrikes() {
        return nMissedStrikes;
    }

    public Integer getnSpares() {
        return nSpares;
    }


    /**
     * @return Convert rolls which are stored as string, into a list of integers
     *  The rolls are stored like the following example "5;4;2;1;3;"
     */
    public List<Integer> getRollsAsList() {
        if (this.rolls == null) {
            return new ArrayList<Integer>();
        }
        String[] stringArray = this.rolls.split(";");
        List<Integer> integerList = new ArrayList<>();
        for(String roll : stringArray) {
            integerList.add(Integer.parseInt(roll));
        }

        return integerList;
    }


    /**
     * @param integerList Integer list that needs to be converted to String to save to db
     * @return Returns the final string
     */
    public String setRollString(List<Integer> integerList) {
        StringBuilder newRollString = new StringBuilder("");
        for(Integer roll : integerList) {
            newRollString.append(String.valueOf(roll));
            newRollString.append(";");
        }

        this.rolls = newRollString.toString();
        return this.rolls;
    }


    /**
     * @return Returns the roll of the current player
     */
    public Integer rollBall() {

        List<Integer> rolls = this.getRollsAsList();
        Integer remainingPins = 10;

        // Previous was a strike, reset the pins
        if (rolls.size() > 0 && rolls.size() % 2 != 0 && rolls.get(rolls.size() - 1) == 10) {
            remainingPins = 10;
        }

        // Else carry over the remaining pins
        else if (rolls.size() > 0 && rolls.size() % 2 != 0) {
            remainingPins = 10 - rolls.get(rolls.size() - 1);
        }

        // If player is eligible for the last sub frame (Only possible in the last frame)
        if (rolls.size() == 20 && this.eligibleForExtraFrame == false) {
            rolls.add(-1);
            this.setRollString(rolls);
            return -1;
        }

        // Generate random roll
        Random roller = new Random();
        Integer randomRoll = roller.nextInt(remainingPins + 1);
        rolls.add(randomRoll);
        this.setRollString(rolls);

        // Count missed strikes
        if (rolls.size() > 1 && rolls.size() % 2 == 0 && rolls.get(rolls.size() - 1) + rolls.get(rolls.size() - 2) == 9) {
            this.nMissedStrikes+=1;
        }

        // If random roll is a strike
        if (randomRoll == 10) {
            this.score += 10;
            this.nStrikes += 1;
        }

        // If random roll is a spare
        else if (rolls.size() %2 == 0 && rolls.size() > 2 && rolls.get(rolls.size() -1) + rolls.get(rolls.size() -2) == 10) {
            this.score += 5;
            this.nSpares+=1;
        }

        this.score += randomRoll;

        // Check if on last sub frame
        if (rolls.size() == 20) {
            // Check if the player has rolled a strike on the last frame, if so grant him an extra shot
            if (rolls.get(18) == 10 || rolls.get(19) == 10) {
                this.game.throwExtraFrame();
                this.eligibleForExtraFrame = true;
            }

            // Check if player has rolled a spare in the last frame, if so grant him an extra shot
            else if (rolls.get(18) + rolls.get(19) == 10) {
                this.game.throwExtraFrame();
                this.eligibleForExtraFrame = true;
            }

            // End game otherwise (The game is only ended if eligibleForExtraFrame is not set)
            this.game.endGame();

        }

        return randomRoll;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
