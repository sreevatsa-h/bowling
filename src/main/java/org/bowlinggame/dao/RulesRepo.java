package org.bowlinggame.dao;

import org.springframework.data.repository.CrudRepository;

public interface RulesRepo extends CrudRepository<Rules, Integer> {

    Rules findByRuleName(String ruleName);
}
