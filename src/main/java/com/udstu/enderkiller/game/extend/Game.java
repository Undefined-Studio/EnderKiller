package com.udstu.enderkiller.game.extend;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.enumeration.VoteCause;
import com.udstu.enderkiller.task.TimeLapseTask;
import com.udstu.enderkiller.vote.*;
import com.udstu.enderkiller.vote.implement.VoteCallBack;
import com.udstu.enderkiller.worldcreator.MainWorldCreator;
import com.udstu.enderkiller.worldcreator.NetherWorldCreator;
import com.udstu.enderkiller.worldcreator.SpawnWorldCreator;
import com.udstu.enderkiller.worldcreator.TheEndWorldCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czp on 16-8-25.
 * 所有游戏模式的父类
 */
public abstract class Game implements VoteCallBack {
    protected Room room = null;
    protected Alignment[] alignments = null;
    protected Occupation[] lurkers = null;
    protected Occupation[] explorers = null;
    private int day = 1;
    private String worldNamePrefix = null;
    private String spawnWorldName = null;
    private World spawnWorld = null;
    private Location spawnWorldSpawnLocation = null;
    private String mainWorldName = null;
    private World mainWorld = null;
    private Location mainWorldSpawnLocation = null;
    private String netherWorldName = null;
    private String theEndWorldName = null;
    private Plugin thisPlugin = null;
    private Server server = null;
    private BukkitTask timeLapseTask = null;

    public Game(Room room) {
        this.room = room;
        int id = room.getId();
        thisPlugin = R.getMainClass();
        server = thisPlugin.getServer();

        worldNamePrefix = R.getConfig("worldNamePrefix");
        spawnWorldName = R.getConfig("spawnWorldName");
        mainWorldName = worldNamePrefix + "_" + id + "_main";
        netherWorldName = worldNamePrefix + "_" + id + "_nether";
        theEndWorldName = worldNamePrefix + "_" + id + "_the_end";
    }

    public void start() {
        //log
        thisPlugin.getLogger().info("房间 " + room.getId() + " 开始了游戏");

        initOccupation();
        initWorlds();
        tpPlayersToSpawnWorld();
        allocateOccupation();
        giveInitMoney();
        registerTimeLapseTask();
        launchTeamLeaderVote();

        room.setGame(this);
        room.updateScoreBoard();
    }

    protected abstract void initOccupation();

    private void initWorlds() {
        room.broadcast(R.getLang("initializingGame"));

        //加载或创建主城世界
        room.broadcast(R.getLang("loadingWorld") + ": " + spawnWorldName);
        spawnWorld = server.createWorld(new SpawnWorldCreator(spawnWorldName));
        spawnWorldSpawnLocation = spawnWorld.getSpawnLocation();
        spawnWorld.setPVP(false);
        spawnWorld.setGameRuleValue("doDaylightCycle", "false");
        spawnWorld.setGameRuleValue("doFireTick", "false");
        spawnWorld.setGameRuleValue("doMobLoot", "false");
        spawnWorld.setGameRuleValue("doMobSpawning", "false");
        spawnWorld.setGameRuleValue("keepInventory", "true");
        spawnWorld.setGameRuleValue("mobGriefing", "false");
        spawnWorld.setTime(6000);

        //创建游戏世界
        room.broadcast(R.getLang("loadingWorld") + ": " + mainWorldName);
        mainWorld = server.createWorld(new MainWorldCreator(mainWorldName));
        mainWorldSpawnLocation = mainWorld.getSpawnLocation();
        mainWorld.setTime(13800);

        room.broadcast(R.getLang("loadingWorld") + ": " + netherWorldName);
        server.createWorld(new NetherWorldCreator(netherWorldName));

        room.broadcast(R.getLang("loadingWorld") + ": " + theEndWorldName);
        server.createWorld(new TheEndWorldCreator(theEndWorldName));
    }

