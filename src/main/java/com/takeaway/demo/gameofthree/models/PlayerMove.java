package com.takeaway.demo.gameofthree.models;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Turn.
 *
 * @author Andrey Arefyev
 */

@Data
@Accessors(chain = true)
public class PlayerMove {
    private String playerId;
    private Integer addition;
}