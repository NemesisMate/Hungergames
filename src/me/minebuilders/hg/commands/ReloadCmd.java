package me.minebuilders.hg.commands;

import me.minebuilders.hg.HG;
import me.minebuilders.hg.Util;

public class ReloadCmd extends BaseCmd {

	public ReloadCmd() {
		forcePlayer = true;
		cmdName = "reload";
		argLength = 1;
		extra = "";
		forceInRegion = false;
		usage = "";
	}

	@Override
	public boolean run() {
		Util.msg(player, "&l&aAttempting to for reload Hungergames!");
		HG.plugin.stopAll();
		HG.plugin.reloadConfiguration();
		HG.arenaconfig.saveCustomConfig();
		HG.arenaconfig.reloadCustomConfig();
		HG.arenaconfig.load();
		Util.msg(player, "&6 - &eArenas have been reloaded!");
		HG.plugin.kit.kititems.clear();
		HG.plugin.ism.setkits();
		Util.msg(player, "&6 - &eKits have been reloaded!");
		HG.plugin.items.clear();
		HG.ri.load();
		Util.msg(player, "&6 - &eRandomItems have been reloaded!");
		Util.msg(player, "&l&a- Successfully reloaded HungerGames -");
		return true;
	}
}
