package org.bowlinggame.dao;

import org.springframework.data.repository.CrudRepository;

public interface GameRepo extends CrudRepository<Game, Integer> {
}
