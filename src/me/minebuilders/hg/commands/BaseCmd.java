package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCmd {

	public CommandSender sender;
	public String[] args;
	public String cmdName;
	public int argLength = 0;
	public String extra;
	public boolean forcePlayer = true;
	public boolean forceInGame = false;
	public boolean forceInRegion = false;
	public String usage;
	public Player player;

	public boolean processCmd(HG p, CommandSender s, String[] arg) {
		sender = s;
		args = arg;

		if (forcePlayer) {
			if (!(s instanceof Player)) return false;
			else player = (Player) s;
		}
		if (!s.hasPermission("hg." + cmdName))
			sender.sendMessage(ChatColor.RED + "You do not have permission to use: " + ChatColor.GOLD + "/hg " + cmdName);
		else if (forceInGame && !HG.plugin.players.containsKey(player.getName()))
			sender.sendMessage(ChatColor.RED + "Your not in a valid game!");
		else if (forceInRegion && !HG.manager.isInRegion(player.getLocation()))
			sender.sendMessage(ChatColor.RED + "Your not in a valid HungerGames region!");
		else if (argLength > arg.length)
			s.sendMessage(ChatColor.RED + "Wrong usage: " + sendArgsLine());
		else return run();
		return true;
	}

	public abstract boolean run();

	public String sendHelpLine() {
		return ChatColor.DARK_AQUA + "/hg " + cmdName + ChatColor.AQUA + " " + usage;
	}

	public String sendArgsLine() {
		return ChatColor.DARK_AQUA + "/hg " + cmdName + " " + usage;
	}
}
