package me.minebuilders.hg.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.minebuilders.hg.Bound;
import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Manager {

	private HG plugin;
	private Random rg = new Random();
	private int itemsize = 0;
	
	public Manager(HG p) {
		plugin = p;
		itemsize = p.items.size();
	}
	
	public void runDebugger(CommandSender sender, String s) {
		Configuration arenadat = HG.arenaconfig.getCustomConfig();
		boolean isReady = true;
		List<Location> spawns = new ArrayList<Location>();
		Sign lobbysign = null;
		int timer = 0;
		int minplayers = 0;
		int maxplayers = 0;

		try {
			timer = arenadat.getInt("arenas." + s + ".info." + "timer");
			minplayers = arenadat.getInt("arenas." + s + ".info." + "min-players");
			maxplayers = arenadat.getInt("arenas." + s + ".info." + "max-players");
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load infomation for arena " + s + "!"); 
			isReady = false;
		}

		try {
			lobbysign = (Sign) HG.arenaconfig.getSLoc(arenadat.getString("arenas." + s + "." + "lobbysign")).getBlock().getState();
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load lobbysign for arena " + s + "!"); 
			isReady = false;
		}

		try {
			for (String l : arenadat.getStringList("arenas." + s + "." + "spawns")) {
				spawns.add(HG.arenaconfig.getLocFromString(l));
			}
			int count = arenadat.getStringList("arenas." + s + "." + "spawns").size();
			if (count < maxplayers) {
				Util.scm(sender, "&cYou need to add " + (maxplayers - count) + " more spawns!"); 
				isReady = false;
			}
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load random spawns for arena " + s + "!"); 
			isReady = false;
		}

		try {
			@SuppressWarnings("unused")
			Bound b = new Bound(arenadat.getString("arenas." + s + ".bound." + "world"), HG.arenaconfig.BC(s, "x"), HG.arenaconfig.BC(s, "y"), HG.arenaconfig.BC(s, "z"), HG.arenaconfig.BC(s, "x2"), HG.arenaconfig.BC(s, "y2"), HG.arenaconfig.BC(s, "z2"));
		} catch (Exception e) { 
			Util.scm(sender, "&cUnable to load region bounds for arena " + s + "!"); 
			isReady = false;
		}
		if (isReady) {
			Util.scm(sender,"&2&l---= &a&lYour hungergames arena is ready to run! &2&l=---");
			Util.scm(sender, "&2Spawns:&a " + spawns.size());
			Util.scm(sender, "&2Lobby:&a z:" + lobbysign.getX() +", y:"+ lobbysign.getY() +", z:"+ lobbysign.getZ());
			Util.scm(sender, "&2Timer:&a " + timer);
			Util.scm(sender, "&2MinPlayers:&a " + minplayers);
			Util.scm(sender, "&2MaxPlayers:&a " + maxplayers);
		}
	}

	public void restoreChests(Game arena) {
		int max = plugin.getConfig().getInt("settings.max-chestcontent");
		ArrayList<Location> chests = arena.getChests();
		for (Location l : chests) {
			Block b = l.getBlock();
			if (b.getType().equals(Material.CHEST)) {
				Inventory i = ((InventoryHolder)b.getState()).getInventory();
				i.clear();
				int c = rg.nextInt(max) + 1;
				while (c != 0) {
					ItemStack it = randomitem();
					if (it != null) {
						i.addItem(it);
					}
					c--;
				}
			}
		}
	}

	public ItemStack randomitem() {
		int c = rg.nextInt(itemsize) + 1;
		return plugin.items.get(c);
	}
	
	public boolean isInRegion(Location l) {
		for (Game g : HG.plugin.games) {
			if (g.isInRegion(l))
				return true;
		}
		return false;
	}

	public Game getGame(Location l) {
		for (Game g : HG.plugin.games) {
			if (g.isInRegion(l))
				return g;
		}
		return null;
	}

	public Game getGame(String s) {
		for (Game g : HG.plugin.games) {
			if (g.getName().equalsIgnoreCase(s)) {
				return g;
			}
		}
		return null;
	}
}
