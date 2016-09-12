package com.udstu.enderkiller.character;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.enumeration.SkillStatus;
import com.udstu.enderkiller.enumeration.VoteCause;
import com.udstu.enderkiller.vote.VoteListener;
import com.udstu.enderkiller.vote.VotePlayerAndWeight;
import com.udstu.enderkiller.vote.VoteResult;
import com.udstu.enderkiller.vote.implement.VoteCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czp on 16-8-12.
 * 牧师
 */
public class Priest extends GameCharacter implements VoteCallBack {
    private SkillStatus rescueStatus = SkillStatus.cooldown;
    private GameCharacter curseTarget = null;

    public Priest(GameCharacter gameCharacter, Alignment alignment) {
        super(gameCharacter, alignment);
        occupation = Occupation.priest;
    }

    @Override
    public void nextDay() {
        if (rescueStatus == SkillStatus.cooldown) {
            player.sendMessage(R.getLang("skillCoolDownComplete").replace("{0}", R.getLang("rescue")));
        }
        rescueStatus = SkillStatus.available;
    }

    @Override
    public void nextNight() {

    }

    //技能 拯救.条件不足导致发动失败时返回false
    public boolean rescue(GameCharacter targetGameCharacter) {
        curseTarget = targetGameCharacter;
        List<VotePlayerAndWeight> votePlayerAndWeights = new ArrayList<>();
        votePlayerAndWeights.add(new VotePlayerAndWeight(player));

        //技能是否冷却完毕
        if (rescueStatus == SkillStatus.cooldown) {
            return false;
        }

        player.sendMessage(R.getLang("doYouWantToRescue").replace("{0}", targetGameCharacter.getPlayer().getName()));
        thisPlugin.getServer().getPluginManager().registerEvents(new VoteListener(yesOrNoVoteItem, votePlayerAndWeights, VoteCause.skillLaunchVote, yesOrNoWarning, this, skillLaunchVoteTimeout, "n"), thisPlugin);

        return true;
    }

    //牧师拯救技能投票的回调函数
    @Override
    public void voteCallBack(List<VoteResult> voteResults, VoteCause voteCause) {
        //无y投票
        if (voteResults.get(0).votes == 0) {
            curseTarget.getPlayer().setHealth(0);
        } else {
            rescueStatus = SkillStatus.cooldown;
        }
    }
}
