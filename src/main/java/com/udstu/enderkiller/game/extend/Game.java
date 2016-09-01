package com.udstu.enderkiller.game.extend;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.Room;
import com.udstu.enderkiller.Util;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.worldcreator.MainWorldCreator;
import com.udstu.enderkiller.worldcreator.NetherWorldCreator;
import com.udstu.enderkiller.worldcreator.SpawnWorldCreator;
import com.udstu.enderkiller.worldcreator.TheEndWorldCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Created by czp on 16-8-25.
 * 所有游戏模式的父类
 */
public abstract class Game {
    protected Room room = null;
    protected Alignment[] alignments = null;
    protected Occupation[] lurkers = null;
    protected Occupation[] explorers = null;
    private String worldNamePrefix = null;
    private String spawnWorldName = null;
    private Location spawnWorldSpawnLocation = null;
    private String mainWorldName = null;
    private String netherWorldName = null;
    private String theEndWorldName = null;
    private Plugin thisPlugin = null;
    private Server server = null;

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
        initOccupation();
        initWorlds();
        tpPlayersToSpawnWorld();
        allocateOccupation();
        giveInitMoney();
    }

    protected abstract void initOccupation();

    private void initWorlds() {
        room.broadcast(R.getLang("initializingGame"));

        //加载或创建主城世界
        room.broadcast(R.getLang("loadingWorld") + ": " + spawnWorldName);
        spawnWorldSpawnLocation = server.createWorld(new SpawnWorldCreator(spawnWorldName)).getSpawnLocation();

        //创建游戏世界
        room.broadcast(R.getLang("loadingWorld") + ": " + mainWorldName);
        server.createWorld(new MainWorldCreator(mainWorldName));
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
