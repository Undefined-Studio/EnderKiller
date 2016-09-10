package com.udstu.enderkiller.vote;

/**
 * Created by czp on 16-9-6.
 * 投票结果,用作结构体
 */
public class VoteResult {
    public VoteItem voteItem = null;
    public double votes = 0;

    public VoteResult(VoteItem voteItem) {
        this.voteItem = voteItem;
    }
}
