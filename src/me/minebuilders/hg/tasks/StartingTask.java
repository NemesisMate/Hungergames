package me.minebuilders.hg.tasks;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.Util;

public class StartingTask implements Runnable {

	private int timer;
	private Game game;

	public StartingTask(Game g) {
		this.timer = 30;
		this.game = g;
		Util.broadcast("&b&l Arena " + g.getName() + " will begin in 30 seconds!");
		Util.broadcast("&b&l Use:&3&l /hg join " + g.getName() + "&b&l to join!");
	}

	@Override
	public void run() {
		timer = (timer - 5);

		if (timer <= 0) {
			game.startFreeRoam();
			game.cancelStartTask();
		} else {
			game.msgAll("The game will start in " + timer + " seconds..");
		}
	}
}
