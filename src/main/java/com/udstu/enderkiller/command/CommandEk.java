package com.udstu.enderkiller.command;

import com.udstu.enderkiller.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
    private boolean commandHelp(CommandSender commandSender, Command command, String label, String[] args) {
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

        return true;
    }

    //重载配置文件 /ek reload
    private boolean commandReload(CommandSender commandSender, Command command, String label, String[] args) {
        if (Config.reload()) {
            commandSender.sendMessage(R.getLang("reloadedConfiguration"));
        } else {
            commandSender.sendMessage("An error occurred while loading configuration");
        }

        return true;
    }

    //新建房间 /ek create roomName mode
    private boolean commandCreate(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage(R.getLang("usage") + " " + "/" + label + " create <roomName> <mode>");
            return true;
        }
        if (!Util.isInteger(args[2]) || Integer.parseInt(args[2]) != 8) {
            commandSender.sendMessage(R.getLang("modeAllowed") + ": " + "8");
            return true;
        }

        if (!Lobby.add(new Room(args[1], Integer.parseInt(args[2])))) {
            commandSender.sendMessage(R.getLang("numberOfRoomsOutOfLimit"));
        }

        return true;
    }

    //列出房间 /ek list
    private boolean commandList(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> roomStatus = new ArrayList<>();

        for (Room room : Lobby.getRoomList()) {
            roomStatus.add(room.getId() + "." + room.getName() + " " + room.getPlayers().size() + "/" + room.getSlot());
        }

        commandSender.sendMessage(roomStatus.toArray(new String[roomStatus.size()]));

        return true;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
                case "help": {
                    return commandHelp(commandSender, command, label, args);
                }
                case "reload": {
                    return commandReload(commandSender, command, label, args);
                }
                case "create": {
                    return commandCreate(commandSender, command, label, args);
                }
                case "list": {
                    return commandList(commandSender, command, label, args);
                }
            }
        }

        //无效的参数,输入 /ek help 查看帮助
        commandSender.sendMessage(R.getLang("invalidCommand").replace("{$help}", "/" + label + " " + "help"));

        return true;
    }
}
