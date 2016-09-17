package com.udstu.enderkiller.character.extend;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.enumeration.SkillStatus;
import com.udstu.enderkiller.task.SummonTask;
import com.udstu.enderkiller.vote.VoteItem;
import com.udstu.enderkiller.vote.VoteResult;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;

/**
 * Created by czp on 16-8-12.
 * 所有职业的父类
 */
public abstract class GameCharacter {
    protected static List<VoteItem> yesOrNoVoteItem = Arrays.asList(new VoteItem("y"), new VoteItem("n"));    //默认的yes和no选项
    protected Player player = null;
    protected Plugin thisPlugin = R.getMainClass();
    protected Room room = null;
    protected Alignment alignment = null;
    protected Occupation occupation = null;
    protected String yesOrNoWarning = R.getLang("pleaseInputYesOrNo");  //默认的yes和no提示
    protected int skillLaunchVoteTimeout = Integer.valueOf(R.getConfig("skillLaunchVoteTimeout"));
    protected GameCharacterStatus gameCharacterStatus = GameCharacterStatus.alive;
    private SkillStatus summonStatus = SkillStatus.cooldown;
    private boolean isTeamLeader = false;
    private BukkitTask summonTask = null;

    public GameCharacter(Player player, Room room) {
        this.room = room;
        this.player = player;
    }

    public GameCharacter(GameCharacter gameCharacter) {
        this.room = gameCharacter.getRoom();
        this.player = gameCharacter.getPlayer();
    }

    public GameCharacter(GameCharacter gameCharacter, Alignment alignment) {
        this.room = gameCharacter.getRoom();
        this.player = gameCharacter.getPlayer();
        this.alignment = alignment;
    }

    public abstract void nextDay();

    public abstract void nextNight();

    //给予初始物品
    public void giveInitItems() {
        PlayerInventory playerInventory = player.getInventory();
        Material material;
        int amount;

        //所有玩家初始给予一个下界之星,用于投票的发起
        material = Material.NETHER_STAR;
        amount = 1;
        playerInventory.addItem(new ItemStack(material, amount));
        player.sendMessage(R.getLang("youGet") + ": " + material.toString() + " * " + amount);

        //潜伏者阵营初始给予一个恶魂之泪,用于部分技能的发动
        if (alignment == Alignment.lurker) {
            material = Material.GHAST_TEAR;
            amount = 1;
            playerInventory.addItem(new ItemStack(material, amount));
            player.sendMessage(R.getLang("youGet") + ": " + material.toString() + " * " + amount);
        }
    }

    public void onDeath() {
        gameCharacterStatus = GameCharacterStatus.dead;
        unsetTeamLeader();
        //将玩家变为观察者
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void voteToDeath(List<VoteResult> voteResults) {
        int voteToDieDelay = Integer.valueOf(R.getConfig("voteToDieDelay"));

        player.sendMessage(R.getLang("youAreVotedToDeath"));
        player.sendMessage(R.getLang("timeToDie") + ": " + voteToDieDelay / 20 + " s");

        //延迟杀死玩家
        thisPlugin.getServer().getScheduler().runTaskLater(thisPlugin, new Runnable() {
            @Override
            public void run() {
                player.setHealth(0);
            }
        }, voteToDieDelay);
    }

    public boolean isTeamLeader() {
        return isTeamLeader;
    }

    public void setTeamLeader() {
        if (!isTeamLeader()) {
            player.setMaxHealth(player.getMaxHealth() + 10);
            player.setHealth(player.getHealth() + 10);
        }
        isTeamLeader = true;
    }

    public void unsetTeamLeader() {
        if (isTeamLeader()) {
            player.setMaxHealth(player.getMaxHealth() - 10);
        }
        isTeamLeader = false;

        //传送任务存在时,取消它
        if (summonTask != null) {
            summonTask.cancel();
            room.broadcast(R.getLang("summonIsCanceled"));
        }
    }

    //队长 技能 召集
    public void summon() {
        int teamLeaderSummonDelay = Integer.valueOf(R.getConfig("teamLeaderSummonDelay"));

        //技能未冷却完毕时
        if (summonStatus != SkillStatus.available) {
            player.sendMessage(R.getLang("skillCooldown"));
            return;
        }

        //技能发动
        summonStatus = SkillStatus.cooldown;

        room.broadcast(R.getLang("teamLeaderStartSummon").replace("{0}", player.getName()));
        summonTask = thisPlugin.getServer().getScheduler().runTaskLater(thisPlugin, new SummonTask(room.getGameCharacters(), this), teamLeaderSummonDelay);

        room.broadcast(R.getLang("timeToTeleport") + ": " + teamLeaderSummonDelay / 20 + "s");
    }

    //游戏结束时执行
    public void gameOver() {
        unsetTeamLeader();
        //将玩家重置为生存模式
        player.setGameMode(GameMode.SURVIVAL);
    }

    public void setSummonStatus(SkillStatus summonStatus) {
        this.summonStatus = summonStatus;
    }

    public Room getRoom() {
        return room;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public GameCharacterStatus getGameCharacterStatus() {
        return gameCharacterStatus;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public void setSummonTask(BukkitTask summonTask) {
        this.summonTask = summonTask;
    }
}
