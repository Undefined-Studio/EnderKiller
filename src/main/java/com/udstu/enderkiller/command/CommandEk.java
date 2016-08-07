package com.udstu.enderkiller.command;

import com.udstu.enderkiller.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
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

        commandSender.sendMessage(helpList.toArray(new String[helpList.size()]));
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

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " " + "/" + label + " create <" + R.getLang("roomName") + "> <" + R.getLang("mode") + ">");
            return;
        }
        mode = args[1];

        if (!Util.isInteger(mode) || Integer.parseInt(mode) != 8) {
            commandSender.sendMessage(R.getLang("modeAllowed") + ": " + "8");
            return;
        }
        modeInt = Integer.parseInt(mode);

        newRoom = new Room(commandSender.getName(), modeInt);
        if (!Lobby.add(newRoom)) {
            commandSender.sendMessage(R.getLang("numberOfRoomsOutOfLimit"));
            return;
        }

        //若命令来源是玩家则在房间创建完毕后加入房间
        if (Player.class.isInstance(commandSender)) {
            commandJoin(commandSender, command, label, new String[]{"join", Integer.valueOf(newRoom.getId()).toString()});
        }
    }

    //列出房间 /ek list
    private void commandList(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> roomInfoList = new ArrayList<>();

        for (Room room : Lobby.getRoomList()) {
            roomInfoList.add(room.getId() + " " + room.getName() + " " + room.getPlayers().size() + "/" + room.getSlot() + " " + room.getStatus().toString());
        }

        commandSender.sendMessage(roomInfoList.toArray(new String[roomInfoList.size()]));
    }

    //加入房间 /ek join <roomId>
    private void commandJoin(CommandSender commandSender, Command command, String label, String[] args) {
        String roomId;
        int roomIdInt;
        Room targetRoom = null;

        if (args.length < 2) {
            commandSender.sendMessage(R.getLang("usage") + " " + "/" + label + " join <" + R.getLang("roomId") + ">");
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
        } else if (!targetRoom.add((Player) commandSender)) {
            commandSender.sendMessage(R.getLang("roomIsFull"));
        }
    }

    //退出房间 /ek exit
    private void commandExit(CommandSender commandSender, Command command, String label, String[] args) {
        Room locatedRoom = null;

        for (Room room : Lobby.getRoomList()) {
            for (Player player : room.getPlayers()) {
                if (player == commandSender) {
                    locatedRoom = room;
                }
            }
        }

        if (locatedRoom == null) {
            commandSender.sendMessage(R.getLang("notInARoom"));
        } else if (!locatedRoom.remove((Player) commandSender)) {
            commandSender.sendMessage(R.getLang("cannotExitInGame"));
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
            }
        }

        //无效的参数,输入 /ek help 查看帮助
        commandSender.sendMessage(R.getLang("invalidCommand").replace("{$help}", "/" + label + " " + "help"));

        return true;
    }
}
