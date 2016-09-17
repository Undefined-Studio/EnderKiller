package com.udstu.enderkiller.vote;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.enumeration.VoteCause;
import com.udstu.enderkiller.task.Timer;
import com.udstu.enderkiller.task.implement.TimerCallBack;
import com.udstu.enderkiller.vote.implement.VoteCallBack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czp on 16-9-6.
 * 投票监听器
 */
public class VoteListener implements Listener, TimerCallBack {
    private Plugin thisPlugin = R.getMainClass();
    private List<VoteItem> voteItems = null;
    private List<VotePlayerAndWeight> votePlayerAndWeights = null;
    private VoteCause voteCause = null;
    private String warning = null;
    private VoteCallBack voteCallBack = null;
    private List<VoteResult> voteResults = null;
    private long longestTime = -1;
    private String defaultChoose = null;
    private BukkitTask timer = null;

    public VoteListener(List<VoteItem> voteItems, List<VotePlayerAndWeight> votePlayerAndWeights, VoteCause voteCause, String warning, VoteCallBack voteCallBack) {
        this.voteItems = voteItems;
        this.votePlayerAndWeights = votePlayerAndWeights;
        this.voteCause = voteCause;
        this.warning = warning;
        this.voteCallBack = voteCallBack;

        voteResults = new ArrayList<>();
        for (VoteItem voteItem : voteItems) {
            voteResults.add(new VoteResult(voteItem));
        }
    }

    public VoteListener(List<VoteItem> voteItems, List<VotePlayerAndWeight> votePlayerAndWeights, VoteCause voteCause, String warning, VoteCallBack voteCallBack, long longestTime) {
        this(voteItems, votePlayerAndWeights, voteCause, warning, voteCallBack);
        this.longestTime = longestTime;
        timer = thisPlugin.getServer().getScheduler().runTaskLater(thisPlugin, new Timer(this), longestTime);
    }

    public VoteListener(List<VoteItem> voteItems, List<VotePlayerAndWeight> votePlayerAndWeights, VoteCause voteCause, String warning, VoteCallBack voteCallBack, long longestTime, String defaultChoose) {
        this(voteItems, votePlayerAndWeights, voteCause, warning, voteCallBack, longestTime);
        this.defaultChoose = defaultChoose;
    }

    //得到输入值在投票项中的下标,若不存在则返回-1
    private static int indexOfVoteItem(List<VoteItem> voteItems, String voteItem) {
        for (int i = 0; i < voteItems.size(); i++) {
            if (voteItems.get(i).item.equals(voteItem)) {
                return i;
            }
        }

        return -1;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent asyncPlayerChatEvent) {
        Player player = asyncPlayerChatEvent.getPlayer();
        String message = asyncPlayerChatEvent.getMessage();
        int indexOfPlayers;
        int indexOfVoteItems;

        //若事件已被取消则直接退出
        if (asyncPlayerChatEvent.isCancelled()) {
            return;
        }

        //检测玩家是否在投票目标玩家群体内
        for (indexOfPlayers = 0; indexOfPlayers < votePlayerAndWeights.size(); indexOfPlayers++) {
            if (votePlayerAndWeights.get(indexOfPlayers).player.getName().equals(player.getName())) {
                break;
            }
        }

        //此玩家是投票目标玩家群体中的一个
        if (indexOfPlayers < votePlayerAndWeights.size()) {
            //若输入弃权符号则直接从投票目标玩家群体中移除此玩家
            if (message.equals(R.getConfig("voteAbstainSign"))) {
                player.sendMessage(R.getLang("youAbstainFromVoting"));
                votePlayerAndWeights.remove(indexOfPlayers);
            } else {
                indexOfVoteItems = indexOfVoteItem(voteItems, message);
                //若输入值不为任何一个投票项,则给予此玩家提示
                if (indexOfVoteItems == -1) {
                    player.sendMessage(warning);
                } else {    //投票给目标选项
                    player.sendMessage(R.getLang("youChoose") + ": " + message);
                    voteResults.get(indexOfVoteItems).votes += votePlayerAndWeights.get(indexOfPlayers).weight;
                    voteResults.get(indexOfVoteItems).votePlayerAndWeights.add(votePlayerAndWeights.get(indexOfPlayers));
                    votePlayerAndWeights.remove(indexOfPlayers);
                }
            }
            //关闭此事件,以防止其他插件或spigot对玩家输入做出反应
            asyncPlayerChatEvent.setCancelled(true);
        }

        //若投票目标玩家群体大小为0,即所有人已投票完毕
        if (votePlayerAndWeights.size() == 0) {
            over();
        }
    }

    @Override
    public void timerCallBack() {
        int index;
        VoteResult voteResult;

        for (VotePlayerAndWeight votePlayerAndWeight : votePlayerAndWeights) {
            votePlayerAndWeight.player.sendMessage(R.getLang("votingTimeout"));
        }

        //默认选项不为null时
        if (defaultChoose != null) {
            index = indexOfVoteItem(voteItems, defaultChoose);
            //当默认选项有效时
            if (index != -1) {
                voteResult = voteResults.get(index);
                for (VotePlayerAndWeight votePlayerAndWeight : votePlayerAndWeights) {
                    votePlayerAndWeight.player.sendMessage(R.getLang("youChoose") + ": " + defaultChoose);
                    voteResult.votes += votePlayerAndWeight.weight;
                }
            } else {
                thisPlugin.getLogger().warning("默认选项 '" + defaultChoose + "' 不存在");
            }

        }

        over();
    }

    private void over() {
        if (timer != null) {
            timer.cancel();
        }
        HandlerList.unregisterAll(this);
        //回调函数.加入计划任务使其变为同步
        thisPlugin.getServer().getScheduler().runTask(thisPlugin, new Runnable() {
            @Override
            public void run() {
                voteCallBack.voteCallBack(voteResults, voteCause);
            }
        });
    }
}
