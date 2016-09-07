package com.udstu.enderkiller.vote;

import org.bukkit.entity.Player;

/**
 * Created by czp on 16-9-6.
 * 投票玩家与其权重,用作结构体
 */
public class VotePlayerAndWeight {
    public Player player = null;
    public double weight = 1;

    public VotePlayerAndWeight(Player player) {
        this.player = player;
    }

    public VotePlayerAndWeight(Player player, double weight) {
        this.player = player;
        this.weight = weight;
    }
}
