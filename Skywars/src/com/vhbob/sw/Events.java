package com.vhbob.sw;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener {

	public Main main;

	public Events(Main m) {
		main = m;
	}

	@EventHandler
	public void onLeave(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().toLowerCase().contains("spawn")) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			Location l = (Location) Main.getMain().getConfig().get("lobby");
			p.teleport(l);
			p.sendMessage(
					ChatColor.translateAlternateColorCodes('&', Main.getMain().getConfig().getString("lobby-tp")));
			for (Arena a : Main.getMain().getArenas()) {
				if (a.getPlayers().contains(p)) {
					a.removePlayer(p);
					for (Player player : a.getPlayers()) {
						player.sendMessage(ChatColor.DARK_RED + player.getName() + " quit!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onClickSign(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getType().toString().contains("SIGN")) {
				Sign s = (Sign) e.getClickedBlock().getState();
				if (s.getLine(1) != null) {
					String arenaName = ChatColor.stripColor(s.getLine(1));
					boolean found = false;
					Arena arena = null;
					Player p = e.getPlayer();
					for (Arena looking : main.arenas) {
						if (looking.getName().equals(arenaName)) {
							found = true;
							arena = looking;
							break;
						}
					}
					if (found && arena != null) {
						if (main.getConfig().contains("rooms." + arena.getName())
								&& main.getConfig().contains("arenas." + arena.getName() + ".spawns")) {
							if (!arena.isRunning()) {
								if (arena.getPlayers().size() < arena.size()) {
									arena.addPlayer(p);
									if (arena.getPlayers().size() >= main.getConfig().getInt("min-players")
											&& !arena.isStarting()) {
										arena.start();
										Bukkit.getScheduler().runTaskLater(main, new Runnable() {
											@Override
											public void run() {
												main.updateSigns();
											}
										}, 120);
									}
								} else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&',
											main.getConfig().getString("arena-full")));
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										main.getConfig().getString("already-running")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									main.getConfig().getString("setup-error")));
						}
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',
								main.getConfig().getString("missing-arena")));
					}
				}
			}
		}
	}

	@EventHandler
	public void leaveArena(PlayerInteractEvent e) {
		if (e.getPlayer().getInventory().getItemInMainHand() != null) {
			if (e.getPlayer().getInventory().getItemInMainHand().hasItemMeta() && ChatColor
					.stripColor(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())
					.equalsIgnoreCase("Return to lobby")) {
				for (Arena a : main.arenas) {
					if (a.getPlayers().contains(e.getPlayer())) {
						a.removePlayer(e.getPlayer());
						for (Player player : a.getPlayers()) {
							player.sendMessage(ChatColor.DARK_RED + e.getPlayer().getName() + " quit!");
						}
						e.getPlayer().sendMessage(
								ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("leave-game")));
					}
				}
				main.updateSigns();
			} else if (e.getPlayer().getInventory().getItemInMainHand().hasItemMeta() && ChatColor
					.stripColor(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName())
					.equalsIgnoreCase("Select a kit")) {
				main.openKitGUI(e.getPlayer());
			}
		}
	}

	@EventHandler
	public void onDeath(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (e.getDamage() >= p.getHealth()) {
				for (Arena a : Main.getMain().arenas) {
					if (!a.getPlayers().isEmpty() && a.getPlayers().contains(p)) {
						a.death(p);
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		for (Arena a : main.getArenas()) {
			if (a.getPlayers().contains(p) && a.isRunning()) {
				a.blockModified(e.getBlock(), true);
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		for (Arena a : main.getArenas()) {
			if (a.getPlayers().contains(p) && a.isRunning()) {
				a.blockModified(e.getBlock(), false);
			}
		}
		if (e.getBlock().getType().toString().contains("SIGN")) {
			Sign s = (Sign) e.getBlock().getState();
			for (Arena a : main.signs.keySet()) {
				if (main.signs.get(a).contains(s)) {
					if (e.getPlayer().hasPermission("sw.break.sign") || e.getPlayer().isOp()) {
						main.signs.get(a).remove(s);
						e.getPlayer().sendMessage(ChatColor.DARK_RED + "You broke an arena sign!");
					} else {
						e.getPlayer()
								.sendMessage(ChatColor.DARK_RED + "Error: You do not have permission to break that!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onIgnite(BlockIgniteEvent e) {
		Player p = e.getPlayer();
		for (Arena a : main.getArenas()) {
			if (a.getPlayers().contains(p) && a.isRunning()) {
				a.blockModified(e.getBlock(), false);
			}
		}
	}

	@EventHandler
	public void onBucketUse(PlayerBucketEmptyEvent e) {
		Player p = e.getPlayer();
		for (Arena a : main.getArenas()) {
			if (a.getPlayers().contains(p) && a.isRunning()) {
				a.blockModified(e.getBlockClicked().getRelative(e.getBlockFace()), true);
			}
		}
	}

	@EventHandler
	public void onSpread(BlockFromToEvent e) {
		if (e.getBlock().getType().equals(Material.WATER)) {
			Block b = e.getBlock();
			Block source = null;
			if (b.getRelative(BlockFace.DOWN).getType() == Material.WATER) {
				source = b.getRelative(BlockFace.DOWN);
			} else if (b.getRelative(BlockFace.UP).getType() == Material.WATER) {
				source = b.getRelative(BlockFace.UP);
			} else if (b.getRelative(BlockFace.EAST).getType() == Material.WATER) {
				source = b.getRelative(BlockFace.EAST);
			} else if (b.getRelative(BlockFace.WEST).getType() == Material.WATER) {
				source = b.getRelative(BlockFace.WEST);
			} else if (b.getRelative(BlockFace.NORTH).getType() == Material.WATER) {
				source = b.getRelative(BlockFace.NORTH);
			} else if (b.getRelative(BlockFace.SOUTH).getType() == Material.WATER) {
				source = b.getRelative(BlockFace.SOUTH);
			}
			if (source != null) {
				for (Arena a : main.getArenas()) {
					if (a.getReset().contains(source.getLocation())) {
						a.blockModified(e.getBlock(), true);
					}
					if (!a.isRunning()) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onSpawn(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			for (Arena a : main.getArenas()) {
				if (a.getPlayers().contains(p) && p.getInventory().getItemInMainHand() != null) {
					if (p.getInventory().getItemInMainHand().getType().toString().contains("SPAWN_EGG")) {
						@SuppressWarnings("deprecation")
						Entity en = p.getWorld().spawnEntity(p.getTargetBlockExact(7).getLocation().add(0, 1, 0),
								EntityType.fromName(p.getInventory().getItemInMainHand().getType().toString()
										.replace("SPAWN_EGG", "")));
						a.logSpawn(en);
						p.getInventory().getItemInMainHand()
								.setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEggSpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == SpawnReason.EGG) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void rewardKill(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			if (e.getEntity() instanceof Player) {
				Player damaged = (Player) e.getEntity();
				for (Arena a : Main.getMain().getArenas()) {
					if (a.getPlayers().contains(damaged)) {
						a.damage(damaged, damager);
						break;
					}
				}

			}
		}
	}

	@EventHandler
	public void rewardShot(EntityDamageByEntityEvent e) {
		if (e.getEntityType().equals(EntityType.ARROW)) {
			Arrow a = (Arrow) e.getEntity();
			Player damager = (Player) a.getShooter();
			if (e.getEntity() instanceof Player) {
				Player damaged = (Player) e.getEntity();
				for (Arena ar : Main.getMain().getArenas()) {
					if (ar.getPlayers().contains(damaged)) {
						ar.damage(damaged, damager);
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onDC(PlayerQuitEvent e) {
		for (Arena a : Main.getMain().getArenas()) {
			if (a.getPlayers().contains(e.getPlayer())) {
				a.removePlayer(e.getPlayer());
				for (Player player : a.getPlayers()) {
					player.sendMessage(ChatColor.DARK_RED + e.getPlayer().getName() + " quit!");
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		if (Main.getMain().kits.isEmpty() || !Main.getMain().kits.keySet().contains(e.getPlayer())) {
			if (Main.getMain().getConfig().contains("default-kit")) {
				Main.getMain().kits.put(e.getPlayer(), Main.getMain().getConfig().getString("default-kit"));
			}
		}
	}

	@EventHandler
	public void selectKit(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase(
				ChatColor.translateAlternateColorCodes('&', Main.getMain().getConfig().getString("kit-gui-title")))) {
			e.setCancelled(true);
			if (e.getWhoClicked() instanceof Player && e.getCurrentItem() != null
					&& !e.getCurrentItem().getType().equals(Material.AIR) && e.getCurrentItem().hasItemMeta()) {
				String kit = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
				if (main.getConfig().contains("kits." + kit)) {
					if (e.getAction() == InventoryAction.PICKUP_ALL) {
						if (e.getWhoClicked().hasPermission("kit." + kit.toLowerCase()) || e.getWhoClicked().isOp()) {
							if (main.kits.containsKey(e.getWhoClicked())) {
								main.kits.remove((Player) e.getWhoClicked());
							}
							main.kits.put((Player) e.getWhoClicked(), kit);
							e.getWhoClicked()
									.sendMessage(ChatColor.GREEN + "Selected the "
											+ e.getCurrentItem().getItemMeta().getDisplayName() + ChatColor.RESET + ""
											+ ChatColor.GREEN + " kit");
						} else {
							e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&',
									main.getConfig().getString("no-perm-kit")));
						}
						e.getWhoClicked().closeInventory();
					} else {
						Inventory i = Bukkit.createInventory(null, 54, ChatColor.GREEN + "" + ChatColor.BOLD + kit);
						if (Main.getMain().getConfig().get("kits." + kit) != null) {
							@SuppressWarnings("unchecked")
							ArrayList<ItemStack> items = (ArrayList<ItemStack>) Main.getMain().getConfig()
									.get("kits." + kit);
							for (ItemStack item : items) {
								i.addItem(item);
							}
						}
						ItemStack back = new ItemStack(Material.ARROW);
						ItemMeta backMeta = back.getItemMeta();
						backMeta.setDisplayName(ChatColor.BOLD + "Back");
						ArrayList<String> lore = new ArrayList<String>();
						lore.add(ChatColor.WHITE + "return to the kit selector");
						backMeta.setLore(lore);
						back.setItemMeta(backMeta);
						i.setItem(45, back);
						e.getWhoClicked().openInventory(i);
					}
				} else {
					e.getWhoClicked().sendMessage(
							ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("kit-not-found")));
				}
			}
		} else {
			if (main.getConfig().getConfigurationSection("kits") != null) {
				for (String kit : main.getConfig().getConfigurationSection("kits").getKeys(false)) {
					if (ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase(kit)) {
						if ((e.getWhoClicked() instanceof Player && e.getCurrentItem() != null
								&& !e.getCurrentItem().getType().equals(Material.AIR)
								&& e.getCurrentItem().hasItemMeta())) {
							if (e.getCurrentItem().getItemMeta().getDisplayName()
									.equalsIgnoreCase(ChatColor.BOLD + "Back")) {
								Main.getMain().openKitGUI((Player) e.getWhoClicked());
							}
						}
						e.setCancelled(true);
					}
				}
			}
		}
	}

}
