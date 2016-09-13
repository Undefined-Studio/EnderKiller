package com.udstu.enderkiller.vote;

/**
 * Created by czp on 16-9-6.
 * 投票项,用作结构体
 */
public class VoteItem {
    public int no = 0;
    public String item;

    public VoteItem(String item) {
        this.item = item;
    }

    public VoteItem(int no, String item) {
        this.no = no;
        this.item = item;
    }
}
