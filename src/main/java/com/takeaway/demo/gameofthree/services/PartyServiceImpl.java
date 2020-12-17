package com.takeaway.demo.gameofthree.services;

import com.takeaway.demo.gameofthree.entities.Party;
import com.takeaway.demo.gameofthree.entities.Player;
import com.takeaway.demo.gameofthree.exceptions.PartyException;
import com.takeaway.demo.gameofthree.exceptions.PartyNotFoundException;
import com.takeaway.demo.gameofthree.mappers.PartyMapper;
import com.takeaway.demo.gameofthree.models.PartyInfo;
import com.takeaway.demo.gameofthree.models.PlayerMove;
import com.takeaway.demo.gameofthree.repositories.PartyRepository;
import com.takeaway.demo.gameofthree.repositories.PlayerRepository;


import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * PartyServiceImpl.
 *
 * @author Andrey Arefyev
 */
@Service
public class PartyServiceImpl implements PartyService {

    private final PartyRepository parties;
    private final PlayerRepository players;
    private final PartyMapper partyMapper;
    private final InitialValueGenerator valueGenerator;

    public PartyServiceImpl(PartyRepository parties,
            PlayerRepository players,
            PartyMapper partyMapper,
            InitialValueGenerator valueGenerator) {
        this.parties = parties;
        this.players = players;
        this.partyMapper = partyMapper;
        this.valueGenerator = valueGenerator;
    }

    @Override
    @Transactional
    public PartyInfo create(String starterNickName) {
        var player = getOrCreatePlayer(starterNickName);
        var party = new Party(player);
        parties.save(party);
        return partyMapper.toDto(parties.save(party));
    }

    private Player getOrCreatePlayer(String starterNickName) {
        return players.findById(starterNickName)
                .orElseGet(() -> players.save(new Player().setNickName(starterNickName.trim())));
    }

    @Override
    public PartyInfo getById(String partyId) {
        return partyMapper.toDto(getPartyChecked(partyId));
    }

    @Override
    @Transactional
    public PartyInfo tryJoinParty(String partyId, String playerNick) {
        var party = getPartyChecked(partyId);
        var player = getOrCreatePlayer(playerNick);
        party.join(player);
        return partyMapper.toDto(parties.save(party));
    }

    private Party getPartyChecked(String partyId) {
        return parties.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException("error.party.is.not.found", partyId));
    }

    @Override
    public PartyInfo makeManualMove(String partyId, PlayerMove turn) {
        var party = getPartyChecked(partyId);
        var player =
                players.findById(turn.getPlayerId())
                        .orElseThrow(() -> new PartyException("error.player.is.not.found", turn.getPlayerId()));
        party.makeMove(player, turn.getAddition());
        return partyMapper.toDto(parties.save(party));
    }

    @Override
    public PartyInfo makeAutoMove(String partyId, String playerId) {
        var party = getPartyChecked(partyId);
        return makeManualMove(partyId, new PlayerMove().setPlayerId(playerId).setAddition(calcAddition(party.getCurrentValue())));
    }

    private static int calcAddition(int a) {
        return (2 * (a % 3) - 3) % 3;
    }

    @Override
    @Transactional
    public PartyInfo startParty(String partyId) {
        var party = getPartyChecked(partyId);
        party.start(valueGenerator.generate());
        return partyMapper.toDto(parties.save(party));
    }
}