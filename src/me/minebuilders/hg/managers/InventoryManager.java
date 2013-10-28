package me.minebuilders.hg.managers;

import java.util.HashMap;

import java.util.Map;

import me.minebuilders.hg.Util;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

public class InventoryManager {
	
	
	public class PlayerData {
		ItemStack[] inv;
		ItemStack[] equip;
		Scoreboard sb;
		double health;
		int food, level;
		float exp;
		GameMode mode;
		Location loc;
		
		public PlayerData(Player player){
			inv = player.getInventory().getContents();
			equip = player.getInventory().getArmorContents();
			sb = player.getScoreboard();
			health = player.getHealth();
			food = player.getFoodLevel();
			level = player.getLevel();
			exp = player.getExp();
			mode = player.getGameMode();
			loc = player.getLocation();
		}
		
		public ItemStack[] getInv(){
			return inv;
		}
		
		public ItemStack[] getEquip(){
			return equip;
		}
		
		public Scoreboard getSB(){
			return sb;
		}
		
		public double getHealth(){
			return health;
		}
		
		public int getFood(){
			return food;
		}
		
		public int getLevel(){
			return level;
		}
		
		public float getXP(){
			return exp;
		}
		
		public GameMode getMode(){
			return mode;
		}
		
		public Location getLocation(){
			return loc;
		}
	}

	
	public Map<String, PlayerData> dataMap = new HashMap<>();
	
	/*public Map<String, ItemStack[]> inv = new HashMap<String, ItemStack[]>();
	public Map<String, ItemStack[]> armorMap = new HashMap<String, ItemStack[]>();*/

	public void saveinv(Player player) {
		/*inv.put(player.getName(), player.getInventory().getContents());
		saveArm(player);*/
		saveData(player);
		player.setLevel(0);
		player.setExp(0);
		Util.clearInv(player);
	}

	/*public void restoreinv(Player player) {
		Util.clearInv(player);
		if(inv.containsKey(player.getName())) {
			player.getInventory().setContents(inv.get(player.getName()));
			inv.remove(player);
		}
		if(armorMap.containsKey(player.getName()))
			restoreArm(player);
		player.updateInventory();
	}*/
	public void restoreData(Player player) {
		Util.clearInv(player);
		PlayerData playerData = dataMap.get(player.getName());
		if(playerData != null){
			player.getInventory().setContents(playerData.getInv());
			player.getInventory().setArmorContents(playerData.getEquip());
			player.setScoreboard(playerData.getSB());
			dataMap.remove(player.getName());
			player.setHealth(playerData.getHealth());
			player.setFoodLevel(playerData.getFood());
			player.setLevel(playerData.getLevel());
			player.setExp(playerData.getXP());
			player.setGameMode(playerData.getMode());
			player.teleport(playerData.getLocation());
		}
		//player.updateInventory();
	}

	/*public void saveArm(Player player) {
		armorMap.put(player.getName(), player.getInventory().getArmorContents());
	}*/
	public void saveData(Player player){
		dataMap.put(player.getName(), new PlayerData(player));
	}
/*
	public void restoreArm(Player player) {
		player.getInventory().setHelmet(new ItemStack(0));
		player.getInventory().setChestplate(new ItemStack(0));
		player.getInventory().setLeggings(new ItemStack(0));
		player.getInventory().setBoots(new ItemStack(0));
		player.getInventory().setArmorContents(armorMap.get(player.getName()));
		armorMap.remove(player);
	}*/
}
