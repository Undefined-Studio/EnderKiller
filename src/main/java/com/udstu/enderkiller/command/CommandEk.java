package com.udstu.enderkiller.command;

import com.udstu.enderkiller.*;
import com.udstu.enderkiller.character.DefaultGameCharacter;
import com.udstu.enderkiller.character.Detective;
import com.udstu.enderkiller.character.Warlock;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.*;
import com.udstu.enderkiller.game.extend.Game;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by czp on 16-8-4.
 * Command ek
 */
public class CommandEk implements CommandExecutor {
    //输出help菜单 /ek help
    private void commandHelp(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> helpList = new ArrayList<>();
        Pattern pattern = Pattern.compile("^command.*");
        Method[] methods = this.getClass().getDeclaredMethods();
        String methodName;
        Matcher matcher;

        //获取所有指令名(本类中以command开头的方法)并得到对应文本
        for (Method method : methods) {
            methodName = method.getName();
            matcher = pattern.matcher(methodName);
            if (matcher.matches()) {
                helpList.add("§6/" + label + " " + methodName.substring("command".length()).toLowerCase() + ": §f" + R.getLang(methodName));
            }
        }

        Util.sendMessages(commandSender, "EkHelp", R.getLang("useToGetPageN").replace("{$command}", "/" + label + " help"), helpList, Arrays.copyOfRange(args, 1, args.length));
    }

    //重载配置文件 /ek reload
    private void commandReload(CommandSender commandSender, Command command, String label, String[] args) {
        if (Config.reload()) {
            commandSender.sendMessage(R.getLang("reloadedConfiguration"));
        } else {
            commandSender.sendMessage("An error occurred while loading configuration");
            R.getMainClass().getLogger().warning("Cannot load configuration");
        }
    }

