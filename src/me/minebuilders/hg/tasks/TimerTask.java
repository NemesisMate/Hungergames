package me.minebuilders.hg.tasks;

import org.bukkit.ChatColor;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;

public class TimerTask implements Runnable {

	private int remainingtime;
	private Game game;

	public TimerTask(Game g, int time) {
		this.remainingtime = time;
		this.game = g;
	}
	@Override
	public void run() {
		remainingtime = (remainingtime - 30);

		// 4 must be add to config arena config
		if (((g.getStatus() != Status.RUNNING && g.getPlayers().size() < 4) || remainingtime == 30) && HG.plugin.getConfig().getBoolean("settings.teleport-at-end")) {
			game.msgAll("&l&cThe game is almost over, fight to the death!");
			game.respawnAll();
		} else if (this.remainingtime < 10) {
			game.stop();
		} else {
			int minutes = this.remainingtime / 60;
			int asd = Integer.valueOf(this.remainingtime % 60);
			if (minutes != 0) game.msgAll(ChatColor.GREEN+"The game is ending in " + minutes + (asd == 0?" minute(s)!":" minute(s), and " + asd+" seconds!"));
			else game.msgAll(ChatColor.GREEN+"The game is ending in " + this.remainingtime +" seconds!");
		}
	}
}
