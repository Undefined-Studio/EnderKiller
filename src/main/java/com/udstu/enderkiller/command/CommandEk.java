package com.udstu.enderkiller.command;

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
    private boolean commandHelp(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> helpList = new ArrayList<>();
        Pattern pattern = Pattern.compile("^command.*");
        Method[] methods = this.getClass().getDeclaredMethods();
        String methodName;
        Matcher matcher;

        //获取所有指令名并得到对应文本
        for (Method method : methods) {
            methodName = method.getName();
            matcher = pattern.matcher(methodName);
            if (matcher.matches()) {
                helpList.add("/"+label+" "+methodName.substring(7).toLowerCase()+" "+R.getLang(methodName));
            }
        }

        commandSender.sendMessage(helpList.toArray(new String[helpList.size()]));

        return true;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        //根据参数长度分类
        switch (args.length) {
            case 0: {
                return commandHelp(commandSender, command, label, args);
            }
            case 1: {
                switch (args[0]) {
                    case "help":{
                        return commandHelp(commandSender, command, label, args);
                    }
                }
            }
            default:{
                //无效的参数,输入 /ek help 查看帮助
                commandSender.sendMessage(R.getLang("invalidCommand").replace("{$help}","/"+label+" "+"help"));
                return true;
            }
        }
    }
}
