package com.takeaway.demo.gameofthree.services;

import com.takeaway.demo.gameofthree.models.PartyInfo;
import com.takeaway.demo.gameofthree.models.PlayerMove;

/**
 * GameService.
 *
 * @author Andrey Arefyev
 */
public interface PartyService {
    PartyInfo create(String starterNickName);

    PartyInfo getById(String partyId);

    PartyInfo tryJoinParty(String partyId, String player);

    PartyInfo makeManualMove(String partyId, PlayerMove turn);

    PartyInfo makeAutoMove(String partyId, String playerId);

    PartyInfo startParty(String partyId);
}