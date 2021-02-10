package org.bowlinggame.dao;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Double score = 0.0;
    private Integer laneNumber;

    @ManyToOne
    @JoinColumn(name="game_id")
    private Game game;

    @Column(name = "rolls")
    private String rolls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getScore() {
        return score;
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

    public String setRollString(List<Integer> integerList) {
        StringBuilder newRollString = new StringBuilder("");
        for(Integer roll : integerList) {
            newRollString.append(String.valueOf(roll));
            newRollString.append(";");
        }

        this.rolls = newRollString.toString();
        return this.rolls;
    }

    public Integer rollBall() {

        List<Integer> rolls = this.getRollsAsList();
        Integer remainingPins = 10;
        if (rolls.size() > 0 && rolls.size() % 2 != 0 && rolls.get(rolls.size() - 1) == 10) {
            rolls.add(-1);
            this.setRollString(rolls);
            return -1;
        }

        if (rolls.size() > 0 && rolls.size() % 2 != 0) {
            remainingPins = 10 - rolls.get(rolls.size() - 1);
        }

        Random roller = new Random();
        Integer randomRoll = roller.nextInt(remainingPins + 1);
        rolls.add(randomRoll);
        this.setRollString(rolls);

        if (randomRoll == 10) {
            this.score += 10;
        } else if (rolls.size() %2 == 0 && rolls.size() > 2 && rolls.get(rolls.size() -1) + rolls.get(rolls.size() -2) == 10) {
            this.score += 5;
        }

        this.score += randomRoll;

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
