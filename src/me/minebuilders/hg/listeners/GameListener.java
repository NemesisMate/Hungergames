package me.minebuilders.hg.listeners;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;
import me.minebuilders.hg.Status;
import me.minebuilders.hg.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class GameListener implements Listener {

	private HG plugin;
	private String tsn = ChatColor.GOLD + "TrackingStick " + ChatColor.GREEN + "Uses: ";
	private ItemStack trackingStick;

	public GameListener(HG plugin) {
		this.plugin = plugin;
		ItemStack it = new ItemStack(Material.STICK, 1);
		ItemMeta im = it.getItemMeta();
		im.setDisplayName(tsn + plugin.getConfig().getInt("settings.trackingstick-uses"));
		it.setItemMeta(im);
		trackingStick = it;
	}

	public void dropInv(Player p) {
		PlayerInventory inv = p.getInventory();
		Location l = p.getLocation();
		for (ItemStack i : inv.getContents()) {
			if (i != null && i.getType() != Material.AIR)
				l.getWorld().dropItemNaturally(l, i);
		}
		for (ItemStack i : inv.getArmorContents()) {
			if (i != null && i.getType() != Material.AIR)
				l.getWorld().dropItemNaturally(l, i);
		}
	}

	public void checkStick(Game g) {
		if (plugin.getConfig().getInt("settings.players-for-trackingstick") == g.getPlayers().size()) {
			for (String r : g.getPlayers()) {
				Player p = Bukkit.getPlayer(r);
				if (p != null) {
					Util.scm(p,"&a&l[]------------------------------------------[]");
					Util.scm(p, "&a&l |&3&l   You have been given a player-tracking stick! &a&l |");
					Util.scm(p, "&a&l |&3&l   Swing the stick to track players!                &a&l |");
					Util.scm(p,"&a&l[]------------------------------------------[]");
					p.getInventory().addItem(trackingStick);
				}
			}
		}
	}

	@EventHandler
	public void onSprint(FoodLevelChangeEvent event) {
		Player p = (Player)event.getEntity();
		if (plugin.players.containsKey(p.getName())) {
			Status st = plugin.players.get(p.getName()).getStatus();
			if (st == Status.WAITING || st == Status.COUNTDOWN) {
			event.setFoodLevel(1);
			event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = false)
	public void onDamage(EntityDamageEvent event) {
		Entity defender = event.getEntity();
		DamageCause damager = event.getCause();

		if (defender instanceof Player && (event.getDamage() + 2) >= ((Player) defender).getHealth() && !damager.equals(DamageCause.ENTITY_ATTACK) && !damager.equals(DamageCause.PROJECTILE)) {
			Player d = (Player)defender;
			if (plugin.players.containsKey(d.getName())) {
				Game g = plugin.players.get(d.getName());
				if (g.getStatus() != Status.RUNNING) {
					event.setCancelled(true);
					d.setHealth(20);
				} else {
				dropInv(d);
				event.setCancelled(true);
				g.msgDef("&d" + HG.killmanager.getDeathString(damager, d.getName()));
				g.leave(d);
				checkStick(g);
			}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void useTrackStick(Player p) {
		ItemStack i = p.getItemInHand();
		ItemMeta im = i.getItemMeta();
		if (im.getDisplayName() != null && im.getDisplayName().startsWith(tsn)) {
			int uses = 0;
			uses = Integer.parseInt(im.getDisplayName().replace(tsn, ""));
			if (uses == 0) {
				p.sendMessage(ChatColor.RED + "This trackingstick is out of uses!");
			} else {
				boolean foundno = true;
				for (Entity e : p.getNearbyEntities(120, 50, 120)) {
					if (e instanceof Player) {
						im.setDisplayName(tsn + (uses -1));
						foundno = false;
						Location l = e.getLocation();
						int range = (int) p.getLocation().distance(l);
						Util.msg(p, ("&3" + ((Player)e).getName()) + "&b is " + range + " blocks away from you:&3 " + getDirection(p.getLocation().getBlock(), l.getBlock()));
						i.setItemMeta(im);
						p.updateInventory();
						return;
					} 
				}
				if (foundno)
					Util.msg(p, ChatColor.RED + "Couldn't locate any nearby players!");

			}
		}
	}

	public String getDirection(Block block, Block block1) {
		Vector bv = block.getLocation().toVector();
		Vector bv2 = block1.getLocation().toVector();
		float y = (float) angle(bv.getX(), bv.getZ(), bv2.getX(), bv2.getZ());
		float cal = (y * 10);
		int c = (int) cal;
		if (c<=1 && c>=-1) {
			return "South";
		} else if (c>-14 && c<-1) {
			return "SouthWest";
		} else if (c>=-17 && c<=-14) {
			return "West";
		} else if (c>-29 && c<-17) {
			return "NorthWest";
		} else if (c>17 && c<29) {
			return "NorthEast";
		} else if (c<=17 && c>=14) {
			return "East";
		} else if (c>1 && c<14) {
			return "SouthEast";
		}  else if (c<=29 && c>=-29) {
			return "North";
		} else {
			return "UnKnown";
		}
	}


	public double angle(double d, double e, double f, double g) {
		//Vector differences
		int x = (int) (f - d);
		int z = (int) (g - e);

		double yaw = Math.atan2(x, z);
		return yaw;
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = false)
	public void onAttack(EntityDamageByEntityEvent event) {
		Entity defender = event.getEntity();
		Entity damager = event.getDamager();
		if (damager instanceof Arrow)
			damager = ((Arrow)damager).getShooter();
		if (defender instanceof Player) {
			Player p = (Player)defender;
			if (plugin.players.containsKey(p.getName())) {
				Game g = plugin.players.get(p.getName());

				if (g.getStatus() != Status.RUNNING) {
					event.setCancelled(true);
				} else if (event.getDamage() >= p.getHealth()) {
					dropInv(p);
					g.msgDef("&l&d" + HG.killmanager.getKillString(p.getName(), damager));
					g.leave(p);
					event.setCancelled(true);
					checkStick(g);
				} else if (event.isCancelled()) event.setCancelled(false);
			}
		}
	}
	
	@EventHandler
	public void onItemUseAttempt(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction() != Action.PHYSICAL && plugin.players.containsKey(p.getName())) {
			Status st = plugin.players.get(p.getName()).getStatus();
			if (st == Status.WAITING || st == Status.COUNTDOWN) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You cannot interact until the game has started!");
			}
		}
	}

	@EventHandler
	public void onPlayerClickLobby(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = event.getClickedBlock();
			if (b.getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) b.getState();
				if (sign.getLine(0).equals(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "HungerGames")) {
					Game game = HG.manager.getGame(sign.getLine(1).substring(2));
					if (game == null) {
						Util.msg(p, ChatColor.RED + "This arena does not exist!");
						return;
					} else {
						if (p.getItemInHand().getType() == Material.AIR) {
							game.join(p);
						} else {
							Util.msg(p, ChatColor.RED + "Click the sign with your hand!");
						}
					}
				} 
			}
		} else if (event.getAction().equals(Action.LEFT_CLICK_AIR)) {
			if (p.getItemInHand().getType().equals(Material.STICK) && plugin.players.containsKey(p.getName())) {
				useTrackStick(p);
			}
		}
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (HG.manager.isInRegion(b.getLocation())) {
			if (plugin.getConfig().getBoolean("rollback.allow-block-break") && plugin.players.containsKey(p.getName())) {
				Game g = plugin.players.get(p.getName());
				if (g.getStatus() == Status.RUNNING) {
					if (!plugin.blocks.contains(b.getType())) {
						p.sendMessage(ChatColor.RED + "You cannot edit this block type!");
						event.setCancelled(true);
						return;
					} else {
						g.recordBlockPlace(event.getBlockReplacedState());
						return;
					}
				} else {
					p.sendMessage(ChatColor.RED + "The game is not running!");
					event.setCancelled(true);
					return;
				}
			} else if (p.hasPermission("hg.create") && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING) {
				if (b.getType() == Material.CHEST) {
					HG.manager.getGame(b.getLocation()).addChests(b.getLocation());
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (HG.manager.isInRegion(b.getLocation())) {
			if (plugin.getConfig().getBoolean("rollback.allow-block-break") && plugin.players.containsKey(p.getName())) {
				Game g = plugin.players.get(p.getName());
				if (g.getStatus() == Status.RUNNING) {
					if (!plugin.blocks.contains(b.getType())) {
						p.sendMessage(ChatColor.RED + "You cannot edit this block type!");
						event.setCancelled(true);
						return;
					} else {
						g.recordBlockBreak(b);
						return;
					}
				} else {
					p.sendMessage(ChatColor.RED + "The game is not running!");
					event.setCancelled(true);
					return;
				}
			} else if (p.hasPermission("hg.create") && HG.manager.getGame(b.getLocation()).getStatus() != Status.RUNNING) {
				return;
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if (plugin.players.containsKey(p.getName()) && plugin.players.get(p.getName()).getStatus() == Status.WAITING) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onlogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.players.containsKey(player.getName())) {
			plugin.players.get(player.getName()).leave(player);
		}
	}

	@EventHandler
	public void onkick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		if (plugin.players.containsKey(player.getName())) {
			plugin.players.get(player.getName()).leave(player);
		}
	}
}
