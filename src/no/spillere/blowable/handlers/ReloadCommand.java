package no.spillere.blowable.handlers;

import no.spillere.blowable.BlowablePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player) || sender.hasPermission("blowable.reload")) {

            if (args.length == 1) {

                BlowablePlugin.instance.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config successfully reloaded!");

            } else {

                sender.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.YELLOW + "/blowable reload");

            }

        } else {

            sender.sendMessage(ChatColor.RED + "You don't have permission to this!");

        }

        return true;
    }

}
