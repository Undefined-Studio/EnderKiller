package com.udstu.enderkiller.command;

import com.udstu.enderkiller.*;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.RoomStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
        } else {
            if (targetRoom.isFull()) {
                commandSender.sendMessage(R.getLang("roomIsFull"));
            } else {
                targetRoom.add(new GameCharacter((Player) commandSender));
                commandSender.sendMessage(R.getLang("joinRoom").replace("{$roomId}", Integer.valueOf(targetRoom.getId()).toString()));
                R.getMainClass().getLogger().info(commandSender.getName() + " joined room " + targetRoom.getId());
            }
        }
    }

    //退出房间 /ek exit
    private void commandExit(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom;

        locatedRoom = Util.searchPlayer(commandSender.getName());

        if (locatedRoom == null) {
            commandSender.sendMessage(R.getLang("notInARoom"));
        } else {
            if (locatedRoom.remove((Player) commandSender)) {
                commandSender.sendMessage(R.getLang("exitRoomSuccessful"));
                R.getMainClass().getLogger().info(commandSender.getName() + " exited room " + locatedRoom.getId());
            } else {
                commandSender.sendMessage(R.getLang("cannotExitInGame"));
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
                Lobby.remove(targetRoom);
                commandSender.sendMessage(R.getLang("delRoomSuccessful"));
                R.getMainClass().getLogger().info(commandSender.getName() + " deleted room " + targetRoom.getId());
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
                commandSender.sendMessage(R.getLang("roomNotFull"));
            }
        }
    }

    //传送至当前游戏的主世界 /ek tp
    private void commandTp(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom;

        locatedRoom = Util.searchPlayer(commandSender.getName());

        if (locatedRoom == null) {
            commandSender.sendMessage(R.getLang("notInARoom"));
        } else {
            if (locatedRoom.getRoomStatus() == RoomStatus.inGame) {
                commandSender.sendMessage(R.getLang("teleporting"));
                ((Player) commandSender).teleport(locatedRoom.getGame().getMainWorldSpawnLocation());
            } else {
                commandSender.sendMessage(R.getLang("gameNotStart"));
            }
        }
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
            }
        }

        //无效的参数,输入 /ek help 查看帮助
        commandSender.sendMessage(R.getLang("invalidCommand").replace("{$help}", "/" + label + " " + "help"));

        return true;
    }
}
