package com.takeaway.demo.gameofthree.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Turn.
 *
 * @author Andrey Arefyev
 */
@Data
@Entity
@Accessors(chain = true)
public class Turn {

    @Id
    @GeneratedValue(generator = "uuidGenerator")
    private String id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

    @Column
    private int addition;

    @Column
    private LocalDateTime timestamp;

}