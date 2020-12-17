package com.takeaway.demo.gameofthree.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Player.
 *
 * @author Andrey Arefyev
 */
@Entity
@Data
@Accessors(chain = true)
public class PartyMember {

    @Id
    @GeneratedValue(generator = "uuidGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

    public PartyMember() {
    }

    public PartyMember(Player player, Party party) {
        this.player = player;
        this.party = party;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("player", player.getNickName())
                .append("party", party.getId())
                .toString();
    }
}