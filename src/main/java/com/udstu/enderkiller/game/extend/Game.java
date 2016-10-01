package com.udstu.enderkiller.game.extend;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.character.DefaultGameCharacter;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.*;
import com.udstu.enderkiller.task.TimeLapseTask;
import com.udstu.enderkiller.vote.*;
import com.udstu.enderkiller.vote.implement.VoteCallBack;
import com.udstu.enderkiller.worldcreator.MainWorldCreator;
import com.udstu.enderkiller.worldcreator.NetherWorldCreator;
import com.udstu.enderkiller.worldcreator.SpawnWorldCreator;
import com.udstu.enderkiller.worldcreator.TheEndWorldCreator;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
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
    private GameStatus gameStatus = GameStatus.notStart;
    private SkillStatus putToDeathVoteStatus = SkillStatus.cooldown;
    private int day = 1;
    private String worldNamePrefix = null;
    private String spawnWorldName = null;
    private World spawnWorld = null;
    private Location spawnWorldSpawnLocation = null;
    private String mainWorldName = null;
    private World mainWorld = null;
    private Location mainWorldSpawnLocation = null;
    private String netherWorldName = null;
    private World netherWorld = null;
    private String theEndWorldName = null;
    private World theEndWorld = null;
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
        gameStatus = GameStatus.prepare;

        //log
        thisPlugin.getLogger().info("房间 " + room.getId() + " 开始了游戏");

        changeGameMode();
        healAllPlayer();
        if (Boolean.valueOf(R.getConfig("cleanOutInventoryBeforeGame"))) {
            cleanInventory();
        }
        initOccupation();
        initWorlds();
        tpPlayersToSpawnWorld();
        allocateOccupation();
        giveInitMoney();
        toldTeamMates();
        registerTimeLapseTask();
        launchTeamLeaderVote();

        room.setGame(this);
        room.updateScoreBoard();
    }

    private void changeGameMode() {
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            gameCharacter.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    private void healAllPlayer() {
        Player player;

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            player = gameCharacter.getPlayer();
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
        }
    }

    private void cleanInventory() {
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            gameCharacter.getPlayer().getInventory().clear();
        }
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
        netherWorld = server.createWorld(new NetherWorldCreator(netherWorldName));

        room.broadcast(R.getLang("loadingWorld") + ": " + theEndWorldName);
        theEndWorld = server.createWorld(new TheEndWorldCreator(theEndWorldName));
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

    //给予初始金钱(默认为绿宝石)
    private void giveInitMoney() {
        Player player;
        Material material = Material.valueOf(R.getConfig("initMoneyType"));
        int amount = Integer.valueOf(R.getConfig("initMoneyAmount"));

        room.broadcast(R.getLang("givingInitMoney"));

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            //给予通用货币
            player = gameCharacter.getPlayer();
            player.getInventory().addItem(new ItemStack(material, amount));
            player.sendMessage(R.getLang("youGet") + ": " + material.toString() + " * " + amount);

            //给予职业特殊初始物品
            gameCharacter.giveInitItems();
        }
    }

    //告知所有潜伏者全部潜伏者信息
    private void toldTeamMates() {
        List<String> lurkerGameCharactersInfos = room.toGameCharactersInfoList(room.getGameCharacters(Alignment.lurker));
        String[] lurkerGameCharactersInfosStringArray = lurkerGameCharactersInfos.toArray(new String[lurkerGameCharactersInfos.size()]);
        List<GameCharacter> LurkerGameCharacters = room.getGameCharacters(Alignment.lurker);

        for (GameCharacter gameCharacter : LurkerGameCharacters) {
            if (gameCharacter.getAlignment() == Alignment.lurker) {
                gameCharacter.getPlayer().sendMessage(R.getLang("occupationInfo") + ": ");
                gameCharacter.getPlayer().sendMessage(lurkerGameCharactersInfosStringArray);
            }
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
        day++;
        putToDeathVoteStatus = SkillStatus.available;
        room.broadcast(R.getLang("putToDeathVoteNowIsAvailable"));
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            if (gameCharacter.getGameCharacterStatus() == GameCharacterStatus.alive) {
                //若角色为队长,刷新其 召集 技能
                if (gameCharacter.isTeamLeader()) {
                    gameCharacter.setSummonStatus(SkillStatus.available);
                    gameCharacter.getPlayer().sendMessage(R.getLang("skillCoolDownComplete").replace("{0}", R.getLang("summon")));
                }

                gameCharacter.nextDay();
            }
        }

        //第二天天亮进入远征阶段
        if (day == 2) {
            gameStatus = GameStatus.processing;
        }

        room.updateScoreBoard();
    }

    public void nextNight() {
        room.broadcast(R.getLang("nightTimeCome"));
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            if (gameCharacter.getGameCharacterStatus() == GameCharacterStatus.alive) {
                gameCharacter.nextNight();
            }
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

        //可选玩家为0时直接退出
        if (voteItems.size() == 0) {
            return;
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

    //处死投票
    public void putToDeathVote() {
        Player player;
        List<VoteItem> voteItems = new ArrayList<>();
        List<VotePlayerAndWeight> votePlayerAndWeights = new ArrayList<>();
        VoteCause voteCause = VoteCause.putToDeathVote;
        String warning = R.getLang("putToDeathVoteWarning");

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            //若玩家已死亡(屠龙杀游戏角色死亡)则不参与投票也不成为投票项
            if (gameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {
                continue;
            }
            player = gameCharacter.getPlayer();
            voteItems.add(new VoteItem(player.getName()));
            if (gameCharacter.isTeamLeader()) {
                votePlayerAndWeights.add(new VotePlayerAndWeight(player, 1.5));  //队长权重1.5
            } else {
                votePlayerAndWeights.add(new VotePlayerAndWeight(player, 1));    //普通角色权重1
            }
        }

        //可选玩家为0时直接退出
        if (voteItems.size() == 0) {
            return;
        }

        room.broadcast(R.getLang("putToDeathVoteTitle").replace("{0}", R.getConfig("voteAbstainSign")));
        //广播可用玩家列表
        String playerList = R.getLang("playerList") + ": ";
        for (VoteItem voteItem : voteItems) {
            playerList += voteItem.item + " ";
        }
        room.broadcast(playerList);

        server.getPluginManager().registerEvents(new VoteListener(voteItems, votePlayerAndWeights, voteCause, warning, this, Integer.valueOf(R.getConfig("putToDeathVoteTimeout"))), thisPlugin);
        putToDeathVoteStatus = SkillStatus.cooldown;
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
                    gameCharacter = room.getGameCharacters(teamLeaderName);

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
                    gameCharacter = room.getGameCharacters(teamLeaderName);

                    room.broadcast(R.getLang("newTeamleaderBorn") + ": " + teamLeaderName);
                    gameCharacter.getPlayer().sendMessage(R.getLang("youBecomeTeamLeader"));
                    gameCharacter.setTeamLeader();
                } else {
                    launchTeamLeaderVote();
                }
            }
            break;
            case putToDeathVote: {
                Vote.sort(voteResults);
                String voteResultStr = R.getLang("voteResult") + ": ";
                String playerName;
                GameCharacter gameCharacter;

                for (VoteResult voteResult : voteResults) {
                    voteResultStr += voteResult.voteItem.item + "[" + voteResult.votes + "]" + " ";
                }
                room.broadcast(voteResultStr);

                //有同票
                if (voteResults.size() > 1 && voteResults.get(0).votes == voteResults.get(1).votes) {
                    room.broadcast(R.getLang("thereIsSameTicket"));
                    putToDeathVote();
                } else if (voteResults.get(0).votes != 0) {  //第一名不为0票
                    playerName = voteResults.get(0).voteItem.item;
                    gameCharacter = room.getGameCharacters(playerName);

                    room.broadcast(R.getLang("playerVotedToDeath").replace("{0}", playerName));
                    gameCharacter.voteToDeath(voteResults);
                }
            }
        }
        room.updateScoreBoard();
    }

    //游戏结束
    public void gameOver(Alignment alignment) {
        long gameOverDelay = Long.valueOf(R.getConfig("gameOverDelay"));

        room.broadcast(R.getLang("gameOver"));
        room.broadcast(R.getLang("alignmentWin").replace("{0}", alignment.toString()));

        if (Boolean.valueOf(R.getConfig("cleanOutInventoryAfterGame"))) {
            cleanInventory();
        }
        reward();
        broadcastCharacterInfo();
        room.broadcast(R.getLang("timeToTeleport") + ": " + gameOverDelay / 20 + "s");
        server.getScheduler().runTaskLater(thisPlugin, new Runnable() {
            @Override
            public void run() {
                over();
            }
        }, gameOverDelay);
    }

    //分发游戏奖励
    private void reward() {
        Player player;
        Material material = Material.valueOf(R.getConfig("rewardType"));
        int amount = Integer.valueOf(R.getConfig("rewardAmount"));
        int exp = Integer.valueOf(R.getConfig("rewardExp"));

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            player = gameCharacter.getPlayer();
            //分发奖励物品
            player.getInventory().addItem(new ItemStack(material, amount));
            player.sendMessage(R.getLang("youGet") + ": " + material.toString() + " * " + amount);
            //分发奖励经验值
            player.setTotalExperience(player.getTotalExperience() + exp);
            player.sendMessage(R.getLang("youGet") + ": " + "Exp" + " * " + exp);
        }
    }

    //广播所有角色信息
    private void broadcastCharacterInfo() {
        room.broadcast(R.getLang("occupationInfo") + ": ");
        room.broadcast(room.toGameCharactersInfoString(room.getGameCharacters()));
    }

    //退出游戏
    public void over() {
        World defaultWorld = server.getWorlds().get(0);

        //取消本局游戏开启的任务
        removeTimeLapseTask();

        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            gameCharacter.gameOver();
        }

        //传送玩家至主世界
        room.broadcast(R.getLang("teleporting"));
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            //TODO 简单的传送至出生点所在处的最高的方块所在处,应实现安全传送功能.
            gameCharacter.getPlayer().teleport(defaultWorld.getHighestBlockAt(defaultWorld.getSpawnLocation()).getLocation());
        }

        //卸载世界
        server.unloadWorld(mainWorld, false);
        server.unloadWorld(netherWorld, false);
        server.unloadWorld(theEndWorld, false);

        if (Boolean.valueOf(R.getConfig("deleteWorldsAfterGame"))) {
            //删除世界
            File worldFolder = Bukkit.getWorldContainer();
            File mainWorldFolder = new File(worldFolder, mainWorldName);
            File netherWorldFolder = new File(worldFolder, netherWorldName);
            File theEndWorldFolder = new File(worldFolder, theEndWorldName);
            try {
                FileUtils.deleteDirectory(mainWorldFolder);
                FileUtils.deleteDirectory(netherWorldFolder);
                FileUtils.deleteDirectory(theEndWorldFolder);
            } catch (Exception e) {
                thisPlugin.getLogger().warning("房间 " + room.getId() + " 的一部分地图在游戏后未能删除");
            }
        }

        //重置角色
        for (int i = 0; i < room.getGameCharacters().size(); i++) {
            room.getGameCharacters().set(i, new DefaultGameCharacter(room.getGameCharacters().get(i)));
        }

        //重置房间状态
        room.setRoomStatus(RoomStatus.waitingForStart);
        room.setGame(null);
        room.updateScoreBoard();
    }

    //判断游戏是否结束
    public void checkGameOver() {
        boolean isAnyExplorerAlive = false;

        //获取是否有探险家存活
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            if (gameCharacter.getGameCharacterStatus() == GameCharacterStatus.alive && gameCharacter.getAlignment() == Alignment.explorer) {
                isAnyExplorerAlive = true;
                break;
            }
        }

        //探险家全部阵亡,游戏结束
        if (!isAnyExplorerAlive) {
            gameOver(Alignment.lurker);
        }
    }

    public int getDay() {
        return day;
    }

    public SkillStatus getPutToDeathVoteStatus() {
        return putToDeathVoteStatus;
    }

    public World getMainWorld() {
        return mainWorld;
    }

    public Location getMainWorldSpawnLocation() {
        return mainWorldSpawnLocation;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
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
