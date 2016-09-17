package com.udstu.enderkiller.character;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.enumeration.VoteCause;
import com.udstu.enderkiller.listener.DeathListener;
import com.udstu.enderkiller.listener.implement.DeathEventCallBack;
import com.udstu.enderkiller.vote.*;
import com.udstu.enderkiller.vote.implement.VoteCallBack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by czp on 16-8-12.
 * 斗士
 */
public class Fighter extends GameCharacter implements VoteCallBack, DeathEventCallBack {
    private Player targetPlayer = null;

    public Fighter(GameCharacter gameCharacter, Alignment alignment) {
        super(gameCharacter, alignment);
        occupation = Occupation.fighter;
    }

    @Override
    public void nextDay() {

    }

    @Override
    public void nextNight() {

    }

    //斗士死亡时询问其是否发起决斗
    @Override
    public void voteToDeath(List<VoteResult> voteResults) {
        String duelVoteTitle = R.getLang("duelVoteTitle").replace("{0}", R.getConfig("voteAbstainSign"));
        List<VoteItem> voteItems = new ArrayList<>();
        List<VotePlayerAndWeight> votePlayerAndWeights = new ArrayList<>();

        player.sendMessage(R.getLang("youAreVotedToDeath"));

        //将除自己之外的选自己的玩家加入到选项
        for (VotePlayerAndWeight votePlayerAndWeight : voteResults.get(0).votePlayerAndWeights) {
            if (votePlayerAndWeight.player != player) {
                voteItems.add(new VoteItem(votePlayerAndWeight.player.getName()));
            }
        }

        if (voteItems.size() == 0) {
            player.setHealth(0);
        }

        votePlayerAndWeights.add(new VotePlayerAndWeight(player));

        player.sendMessage(duelVoteTitle);
        String playerList = R.getLang("playerList") + ": ";
        for (VoteItem voteItem : voteItems) {
            playerList += voteItem.item + " ";
        }
        player.sendMessage(playerList);

        thisPlugin.getServer().getPluginManager().registerEvents(new VoteListener(voteItems, votePlayerAndWeights, null, duelVoteTitle, this, Integer.valueOf(R.getConfig("duelVoteTimeout"))), thisPlugin);
    }

    @Override
    public void voteCallBack(List<VoteResult> voteResults, VoteCause voteCause) {
        Vote.sort(voteResults);

        //弃票
        if (voteResults.get(0).votes == 0) {
            player.setHealth(0);
            return;
        }

        //非弃票
        duel(room.getGameCharacter(voteResults.get(0).voteItem.item));
    }

    //斗士 技能 决斗
    public void duel(GameCharacter targetGameCharacter) {
        int teleportDelay = Integer.valueOf("teleportDelay");
        targetPlayer = targetGameCharacter.getPlayer();

        room.broadcast(R.getLang("playerIsFighter").replace("{0}", player.getName()));
        room.broadcast(R.getLang("FighterDuelWith").replace("{0}", player.getName()).replace("{1}", targetGameCharacter.getPlayer().getName()));
        targetPlayer.sendMessage(R.getLang("youAreSelectedAsDuelTarget"));
        targetPlayer.sendMessage(R.getLang("timeToTeleport") + ": " + teleportDelay / 20 + "s");

        //延迟传送
        thisPlugin.getServer().getScheduler().runTaskLater(thisPlugin, new Runnable() {
            @Override
            public void run() {
                targetPlayer.teleport(player);
            }
        }, teleportDelay);

        //添加对这两人的决斗死亡监听器
        thisPlugin.getServer().getPluginManager().registerEvents(new DeathListener(Arrays.asList(player, targetPlayer), this), thisPlugin);
    }

    @Override
    public void deathEventCallBack(Player diedPlayer) {
        //场上除斗士外还有其他人时
        if (room.getAliveGameCharacter().size() > 1) {
            //若斗士存活,则杀死
            if (gameCharacterStatus == GameCharacterStatus.alive) {
                room.broadcast(R.getLang("playerPutToDeath").replace("{0}", player.getName()));
                player.setHealth(0);
            }
        } else {   //场上仅剩下斗士时
            if (gameCharacterStatus == GameCharacterStatus.alive) {
                room.broadcast(R.getLang("fighterBecomeOutsideLawFighter").replace("{0}", player.getName()));
            }
        }
    }
}
