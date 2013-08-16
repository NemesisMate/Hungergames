package me.minebuilders.hg.commands;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;

public class JoinCmd extends BaseCmd {

	public JoinCmd() {
		forcePlayer = true;
		cmdName = "join";
		forceInGame = false;
		argLength = 2;
		extra = "";
		usage = "<arena-name>";
	}

	@Override
	public boolean run() {
		Game g = HG.manager.getGame(args[1]);
		if (g != null && !g.getPlayers().contains(player.getName())) {
			g.join(player);
		} else {
			player.sendMessage("This arena does not exist!");
		}
		return true;
	}
}