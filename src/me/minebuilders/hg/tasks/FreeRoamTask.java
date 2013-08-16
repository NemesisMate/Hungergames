package me.minebuilders.hg.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.Util;

public class FreeRoamTask implements Runnable {

	private Game game;

	public FreeRoamTask(Game g) {
		this.game = g;
		for (String s : g.getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				Util.scm(p,"&4[]---------[ &6&lThe game has started! &4]---------[]"); 
				Util.scm(p," &e You have " + g.getRoamTime() + " seconds to roam without taking damage!"); 
				p.setHealth(20);
				p.setFoodLevel(20);
				g.unFreeze(p);
			}
		}
	}

	@Override
	public void run() {
		game.msgAll("&c&lFree-Roam is over, PVP is now enabled!");
		game.startGame();
	}

}
