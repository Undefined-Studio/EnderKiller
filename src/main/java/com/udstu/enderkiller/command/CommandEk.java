package com.udstu.enderkiller.command;

import com.udstu.enderkiller.Config;
import com.udstu.enderkiller.R;
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
    //输出help菜单
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

    //重载配置文件
    private boolean commandReload(CommandSender commandSender, Command command, String label, String[] args) {
        if (Config.reload()) {
            commandSender.sendMessage(R.getLang("reloadedConfiguration"));
        } else {
            commandSender.sendMessage("An error occurred while loading configuration");
        }

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
            }
        }

        //无效的参数,输入 /ek help 查看帮助
        commandSender.sendMessage(R.getLang("invalidCommand").replace("{$help}", "/" + label + " " + "help"));

        return true;
    }
}
