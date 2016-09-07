package com.udstu.enderkiller.vote.implement;

import com.udstu.enderkiller.enumeration.VoteCause;
import com.udstu.enderkiller.vote.VoteResult;

import java.util.List;

/**
 * Created by czp on 16-9-6.
 * 投票结束后的回调函数
 */
public interface VoteCallBack {
    void voteCallBack(List<VoteResult> voteResults, VoteCause voteCause);
}
