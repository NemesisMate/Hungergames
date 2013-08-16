package me.minebuilders.hg;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Util {

	private static final Logger log = Logger.getLogger("Minecraft");

	public static void log(String s) { log.info("[HungerGames] " + s); }
	
	public static void warning(String s) { log.warning("[HungerGames] " + s); }

	public static boolean hp(Player p, String s) {
		if (p.hasPermission("hg." + s)) {
			return true;
		}
		return false;
	}

	public static void msg(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.DARK_RED + "[" + ChatColor.DARK_AQUA + "HungerGames" +ChatColor.DARK_RED +"] " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', s)); 
	}
	
	public static void scm(CommandSender sender, String s) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)); 
	}

	public static void broadcast(String s) { 
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "[" + ChatColor.DARK_AQUA + "HungerGames" +ChatColor.DARK_RED +"] " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', s)); 
	}

	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) { return false; }
		return true;
	}

	public static BlockFace getSignFace(int face) {
		switch(face) {
		case 2: return BlockFace.WEST;
		case 4: return BlockFace.SOUTH;
		case 3: return BlockFace.EAST;
		case 5: return BlockFace.NORTH;
		default:
			return BlockFace.WEST;
		}
	}

	@SuppressWarnings("deprecation")
	public static void clearInv(Player p) {
		p.getInventory().clear();
		p.getEquipment().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.updateInventory();
	}
}
