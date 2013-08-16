package me.minebuilders.hg;

import java.util.ArrayList;
import java.util.List;

import me.minebuilders.hg.mobhandler.Spawner;
import me.minebuilders.hg.tasks.FreeRoamTask;
import me.minebuilders.hg.tasks.StartingTask;
import me.minebuilders.hg.tasks.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Game {

	private String name;
	private List<Location> spawns;
	private Bound b;
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<Location> chests = new ArrayList<Location>();
	private ArrayList<BlockState> blocks = new ArrayList<BlockState>();
	private Location exit;
	private Status status;
	private int minplayers;
	private int maxplayers;
	private int time;
	private Sign s;
	private Sign s1;
	private Sign s2;
	private int roamtime;
	private SBDisplay sb;

	// Task ID's here!
	private int startTaskID;
	private int timerTaskID;
	private int mobTaskID;

	public Game(String s, Bound bo, List<Location> spawns, Sign lobbysign, int timer, int minplayers, int maxplayers, int roam, boolean isready) {
		this.name = s;
		this.b = bo;
		this.spawns = spawns;
		this.s = lobbysign;
		this.time = timer;
		this.minplayers = minplayers;
		this.maxplayers = maxplayers;
		this.roamtime = roam;
		this.mobTaskID = -111;
		
		if (isready) 
			status = Status.STOPPED;
		else 
			status = Status.BROKEN;
		setChests();
		setLobbyBlock(lobbysign);
		sb = new SBDisplay(this);
	}

	public Game(String s, Bound c, int timer, int minplayers, int maxplayers, int roam) {
		this.name = s;
		this.time = timer;
		this.minplayers = minplayers;
		this.maxplayers = maxplayers;
		this.roamtime = roam;
		this.spawns = new ArrayList<Location>();
		this.b = c;
		this.mobTaskID = -111;
		status = Status.NOTREADY;
		setChests();
		sb = new SBDisplay(this);
	}

	public void forceRollback() {
		for (BlockState st : blocks) {
			st.update(true);
		}
	}
	
	public void setStatus(Status st) {
		this.status = st; 
		updateLobbyBlock();
	}
	
	public void recordBlockBreak(Block bl) {
		blocks.add(bl.getState());
		if (!bl.getType().isSolid() || !bl.getType().isBlock()) {
			blocks.add(bl.getRelative(BlockFace.UP).getState());
		}
	}
	
	public void recordBlockPlace(BlockState bs) {
		blocks.add(bs);
	}
	
	public Status getStatus() {
		return this.status;
	}

	public ArrayList<BlockState> getBlocks() {
		return blocks;
	}

	public void resetBlocks() {
		this.blocks.clear();
	}

	public void setChests() {
		chests.clear();
		for (Location bl : b.getBlocks(Material.CHEST)) {
			chests.add(bl);
		}
	}

	public void msgAllMulti(String[] sta) {
		for (String s : sta) {
			for (String st : players) {
				Player p = Bukkit.getPlayer(st);
				if (p != null)
					Util.msg(p, s);
			}
		}
	}

	public ArrayList<String> getPlayers() {
		return players;
	}

	public String getName() {
		return this.name;
	}

	public boolean isInRegion(Location l) {
		return b.isInRegion(l);
	}
	
	public List<Location> getSpawns() {
		return spawns;
	}

	public int getRoamTime() {
		return this.roamtime;
	}

	public void cancelStartTask() {
		Bukkit.getScheduler().cancelTask(startTaskID);
	}

	public void cancelTimerTask() {
		Bukkit.getScheduler().cancelTask(timerTaskID);
	}

	public void join(Player p) {
		if (status != Status.WAITING && status != Status.STOPPED && status != Status.COUNTDOWN) {
			p.sendMessage(ChatColor.RED + "This arena is not ready! Please wait before joining!");
		} else if (maxplayers <= players.size()) {
			p.sendMessage(ChatColor.RED + name + " is currently full!");
		} else {
			if (p.isInsideVehicle()) {
				p.leaveVehicle();
			}
			HG.inv.saveinv(p);
			players.add(p.getName());
			HG.plugin.players.put(p.getName(), this);
			p.teleport(pickSpawn());
			heal(p);
			freeze(p);
			if (players.size() >= minplayers && status.equals(Status.WAITING)) {
				startPreGame();
			} else {
				msgDef("&4(&3"+p.getName() + "&b Has joined the game"+(minplayers-players.size()<= 0?"!":": "+(minplayers-players.size())+" players to start!")+"&4)");
			}
			kitHelp(p);
			if (players.size() == 1)
				status = Status.WAITING;
			updateLobbyBlock();
			sb.setSB(p);
			sb.setAlive();
		}
	}

	public void kitHelp(Player p) {
		String kit = HG.plugin.kit.getKitList();
		Util.scm(p, "&8     ");
		Util.scm(p, "&9&l>----------[&b&lWelcome to HungerGames&9&l]----------<");
		Util.scm(p, "&9&l - &bPick a kit using /hg kit <kit-name>");
		Util.scm(p, "&9&lKits:&b" + kit);
		Util.scm(p, "&9&l>------------------------------------------<");
	}
	
	public void respawnAll() {
		for (String st : players) {
			Player p = Bukkit.getPlayer(st);
			if (p != null)
				p.teleport(pickSpawn());
		}
	}

	public void startPreGame() {
		setStatus(Status.COUNTDOWN);
		startTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, new StartingTask(this), 5 * 20L, 5 * 20L);
		updateLobbyBlock();
	}

	public void startFreeRoam() {
		status = Status.BEGINNING;
		HG.manager.restoreChests(this);
		b.removeEntities();
		Bukkit.getScheduler().scheduleSyncDelayedTask(HG.plugin, new FreeRoamTask(this), roamtime * 20L);
		updateLobbyBlock();
	}

	public void startGame() {
		status = Status.RUNNING;
		if (HG.plugin.getConfig().getBoolean("settings.spawn-mobs")) {
			int interval = HG.plugin.getConfig().getInt("settings.spawn-mobs-interval");
			mobTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, new Spawner(this), interval * 20L, interval * 20L);
		}
		timerTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, new TimerTask(this, time), 30 * 20L, 30 * 20L);
		updateLobbyBlock();
	}


	public void addSpawn(Location l) {
		this.spawns.add(l);
	}

	public Location pickSpawn() {
		int spawn = players.size() - 1;
		if (containsPlayer(spawns.get(spawn))) {
			for (Location l : spawns) {
				if (!containsPlayer(l)) {
					return l;
				}
			}
		}
		return spawns.get(spawn);
	}

	public boolean containsPlayer(Location l) {
		if (l == null) {
			return false;
		}
		for (String s : players) {
			Player p = Bukkit.getPlayer(s);
			if (p.getLocation().getBlock().equals(l.getBlock()))
				return true;
		}
		return false;
	}

	public void msgAll(String s) {
		for (String st : players) {
			Player p = Bukkit.getPlayer(st);
			if (p != null)
				Util.msg(p, s);
		}
	}

	public void msgDef(String s) {
		for (String st : players) {
			Player p = Bukkit.getPlayer(st);
			if (p != null)
				Util.scm(p, s);
		}
	}

	public void updateLobbyBlock() {
		s1.setLine(1, status.getName());
		s2.setLine(1, ChatColor.BOLD + "" + players.size() + "/" + maxplayers);
		s1.update(true);
		s2.update(true);
	}

	public void heal(Player p) {
		for (PotionEffect ef : p.getActivePotionEffects()) {
			p.removePotionEffect(ef.getType());
		}
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setFireTicks(0);
	}

	public void freeze(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10));
		p.setWalkSpeed(0.0001F);
		p.setFoodLevel(1);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setGameMode(GameMode.SURVIVAL);
	}

	public void unFreeze(Player p) {
		p.removePotionEffect(PotionEffectType.JUMP);
		p.setWalkSpeed(0.2F);
	}

	public boolean setLobbyBlock(Sign sign) {
		try {
			this.s = sign;
			Block c = s.getBlock();
			BlockFace face = Util.getSignFace(c.getData());
			this.s1 = (Sign) c.getRelative(face).getState();
			this.s2 = (Sign) s1.getBlock().getRelative(face).getState();

			s.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames");
			s.setLine(1, ChatColor.BOLD + name);
			s.setLine(2, ChatColor.BOLD + "Click To Join");
			s1.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Game Status");
			s1.setLine(1, status.getName());
			s2.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "Alive");
			s2.setLine(1, ChatColor.BOLD + "" + 0 + "/" + maxplayers);
			s.update(true);
			s1.update(true);
			s2.update(true);
		} catch (Exception e) { return false; }
		try {
			String[] h = HG.plugin.getConfig().getString("settings.globalexit").split(":");
			this.exit = new Location(Bukkit.getServer().getWorld(h[0]), Integer.parseInt(h[1]) + 0.5, Integer.parseInt(h[2]) + 0.1, Integer.parseInt(h[3]) + 0.5, Float.parseFloat(h[4]), Float.parseFloat(h[5]));
		} catch (Exception e) {
			this.exit = s.getWorld().getSpawnLocation();
		}
		return true;
	}

	public void setExit(Location l) {
		this.exit = l;
	}

	public void stop() {
		String i = "Nobody";
		Bukkit.getScheduler().cancelTask(mobTaskID);
		cancelStartTask();
		cancelTimerTask();
		for (String s : players) {
			HG.plugin.players.remove(s);
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				unFreeze(p);
				heal(p);
				sb.restoreSB(p);
				i = s;
				exit(p);
				HG.inv.restoreinv(p);
			}
		}
		players.clear();
		if (!i.equals("Nobody") && HG.reward) {
			double db = HG.plugin.getConfig().getDouble("reward.cash");
			Vault.economy.depositPlayer(i, db);
			Util.msg(Bukkit.getPlayer(i), "&aYou won " + db + " for winning HungerGames!");
		}
		Util.broadcast("&l&3" + i + " &l&bHas won HungerGames at arena " + name + "!");
		if (!blocks.isEmpty()) {
			new Rollback(this);
		} else {
			status = Status.STOPPED;
			updateLobbyBlock();
		}
		b.removeEntities();
		sb.resetAlive();
	}

	public void leave(Player p) {
		players.remove(p.getName());
		HG.plugin.players.remove(p.getName());
		unFreeze(p);
		heal(p);
		exit(p);
		HG.inv.restoreinv(p);
		if (status == Status.RUNNING || status == Status.BEGINNING) {
			if (players.size() <= 1) {
				stop();
			}
		} else if (status == Status.WAITING) {
			msgDef("&3&l"+p.getName() + "&l&c Has left the game"+(minplayers-players.size()<= 0?"!":": "+(minplayers-players.size())+" players to start!"));
		}
		updateLobbyBlock();
		sb.restoreSB(p);
		sb.setAlive();
	}
	
	public void addChests(Location b) {
		chests.add(b);
	}

	public ArrayList<Location> getChests() {
		return chests;
	}
	
	public void exit(Player p) {
		if (this.exit == null) {
			p.teleport(s.getWorld().getSpawnLocation());
		} else {
			p.teleport(this.exit);
		}
	}
}
