package com.udstu.enderkiller.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by czp on 16-8-4.
 */
public class CommandEK implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Bukkit.broadcastMessage("HelloWorld");
        return true;
    }
}
