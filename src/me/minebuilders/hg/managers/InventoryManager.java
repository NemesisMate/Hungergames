package me.minebuilders.hg.managers;

import java.util.HashMap;

import java.util.Map;

import me.minebuilders.hg.Util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

public class InventoryManager {
	
	
	public class PlayerData {
		ItemStack[] inv;
		ItemStack[] equip;
		//int xp; // Not working well, essentials have a good way, could copy
		Scoreboard sb;
		
		public PlayerData(Player player){
			inv = player.getInventory().getContents();
			equip = player.getInventory().getArmorContents();
			sb = player.getScoreboard();
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
	}

	
	public Map<String, PlayerData> dataMap = new HashMap<>();
	
	/*public Map<String, ItemStack[]> inv = new HashMap<String, ItemStack[]>();
	public Map<String, ItemStack[]> armorMap = new HashMap<String, ItemStack[]>();*/

	public void saveinv(Player player) {
		/*inv.put(player.getName(), player.getInventory().getContents());
		saveArm(player);*/
		saveData(player);
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
