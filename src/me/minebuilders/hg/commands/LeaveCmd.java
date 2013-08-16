package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class LeaveCmd extends BaseCmd {

	public LeaveCmd() {
		forcePlayer = true;
		cmdName = "leave";
		forceInGame = true;
		argLength = 1;
		extra = "";
		usage = "";
	}

	@Override
	public boolean run() {
		HG.plugin.players.get(player.getName()).leave(player);
		Util.msg(player, "&cYou left Hungergames!");
		return true;
	}
}