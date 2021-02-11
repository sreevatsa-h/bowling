package org.bowlinggame.dao;

import javax.persistence.*;

@Entity
public class Rules {

    @Id
    private Integer id;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "rule_description")
    private String ruleDescription;

    @Column(name = "value")
    private Integer value;

    public Rules() {

    }

    public Rules(Integer id, String ruleName, String ruleDescription, Integer value) {
        this.id = id;
        this.ruleName = ruleName;
        this.ruleDescription = ruleDescription;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