    //新建房间 /ek create <mode>
    private void commandCreate(CommandSender commandSender, Command command, String label, String[] args) {
        String mode;
        int modeInt;
        Room newRoom;

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " create " + "<" + R.getLang("mode") + ">");
            return;
        }
        mode = args[1];

        if (!Util.isInteger(mode) || Integer.parseInt(mode) != 8) {
            commandSender.sendMessage(R.getLang("modeAllowed") + ": " + "8");
            return;
        }
        modeInt = Integer.parseInt(mode);

        //若命令来源为玩家,则检测其是否已经在房间中
        if (Player.class.isInstance(commandSender)) {
            if (Util.searchPlayer(commandSender.getName()) != null) {
                commandSender.sendMessage(R.getLang("alreadyInARoom"));
                return;
            }
        }

        newRoom = new Room(commandSender.getName(), modeInt);
        if (Lobby.isFull()) {
            commandSender.sendMessage(R.getLang("numberOfRoomsOutOfLimit"));
        } else {
            Lobby.add(newRoom);
            commandSender.sendMessage(R.getLang("createRoomSuccessful"));
            R.getMainClass().getLogger().info(commandSender.getName() + " created room " + newRoom.getId());

            //若命令来源是玩家则在房间创建完毕后加入房间
            if (Player.class.isInstance(commandSender)) {
                commandJoin(commandSender, command, label, new String[]{"join", Integer.valueOf(newRoom.getId()).toString()});
            }
        }
    }

    //列出房间 /ek list
    private void commandList(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> roomInfoList = new ArrayList<>();

        for (Room room : Lobby.getRoomList()) {
            roomInfoList.add("§e" + room.getId() + " §f" + room.getName() + " " + room.getGameCharacters().size() + "/" + room.getSlot() + " " + room.getRoomStatus().toString());
        }

        if (roomInfoList.size() == 0) {
            commandSender.sendMessage(R.getLang("noRoomYet"));
        } else {
            Util.sendMessages(commandSender, "EkRooms", R.getLang("useToGetPageN").replace("{$command}", "/" + label + " list"), roomInfoList, Arrays.copyOfRange(args, 1, args.length));
        }
    }

    //加入房间 /ek join <roomId>
    private void commandJoin(CommandSender commandSender, Command command, String label, String[] args) {
        String roomId;
        Room targetRoom;

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " join <" + R.getLang("roomId") + ">");
            return;
        }
        roomId = args[1];

        //若已在房间中则结束命令
        if (Util.searchPlayer(commandSender.getName()) != null) {
            commandSender.sendMessage(R.getLang("alreadyInARoom"));
            return;
        }

        //搜寻目标房间
        targetRoom = Util.searchRoom(roomId);

        if (targetRoom == null) {
            commandSender.sendMessage(R.getLang("noSuchRoom"));
            return;
        }
        if (targetRoom.getRoomStatus() == RoomStatus.inGame) {
            commandSender.sendMessage(R.getLang("gameAlreadyStart"));
            return;
        }
        if (targetRoom.isFull()) {
            commandSender.sendMessage(R.getLang("roomIsFull"));
            return;
        }
        targetRoom.add(new DefaultGameCharacter((Player) commandSender, targetRoom));
        commandSender.sendMessage(R.getLang("joinRoom").replace("{$roomId}", Integer.valueOf(targetRoom.getId()).toString()));
        R.getMainClass().getLogger().info(commandSender.getName() + " joined room " + targetRoom.getId());
    }

    //退出房间 /ek exit
    private void commandExit(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom;

        locatedRoom = Util.searchPlayer(commandSender.getName());

        if (locatedRoom == null) {
            commandSender.sendMessage(R.getLang("notInARoom"));
        } else {
            if (locatedRoom.getRoomStatus() == RoomStatus.inGame) {
                commandSender.sendMessage(R.getLang("cannotExitInGame"));
            } else {
                if (locatedRoom.remove((Player) commandSender)) {
                    commandSender.sendMessage(R.getLang("exitRoomSuccessful"));
                    R.getMainClass().getLogger().info(commandSender.getName() + " exited room " + locatedRoom.getId());
                }
            }
        }
    }

    //删除房间 /ek del <roomId>
    private void commandDel(CommandSender commandSender, Command command, String label, String[] args) {
        String roomId;
        Room targetRoom;

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " del <" + R.getLang("roomId") + ">");
            return;
        }
        roomId = args[1];

        targetRoom = Util.searchRoom(roomId);

        if (targetRoom == null) {
            commandSender.sendMessage(R.getLang("noSuchRoom"));
        } else {
            if (targetRoom.getRoomStatus() != RoomStatus.waitingForStart) {
                commandSender.sendMessage(R.getLang("cannotDelRoomWhichIsInGame"));
            } else {
                if (targetRoom.getGameCharacters().size() != 0) {
                    commandSender.sendMessage(R.getLang("roomNotEmpty"));
                } else {
                    Lobby.remove(targetRoom);
                    commandSender.sendMessage(R.getLang("delRoomSuccessful"));
                    R.getMainClass().getLogger().info(commandSender.getName() + " deleted room " + targetRoom.getId());
                }
            }
        }
    }

    //开始游戏 /ek start
    private void commandStart(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom;

        locatedRoom = Util.searchPlayer(commandSender.getName());

        if (locatedRoom == null) {
            commandSender.sendMessage(R.getLang("notInARoom"));
        } else {
            if (!locatedRoom.startGame()) {
                commandSender.sendMessage(R.getLang("roomNotFullOrGameAlreadyStart"));
            }
        }
    }

    //传送至当前游戏的主世界 /ek tp
    private void commandTp(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom;
        World mainWorld;

        locatedRoom = Util.searchPlayer(commandSender.getName());

        if (locatedRoom == null) {
            commandSender.sendMessage(R.getLang("notInARoom"));
        } else {
            if (locatedRoom.getRoomStatus() == RoomStatus.inGame) {
                commandSender.sendMessage(R.getLang("teleporting"));
                mainWorld = locatedRoom.getGame().getMainWorld();
                ((Player) commandSender).teleport(mainWorld.getHighestBlockAt(mainWorld.getSpawnLocation()).getLocation());
            } else {
                commandSender.sendMessage(R.getLang("gameNotStart"));
            }
        }
    }

    //查看个人信息 /ek my
    private void commandMy(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom = Util.searchPlayer(commandSender.getName());
        GameCharacter gameCharacter;
        List<String> infoList = new LinkedList<>();

        if (locatedRoom == null) {
            infoList.add(R.getLang("roomId") + ": N/A");
        } else {
            infoList.add(R.getLang("roomId") + ": " + locatedRoom.getId());
            infoList.add(R.getLang("roomStatus") + ": " + locatedRoom.getRoomStatus().toString());
            if (locatedRoom.getRoomStatus() == RoomStatus.inGame) {
                infoList.add(R.getLang("gameTime") + ": day " + locatedRoom.getGame().getDay());
                gameCharacter = locatedRoom.getGameCharacters(commandSender.getName());
                infoList.add(R.getLang("yourAlignment") + ": " + gameCharacter.getAlignment());
                infoList.add(R.getLang("yourOccupation") + ": " + gameCharacter.getOccupation());
                infoList.add(R.getLang("location") + ": " + ((Player) commandSender).getWorld().getName());
                //若为潜伏者则显示其队友
                if (gameCharacter.getAlignment() == Alignment.lurker) {
                    infoList.add(R.getLang("occupationInfo") + ": " + locatedRoom.toGameCharactersInfoString(locatedRoom.getGameCharacters(Alignment.lurker)));
                }
            }
        }

        commandSender.sendMessage(infoList.toArray(new String[infoList.size()]));
    }

    //巫师 技能 诅咒 /ek curse
    private void commandCurse(CommandSender commandSender, Command command, String label, String[] args) {
        Room room = Util.searchPlayer(commandSender.getName());
        Room targetRoom;
        GameCharacter gameCharacter;
        GameCharacter targetGameCharacter;
        Warlock warlock;
        String targetPlayerName;

        //检测是否可以发动技能
        if (room == null) { //不在房间中
            commandSender.sendMessage(R.getLang("notInARoom"));
            return;
        }
        if ((room.getRoomStatus() != RoomStatus.inGame)) {  //游戏未开始
            commandSender.sendMessage(R.getLang("gameNotStart"));
            return;
        }
        gameCharacter = room.getGameCharacters(commandSender.getName());
        if (gameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {   //角色已死亡
            commandSender.sendMessage(R.getLang("youAreDead"));
            return;
        }

        if (gameCharacter.getOccupation() != Occupation.warlock) {   //不为巫师
            commandSender.sendMessage(R.getLang("occupationError"));
            return;
        }
        warlock = (Warlock) gameCharacter;

        //检验参数是否合法
        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " curse <" + R.getLang("playerName") + ">");
            return;
        }
        targetPlayerName = args[1];
        targetRoom = Util.searchPlayer(targetPlayerName);
        if (targetRoom != room) { //目标玩家不存在或不和命令发起者在一个房间中
            commandSender.sendMessage(R.getLang("targetPlayerNotInTheSameRoomWithYou"));
            return;
        }
        targetGameCharacter = targetRoom.getGameCharacters(targetPlayerName);
        if (targetGameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {    //目标已死亡
            commandSender.sendMessage(R.getLang("targetAlreadyDie"));
            return;
        }

        warlock.curse(targetGameCharacter);
    }

    //侦探 技能 调查 /ek research
    private void commandResearch(CommandSender commandSender, Command command, String label, String[] args) {
        Room room = Util.searchPlayer(commandSender.getName());
        Room targetRoom;
        GameCharacter gameCharacter;
        GameCharacter targetGameCharacter;
        Detective detective;
        String targetPlayerName;

        //检测是否可以发动技能
        if (room == null) { //不在房间中
            commandSender.sendMessage(R.getLang("notInARoom"));
            return;
        }
        if ((room.getRoomStatus() != RoomStatus.inGame)) {  //游戏未开始
            commandSender.sendMessage(R.getLang("gameNotStart"));
            return;
        }
        gameCharacter = room.getGameCharacters(commandSender.getName());
        if (gameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {   //角色已死亡
            commandSender.sendMessage(R.getLang("youAreDead"));
            return;
        }

        if (gameCharacter.getOccupation() != Occupation.detective) {   //不为巫师
            commandSender.sendMessage(R.getLang("occupationError"));
            return;
        }
        detective = (Detective) gameCharacter;

        //检验参数是否合法
        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " curse <" + R.getLang("playerName") + ">");
            return;
        }
        targetPlayerName = args[1];
        targetRoom = Util.searchPlayer(targetPlayerName);
        if (targetRoom != room) { //目标玩家不存在或不和命令发起者在一个房间中
            commandSender.sendMessage(R.getLang("targetPlayerNotInTheSameRoomWithYou"));
            return;
        }
        targetGameCharacter = targetRoom.getGameCharacters(targetPlayerName);
        if (targetGameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {    //目标已死亡
            commandSender.sendMessage(R.getLang("targetAlreadyDie"));
            return;
        }

        detective.research(targetGameCharacter);
    }

    //发起投票 /ek vote
    private void commandVote(CommandSender commandSender, Command command, String label, String[] args) {
        Room room = Util.searchPlayer(commandSender.getName());
        Game game;
        GameCharacter gameCharacter;
        Player player = (Player) commandSender;

        //检测是否可以发动技能
        if (room == null) { //不在房间中
            commandSender.sendMessage(R.getLang("notInARoom"));
            return;
        }
        game = room.getGame();
        if ((room.getRoomStatus() != RoomStatus.inGame)) {  //游戏未开始
            commandSender.sendMessage(R.getLang("gameNotStart"));
            return;
        }
        gameCharacter = room.getGameCharacters(commandSender.getName());
        if (gameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {   //角色已死亡
            commandSender.sendMessage(R.getLang("youAreDead"));
            return;
        }
        if (game.getGameStatus() == GameStatus.slaughterDragon) {    //屠龙阶段不允许投票
            commandSender.sendMessage(R.getLang("canNotLaunchVoteWhileSlaughteringDragon"));
            return;
        }
        if (game.getPutToDeathVoteStatus() != SkillStatus.available) {  //游戏当前天投票可用
            commandSender.sendMessage(R.getLang("voteAlreadyLaunchedInThisDay"));
            return;
        }
        if (player.getInventory().contains(Material.NETHER_STAR)) { //投票发起者拥有下界之星,则扣除一个
            player.getInventory().remove(Material.NETHER_STAR);
        } else {
            player.sendMessage(R.getLang("noEnough").replace("{0}", Material.NETHER_STAR.toString()));
            return;
        }

        game.putToDeathVote();
    }

    //队长 技能 召唤 /ek summon
    private void commandSummon(CommandSender commandSender, Command command, String label, String[] args) {
        Room room = Util.searchPlayer(commandSender.getName());
        GameCharacter gameCharacter;

        //检测是否可以发动技能
        if (room == null) { //不在房间中
            commandSender.sendMessage(R.getLang("notInARoom"));
            return;
        }
        if ((room.getRoomStatus() != RoomStatus.inGame)) {  //游戏未开始
            commandSender.sendMessage(R.getLang("gameNotStart"));
            return;
        }
        gameCharacter = room.getGameCharacters(commandSender.getName());
        if (gameCharacter.getGameCharacterStatus() != GameCharacterStatus.alive) {   //角色已死亡
            commandSender.sendMessage(R.getLang("youAreDead"));
            return;
        }
        if (!gameCharacter.isTeamLeader()) {    //不是队长
            commandSender.sendMessage(R.getLang("youAreNotTeamLeader"));
            return;
        }

        gameCharacter.summon();
    }

    //op指令,显示房间信息 /ek show
    private void commandShow(CommandSender commandSender, Command command, String label, String[] args) {
        String roomId;
        Room targetRoom;
        List<String> info = new ArrayList<>();

        //非控制台或命令方块且不为op时禁止使用
        if ((!commandSender.getName().equals("CONSOLE") || !commandSender.getName().equals("@")) && !commandSender.isOp()) {
            commandSender.sendMessage("Permission Denied");
            return;
        }

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " show <" + R.getLang("roomId") + ">");
            return;
        }
        roomId = args[1];

        targetRoom = Util.searchRoom(roomId);

        if (targetRoom == null) {
            commandSender.sendMessage(R.getLang("noSuchRoom"));
            return;
        }

        info.add(R.getLang("roomId") + ": " + targetRoom.getId());
        info.add(R.getLang("roomName") + ": " + targetRoom.getName());
        info.add(R.getLang("mode") + ": " + targetRoom.getSlot());
        info.add(R.getLang("roomStatus") + ": " + targetRoom.getRoomStatus());
        info.add(R.getLang("occupationInfo") + ":\n" + targetRoom.toGameCharactersInfoString(targetRoom.getGameCharacters()));
        commandSender.sendMessage(info.toArray(new String[info.size()]));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
                case "help": {
                    commandHelp(commandSender, command, label, args);
                    return true;
                }
                case "reload": {
                    commandReload(commandSender, command, label, args);
                    return true;
                }
                case "create": {
                    commandCreate(commandSender, command, label, args);
                    return true;
                }
                case "list": {
                    commandList(commandSender, command, label, args);
                    return true;
                }
                case "join": {
                    //若命令来源非玩家,则不执行
                    if (Player.class.isInstance(commandSender)) {
                        commandJoin(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "exit": {
                    if (Player.class.isInstance(commandSender)) {
                        commandExit(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "del": {
                    commandDel(commandSender, command, label, args);
                    return true;
                }
                case "start": {
                    if (Player.class.isInstance(commandSender)) {
                        commandStart(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "tp": {
                    if (Player.class.isInstance(commandSender)) {
                        commandTp(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "my": {
                    if (Player.class.isInstance(commandSender)) {
                        commandMy(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "curse": {
                    if (Player.class.isInstance(commandSender)) {
                        commandCurse(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "research": {
                    if (Player.class.isInstance(commandSender)) {
                        commandResearch(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "vote": {
                    if (Player.class.isInstance(commandSender)) {
                        commandVote(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "summon": {
                    if (Player.class.isInstance(commandSender)) {
                        commandSummon(commandSender, command, label, args);
                    } else {
                        commandSender.sendMessage(R.getLang("onlyPlayerCanUseThisCommand"));
                    }
                    return true;
                }
                case "show": {
                    commandShow(commandSender, command, label, args);
                    return true;
                }
            }
        }

        //无效的参数,输入 /ek help 查看帮助
        commandSender.sendMessage(R.getLang("invalidCommand").replace("{$help}", "/" + label + " " + "help"));

        return true;
    }
}