    private void tpPlayersToSpawnWorld() {
        room.broadcast(R.getLang("teleporting"));
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            gameCharacter.getPlayer().teleport(spawnWorldSpawnLocation);
        }
    }

    private void allocateOccupation() {
        int i;
        AlignmentAndOccupation[] alignmentAndOccupations = new AlignmentAndOccupation[alignments.length];
        List<GameCharacter> gameCharacters = room.getGameCharacters();
        GameCharacter gameCharacter;
        Player player;
        Alignment alignment;
        Occupation occupation;

        room.broadcast(R.getLang("allocatingOccupation"));

        //构造结构体数组
        for (i = 0; i < lurkers.length; i++) {
            alignmentAndOccupations[i] = new AlignmentAndOccupation(alignments[i], lurkers[i]);
        }
        for (; i < lurkers.length + explorers.length; i++) {
            alignmentAndOccupations[i] = new AlignmentAndOccupation(alignments[i], explorers[i - lurkers.length]);
        }

        //对包含阵营和职业的结构体数组进行随机排序
        alignmentAndOccupations = (AlignmentAndOccupation[]) Util.randomSort(alignmentAndOccupations);

        for (i = 0; i < gameCharacters.size(); i++) {
            gameCharacter = gameCharacters.get(i);
            player = gameCharacter.getPlayer();
            alignment = alignmentAndOccupations[i].alignment;
            occupation = alignmentAndOccupations[i].occupation;

            gameCharacters.set(i, occupation.newGameCharacter(gameCharacter, alignment));

            player.sendMessage(R.getLang("yourAlignment") + ": " + alignment.toString());
            player.sendMessage(R.getLang("yourOccupation") + ": " + occupation.toString());
        }

        //log
        String occupationsInfo = "";
        for (i = 0; i < gameCharacters.size(); i++) {
            occupationsInfo += "\n" + "[" + (i + 1) + "]" + gameCharacters.get(i).getPlayer().getName() + " " + alignmentAndOccupations[i].alignment + " " + alignmentAndOccupations[i].occupation;
        }
        thisPlugin.getLogger().info("房间 " + room.getId() + " 的身份分配情况: " + occupationsInfo);
    }

    private void giveInitMoney() {
        Player player;
        PlayerInventory playerInventory;
        Material material;
        int amount;

        room.broadcast(R.getLang("givingInitMoney"));

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            player = gameCharacter.getPlayer();
            playerInventory = player.getInventory();
            material = Material.valueOf(R.getConfig("initMoneyType"));
            amount = Integer.valueOf(R.getConfig("initMoneyAmount"));

            playerInventory.addItem(new ItemStack(material, amount));
            player.sendMessage(R.getLang("youGet") + ": " + material.toString() + " * " + amount);
        }
    }

    private boolean registerTimeLapseTask() {
        if (timeLapseTask == null) {
            timeLapseTask = server.getScheduler().runTaskTimer(thisPlugin, new TimeLapseTask(this, mainWorld), 1, 1);
            return true;
        } else {
            return false;
        }
    }

    public void over() {
        removeTimeLapseTask();
    }

    private boolean removeTimeLapseTask() {
        if (timeLapseTask == null) {
            return false;
        } else {
            timeLapseTask.cancel();
            timeLapseTask = null;
            return true;
        }
    }

    public void nextDay() {
        room.broadcast(R.getLang("dayTimeCome"));
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            day++;
            gameCharacter.nextDay();
            room.updateScoreBoard();
        }
    }

    public void nextNight() {
        room.broadcast(R.getLang("nightTimeCome"));
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            gameCharacter.nextNight();
        }
    }

    //队长选举
    private void launchTeamLeaderVote() {
        Player player;
        List<VoteItem> voteItems = new ArrayList<>();
        List<VotePlayerAndWeight> votePlayerAndWeights = new ArrayList<>();
        VoteCause voteCause = VoteCause.teamLeaderVote;
        String warning = R.getLang("teamLeaderVoteWarning");

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            //若玩家已死亡(屠龙杀游戏角色死亡)则不参与投票也不成为投票项
            if (gameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {
                continue;
            }
            player = gameCharacter.getPlayer();
            voteItems.add(new VoteItem(player.getName()));
            votePlayerAndWeights.add(new VotePlayerAndWeight(player));
        }

        room.broadcast(R.getLang("teamLeaderVoteTitle").replace("{0}", R.getConfig("voteAbstainSign")));
        //广播可用玩家列表
        String playerList = R.getLang("playerList") + ": ";
        for (VoteItem voteItem : voteItems) {
            playerList += voteItem.item + " ";
        }
        room.broadcast(playerList);

        server.getPluginManager().registerEvents(new VoteListener(voteItems, votePlayerAndWeights, voteCause, warning, this, Integer.valueOf(R.getConfig("teamLeaderVoteTimeout"))), thisPlugin);
    }

    //队长死亡时选举接班人
    public void launchTeamLeaderDieVote(Player teamLeader) {
        List<VoteItem> voteItems = new ArrayList<>();
        List<VotePlayerAndWeight> votePlayerAndWeights = new ArrayList<>();
        VoteCause voteCause = VoteCause.teamLeaderDieVote;
        String warning = R.getLang("teamLeaderVoteWarning");

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            //若玩家已死亡(屠龙杀游戏角色死亡)则不成为投票项
            if (gameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {
                continue;
            }
            voteItems.add(new VoteItem(gameCharacter.getPlayer().getName()));
        }

        //可选玩家为0时直接退出
        if (voteItems.size() == 0) {
            return;
        }

        votePlayerAndWeights.add(new VotePlayerAndWeight(teamLeader));

        teamLeader.sendMessage(R.getLang("teamLeaderVoteTitle").replace("{0}", R.getConfig("voteAbstainSign")));
        String playerList = R.getLang("playerList") + ": ";
        for (VoteItem voteItem : voteItems) {
            playerList += voteItem.item + " ";
        }
        teamLeader.sendMessage(playerList);

        server.getPluginManager().registerEvents(new VoteListener(voteItems, votePlayerAndWeights, voteCause, warning, this, Integer.valueOf(R.getConfig("teamLeaderDieVoteTimeout"))), thisPlugin);
    }

    @Override
    public void voteCallBack(List<VoteResult> voteResults, VoteCause voteCause) {
        switch (voteCause) {
            case teamLeaderVote: {
                Vote.sort(voteResults);
                String voteResultStr = R.getLang("voteResult") + ": ";
                String teamLeaderName;
                GameCharacter gameCharacter;

                for (VoteResult voteResult : voteResults) {
                    voteResultStr += voteResult.voteItem.item + "[" + voteResult.votes + "]" + " ";
                }
                room.broadcast(voteResultStr);

                //有同票
                if (voteResults.size() > 1 && voteResults.get(0).votes == voteResults.get(1).votes) {
                    room.broadcast(R.getLang("thereIsSameTicket"));
                    launchTeamLeaderVote();
                } else {
                    teamLeaderName = voteResults.get(0).voteItem.item;
                    gameCharacter = room.getGameCharacter(teamLeaderName);

                    room.broadcast(R.getLang("newTeamleaderBorn") + ": " + teamLeaderName);
                    gameCharacter.getPlayer().sendMessage(R.getLang("youBecomeTeamLeader"));
                    gameCharacter.setTeamLeader();
                }
            }
            break;
            case teamLeaderDieVote: {
                Vote.sort(voteResults);
                String teamLeaderName;
                GameCharacter gameCharacter;
                //在有投票时,即非超时未选或弃权的情况
                if (voteResults.get(0).votes != 0) {
                    teamLeaderName = voteResults.get(0).voteItem.item;
                    gameCharacter = room.getGameCharacter(teamLeaderName);

                    room.broadcast(R.getLang("newTeamleaderBorn") + ": " + teamLeaderName);
                    gameCharacter.getPlayer().sendMessage(R.getLang("youBecomeTeamLeader"));
                    gameCharacter.setTeamLeader();
                } else {
                    launchTeamLeaderVote();
                }
            }
            break;
        }
        room.updateScoreBoard();
    }

    public int getDay() {
        return day;
    }

    public Location getMainWorldSpawnLocation() {
        return mainWorldSpawnLocation;
    }

    //内部类,用作结构体
    private class AlignmentAndOccupation {
        public Alignment alignment;
        public Occupation occupation;

        public AlignmentAndOccupation(Alignment alignment, Occupation occupation) {
            this.alignment = alignment;
            this.occupation = occupation;
        }
    }
}
