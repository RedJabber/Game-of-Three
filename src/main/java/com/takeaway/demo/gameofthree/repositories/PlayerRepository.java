package com.takeaway.demo.gameofthree.repositories;

import com.takeaway.demo.gameofthree.entities.Player;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * PlayerRepository.
 *
 * @author Andrey Arefyev
 */
@Repository
public interface PlayerRepository extends CrudRepository<Player, String> {}