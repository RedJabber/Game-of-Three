package com.takeaway.demo.gameofthree.repositories;

import com.takeaway.demo.gameofthree.entities.Party;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * PartyPepository.
 *
 * @author Andrey Arefyev
 */
@Repository
public interface PartyRepository extends CrudRepository<Party, String> {}