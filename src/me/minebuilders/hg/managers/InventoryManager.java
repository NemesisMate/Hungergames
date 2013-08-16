package me.minebuilders.hg.managers;

import java.util.HashMap;

import java.util.Map;

import me.minebuilders.hg.Util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
@SuppressWarnings("deprecation")
public class InventoryManager {
	
	public Map<String, ItemStack[]> inv = new HashMap<String, ItemStack[]>();
	public Map<String, ItemStack[]> armorMap = new HashMap<String, ItemStack[]>();

	public void saveinv(Player player) {
		inv.put(player.getName(), player.getInventory().getContents());
		saveArm(player);
		Util.clearInv(player);
	}

	public void restoreinv(Player player) {
		Util.clearInv(player);
		if(inv.containsKey(player.getName())) {
			player.getInventory().setContents(inv.get(player.getName()));
			inv.remove(player);
		}
		if(armorMap.containsKey(player.getName()))
			restoreArm(player);
		player.updateInventory();
	}

	public void saveArm(Player player) {
		armorMap.put(player.getName(), player.getInventory().getArmorContents());
	}

	public void restoreArm(Player player) {
		player.getInventory().setHelmet(new ItemStack(0));
		player.getInventory().setChestplate(new ItemStack(0));
		player.getInventory().setLeggings(new ItemStack(0));
		player.getInventory().setBoots(new ItemStack(0));
		player.getInventory().setArmorContents(armorMap.get(player.getName()));
		armorMap.remove(player);
	}
}
