package me.minebuilders.hg.listeners;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.commands.BaseCmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandListener implements CommandExecutor {
	
	private final HG p;

	public CommandListener(HG plugin) {
		this.p = plugin;
	}

	public boolean onCommand(CommandSender s, Command command, String label, String[] args) {
		if (args.length == 0 || !p.cmds.containsKey(args[0])) {
			s.sendMessage(ChatColor.DARK_AQUA + "-----------(" + ChatColor.AQUA + ChatColor.BOLD + "Your HungerGames Commands" + ChatColor.DARK_AQUA + ")-----------");
			for (BaseCmd cmd : p.cmds.values().toArray(new BaseCmd[0])) {
				if (s.hasPermission("hg." + cmd.cmdName)) s.sendMessage(ChatColor.DARK_RED + "  - " + cmd.sendHelpLine());
			}
			s.sendMessage(ChatColor.DARK_AQUA + "----------------------------------------------------");
		} else p.cmds.get(args[0]).processCmd(p, s, args);
		return true;
	}
}