package com.udstu.enderkiller.vote;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by czp on 16-9-7.
 * 投票工具类
 */
public class Vote {
    public static void sort(List<VoteResult> voteResults) {
        Collections.sort(voteResults, new VoteResultsComparator());
    }

    private static class VoteResultsComparator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            VoteResult voteResult1 = (VoteResult) object1;
            VoteResult voteResult2 = (VoteResult) object2;

            return Double.valueOf(voteResult2.votes).compareTo(voteResult1.votes);
        }
    }
}
