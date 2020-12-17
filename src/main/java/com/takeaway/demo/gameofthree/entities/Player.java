package com.takeaway.demo.gameofthree.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Player.
 *
 * @author Andrey Arefyev
 */
@Entity
@Data
@Accessors(chain = true)
@EqualsAndHashCode(of = "nickName")
public class Player {

    @Id
    @Column(name = "nick")
    private String nickName;

}