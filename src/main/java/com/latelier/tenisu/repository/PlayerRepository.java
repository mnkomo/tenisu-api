package com.latelier.tenisu.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.latelier.tenisu.model.Player;

@Repository
public interface PlayerRepository extends MongoRepository<Player, String> {

    boolean existsByFirstnameAndLastname(String firstname, String lastname);

}
