package com.takeaway.demo.gameofthree.models;

import lombok.Data;

import java.util.List;

/**
 * GameInfo.
 *
 * @author Andrey Arefyev
 */
@Data
public class PartyInfo {
     private String id;
     private List<PlayerInfo> players;
     private List<PlayerMove> turns;
     private int currentValue;
     private String currentPlayerId;
     private boolean started;
     private boolean over;
}