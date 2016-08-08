package com.udstu.enderkiller.command;

import com.udstu.enderkiller.*;
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
                helpList.add("/" + label + " " + methodName.substring("command".length()).toLowerCase() + " " + R.getLang(methodName));
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
        }
    }

    //新建房间 /ek create <mode>
    private void commandCreate(CommandSender commandSender, Command command, String label, String[] args) {
        String mode;
        int modeInt;
        Room newRoom;
        Room targetRoom;

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
            for (Room room : Lobby.getRoomList()) {
                for (Player player : room.getPlayers()) {
                    if (player == commandSender) {
                        commandSender.sendMessage(R.getLang("alreadyInARoom"));
                        return;
                    }
                }
            }
        }

        newRoom = new Room(commandSender.getName(), modeInt);
        if (Lobby.isFull()) {
            commandSender.sendMessage(R.getLang("numberOfRoomsOutOfLimit"));
        } else {
            Lobby.add(newRoom);
            commandSender.sendMessage(R.getLang("createRoomSuccessful"));

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
            roomInfoList.add(room.getId() + " " + room.getName() + " " + room.getPlayers().size() + "/" + room.getSlot() + " " + room.getStatus().toString());
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
        int roomIdInt;
        Room targetRoom = null;

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " join <" + R.getLang("roomId") + ">");
            return;
        }
        roomId = args[1];

        if (!Util.isInteger(roomId)) {
            commandSender.sendMessage(R.getLang("noSuchRoom"));
            return;
        }
        roomIdInt = Integer.parseInt(roomId);

        for (Room room : Lobby.getRoomList()) {
            if (room.getId() == roomIdInt) {
                targetRoom = room;
            }
            for (Player player : room.getPlayers()) {
                if (player == commandSender) {
                    commandSender.sendMessage(R.getLang("alreadyInARoom"));
                    return;
                }
            }
        }

        if (targetRoom == null) {
            commandSender.sendMessage(R.getLang("noSuchRoom"));
        } else {
            if (targetRoom.isFull()) {
                commandSender.sendMessage(R.getLang("roomIsFull"));
            } else {
                targetRoom.add((Player) commandSender);
                commandSender.sendMessage(R.getLang("joinRoom").replace("{$roomId}", Integer.valueOf(targetRoom.getId()).toString()));
            }
        }
    }

    //退出房间 /ek exit
    private void commandExit(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom = null;

        for (Room room : Lobby.getRoomList()) {
            for (Player player : room.getPlayers()) {
                if (player == commandSender) {
                    locatedRoom = room;
                    break;
                }
            }
        }

        if (locatedRoom == null) {
            commandSender.sendMessage(R.getLang("notInARoom"));
        } else {
            if (locatedRoom.remove((Player) commandSender)) {
                commandSender.sendMessage(R.getLang("exitRoomSuccessful"));
            } else {
                commandSender.sendMessage(R.getLang("cannotExitInGame"));
            }
        }
    }

    //删除房间 /ek del <roomId>
    private void commandDel(CommandSender commandSender, Command command, String label, String[] args) {
        String roomId;
        int roomIdInt;
        Room targetRoom = null;

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " /" + label + " del <" + R.getLang("roomId") + ">");
            return;
        }
        roomId = args[1];

        if (!Util.isInteger(roomId)) {
            commandSender.sendMessage(R.getLang("noSuchRoom"));
            return;
        }
        roomIdInt = Integer.parseInt(roomId);

        for (Room room : Lobby.getRoomList()) {
            if (room.getId() == roomIdInt) {
                targetRoom = room;
                break;
            }
        }

        if (targetRoom == null) {
            commandSender.sendMessage(R.getLang("noSuchRoom"));
        } else {
            if (targetRoom.getStatus() != RoomStatus.waitingForStart) {
                commandSender.sendMessage(R.getLang("cannotDelRoomWhichIsInGame"));
            } else {
                Lobby.remove(targetRoom);
                commandSender.sendMessage(R.getLang("delRoomSuccessful"));
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
            }
        }

        //无效的参数,输入 /ek help 查看帮助
        commandSender.sendMessage(R.getLang("invalidCommand").replace("{$help}", "/" + label + " " + "help"));

        return true;
    }
}
