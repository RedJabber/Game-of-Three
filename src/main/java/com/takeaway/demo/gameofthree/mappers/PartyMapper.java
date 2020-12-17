package com.takeaway.demo.gameofthree.mappers;

import com.takeaway.demo.gameofthree.entities.Party;
import com.takeaway.demo.gameofthree.entities.PartyMember;
import com.takeaway.demo.gameofthree.entities.Turn;
import com.takeaway.demo.gameofthree.models.PartyInfo;
import com.takeaway.demo.gameofthree.models.PlayerInfo;
import com.takeaway.demo.gameofthree.models.PlayerMove;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * PartyMapper.
 *
 * @author Andrey Arefyev
 */
@Mapper()
public interface PartyMapper {

    @Mapping(target = "currentPlayerId", source = "currentPlayer.nickName")
    PartyInfo toDto(Party party);

    @Mapping(target = "nickName", source = "player.nickName")
    PlayerInfo mapPlayer(PartyMember member);

    @Mapping(target = "playerId", source = "player.nickName")
    PlayerMove mapTurn(Turn turn);
}