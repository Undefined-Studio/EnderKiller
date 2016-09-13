package com.udstu.enderkiller.vote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czp on 16-9-6.
 * 投票结果,用作结构体
 */
public class VoteResult {
    public VoteItem voteItem = null;
    public double votes = 0;
    //投票给此选项的玩家与权重
    public List<VotePlayerAndWeight> votePlayerAndWeights = new ArrayList<>();

    public VoteResult(VoteItem voteItem) {
        this.voteItem = voteItem;
    }
}
