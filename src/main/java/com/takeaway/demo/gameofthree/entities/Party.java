package com.takeaway.demo.gameofthree.entities;

import com.takeaway.demo.gameofthree.exceptions.PartyException;


import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * Party.
 *
 * @author Andrey Arefyev
 */

@Data
@Entity
@Accessors(chain = true)
@GenericGenerator(name = "uuidGenerator", strategy = "org.hibernate.id.UUIDGenerator")
public class Party {

    @Id
    @GeneratedValue(generator = "uuidGenerator")
    private String id;

    @Column(name = "initial_value")
    private int initialValue;

    @OneToMany(cascade = CascadeType.ALL)
    private List<PartyMember> players = new ArrayList<>();

    /**
     * A player one should make a move.
     */
    @ManyToOne
    @JoinColumn(name = "current_player_id")
    private Player currentPlayer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "party_id")
    @OrderBy("timestamp")
    private List<Turn> turns = new ArrayList<>();

    protected Party() {}

    public Party(Player player) {
        players.add(new PartyMember().setParty(this).setPlayer(player));
    }

    public boolean isStarted() {
        return players.size() == 2
               && currentPlayer != null
               && initialValue > 0;
    }

    private void setNextPlayer() {
        if (!isStarted()) {
            return;
        }
        players.stream()
                .map(PartyMember::getPlayer)
                .filter(Predicate.not(currentPlayer::equals))
                .findFirst()
                .ifPresent(this::setCurrentPlayer);
    }

    public int getCurrentValue() {
        return turns.stream()
                .map(Turn::getAddition)
                .reduce(initialValue, (lastValue, addition) -> (lastValue + addition) / 3);
    }

    public boolean isOver() {
        return getCurrentValue() == 1;
    }

    public void makeMove(Player player, int addition) {
        checkIsStarted();
        int currentValue = getCurrentValue();
        if ((currentValue + addition) % 3 != 0) {
            throw new PartyException("error.turn.addition.is.incorrect");
        }
        if (addition < -1 || addition > 1) {
            throw new PartyException("error.turn.addition.is.not.in.range", addition);
        }
        if (!currentPlayer.equals(player)) {
            throw new PartyException("error.party.player.step.out.of.order");
        }
        turns.add(new Turn()
                .setAddition(addition)
                .setPlayer(currentPlayer)
                .setParty(this)
                .setTimestamp(LocalDateTime.now()));
        if (!isOver()) {
            setNextPlayer();
        }
    }

    private void checkIsNotStarted() {
        if (isStarted()) {
            throw new PartyException("error.party.is.not.started.yet." + id);
        }
    }

    private void checkIsStarted() {
        if (!isStarted()) {
            throw new PartyException("error.party.is.already.started." + id);
        }
    }

    public void start(int initialValue) {
        checkIsNotStarted();
        currentPlayer = players.get(1).getPlayer();
        this.initialValue = initialValue;
    }

    public void join(Player player) {
        checkIsNotStarted();
        if (players.stream().map(PartyMember::getPlayer).anyMatch(player::equals)) {
            throw new PartyException("error.no.party.self.play." + player.getNickName());
        }
        if (players.size() >= 2) {
            throw new PartyException("error.party.have.enough.players");
        }
        players.add(new PartyMember(player, this));
    }
}