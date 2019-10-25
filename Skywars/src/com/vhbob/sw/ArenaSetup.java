package com.vhbob.sw;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaSetup implements CommandExecutor {

	public void sendAvailableCommands(Player p) {
		p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Here are your available commands");
		if (p.hasPermission("skywars.add.spawn") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars addSpawn (arena) - force starts the given arena");
		}
		if (p.hasPermission("skywars.force.start") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars forceStart (arena) - force starts the given arena");
		}
		if (p.hasPermission("skywars.force.stop") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars forceStop (arena) - force stops the given arena");
		}
		if (p.hasPermission("skywars.delete.arena") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars deleteArena (arena) - deletes the given arena");
		}
		if (p.hasPermission("skywars.set.waiting.room") || p.isOp()) {
			p.sendMessage(
					ChatColor.GREEN + "/skywars set waitingRoom (arena) - sets the waitingroom for the given arena");
		}
		if (p.hasPermission("skywars.set.lobby") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars setLobby - sets the lobby for all arenas");
		}
		if (p.hasPermission("skywars.set.bound") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars setBound (arena) - sets the y-location for which players will be killed");
		}
		if (p.hasPermission("skywars.create.sign") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars createSign (arena) - creates a sign for the given arena");
		}
		if (p.hasPermission("skywars.create.kit") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars createKit (name) - creates a kit with the given name");
		}
		if (p.hasPermission("skywars.delete.kit") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars deleteKit (name) - deletes the kit with the given name");
		}
		if (p.hasPermission("skywars.default.kit") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars defaultKit (name) - sets the default kit to the given kit");
		}
		if (p.hasPermission("skywars.add.item") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars addItem (kit name) - adds the item in your hand to the kit with the given name");
		}
		if (p.hasPermission("skywars.add.chest.item") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars addChestItem - adds the item in your hand to the chests pool");
		}
		if (p.hasPermission("skywars.add.rare.chest.item") || p.isOp()) {
			p.sendMessage(
					ChatColor.GREEN + "/skywars addRareChestItem - adds the item in your hand to the rare chests pool");
		}
		if (p.hasPermission("skywars.remove.chest.item") || p.isOp()) {
			p.sendMessage(
					ChatColor.GREEN + "/skywars removeChestItem - removes the item in your hand to the chests pool");
		}
		if (p.hasPermission("skywars.remove.rare.chest.item") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars removeRareChestItem - removes the item in your hand to the rare chests pool");
		}
		if (p.hasPermission("skywars.add.chest") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars addChest (arena name) - adds your target block as a chest for a given arena");
		}
		if (p.hasPermission("skywars.add.rare.chest") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars addRareChest (arena name) - adds your target block as a rare chest for a given arena");
		}
		if (p.hasPermission("skywars.remove.chest") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars removeChest (arena name) - removes your target chest from a given arena");
		}
		if (p.hasPermission("skywars.remove.rare.chest") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars removeRareChest (arena name) - removes your target rare chest from a given arena");
		}
		if (p.hasPermission("skywars.remove.item") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN
					+ "/skywars removeItem (kit name) - removes the item in your hand from the kit with the given name");
		}
		if (p.hasPermission("skywars.reload") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars reload - reloads the plugin");
		}
		if (p.hasPermission("skywars.add.kit.icon") || p.isOp()) {
			p.sendMessage(ChatColor.GREEN + "/skywars kitIcon - sets the icon for a kit");
		}
		p.sendMessage(ChatColor.GREEN + "/skywars lobby - teleports you to the lobby");
		p.sendMessage(ChatColor.GREEN + "/skywars kit - opens a gui to choose a kit");
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if (cmd.getName().equalsIgnoreCase("leave")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Location l = (Location) Main.getMain().getConfig().get("lobby");
				p.teleport(l);
				p.sendMessage(
						ChatColor.translateAlternateColorCodes('&', Main.getMain().getConfig().getString("lobby-tp")));
				for (Arena a : Main.getMain().getArenas()) {
					if (a.getPlayers().contains(p)) {
						a.removePlayer(p);
						for (Player player : a.getPlayers()) {
							player.sendMessage(ChatColor.DARK_RED + p.getName() + " quit!");
						}
					}
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("Skywars")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("addspawn") && args.length == 2) {
						if (p.hasPermission("sw.add.spawn") || p.isOp()) {
							if (Main.getMain().getConfig().contains("arenas." + args[1] + ".spawns")) {
								ArrayList<Location> spawns = (ArrayList<Location>) Main.getMain().getConfig()
										.get("arenas." + args[1] + ".spawns");
								spawns.add(p.getLocation());
								Main.getMain().getConfig().set("arenas." + args[1] + ".spawns", spawns);
								p.sendMessage(ChatColor.GREEN + "Added a spawn to arena " + args[1]);
								Main.getMain().saveConfig();
							} else {
								ArrayList<Location> spawn = new ArrayList<Location>();
								spawn.add(p.getLocation());
								Main.getMain().getConfig().set("arenas." + args[1] + ".spawns", spawn);
								Main.getMain().arenas.add(new Arena(args[1]));
								p.sendMessage(ChatColor.GREEN + "Created arena " + args[1]);
								p.sendMessage(ChatColor.GREEN + "Added the first spawn to arena " + args[1]);
								Main.getMain().saveConfig();
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("deleteArena") && args.length == 2) {
						if (p.hasPermission("skywars.delete.arena") || p.isOp()) {
							Arena arena = Main.getMain().getArena(args[1]);
							if (arena != null) {
								Main.getMain().getConfig().set("arenas." + arena.getName(), null);
								Main.getMain().getConfig().set("rooms." + arena.getName(), null);
								Main.getMain().getConfig().set("signs." + arena.getName(), null);
								Main.getMain().getConfig().set("bound." + arena.getName(), null);
								Main.getMain().getConfig().set("chests." + arena.getName(), null);
								Main.getMain().getConfig().set("rareChests." + arena.getName(), null);
								Main.getMain().saveConfig();
								Main.getMain().arenas.remove(arena);
								Main.getMain().signs.remove(arena);
								p.sendMessage(ChatColor.RED + "Arena deleted");
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("forcestart") && args.length == 2) {
						if (p.hasPermission("skywars.force.start") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								if (!a.isStarting()) {
									a.start();
									p.sendMessage(ChatColor.GREEN + "Starting arena");
								} else {
									p.sendMessage(ChatColor.translateAlternateColorCodes('&',
											Main.getMain().getConfig().getString("already-running")));
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("forcestop") && args.length == 2) {
						if (p.hasPermission("skywars.force.stop") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								p.sendMessage(ChatColor.DARK_RED + "Stopping arena");
								for (Player pl : a.getPlayers()) {
									pl.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
											Main.getMain().getConfig().getString("end-by-admin")));
								}
								Bukkit.getScheduler().runTaskLater(Main.getMain(), new Runnable() {
									@Override
									public void run() {
										a.endGame();
									}
								}, 100);
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("setlobby")) {
						if (p.hasPermission("skywars.set.lobby") || p.isOp()) {
							Main.getMain().getConfig().set("lobby", p.getLocation());
							Main.getMain().saveConfig();
							p.sendMessage(ChatColor.GREEN + "Spawn saved to your location");
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("lobby")) {
						if (Main.getMain().getConfig().contains("lobby")) {
							Location l = (Location) Main.getMain().getConfig().get("lobby");
							p.teleport(l);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("lobby-tp")));
							for (Arena a : Main.getMain().getArenas()) {
								if (a.getPlayers().contains(p)) {
									a.removePlayer(p);
									for (Player player : a.getPlayers()) {
										player.sendMessage(ChatColor.DARK_RED + p.getName() + " quit!");
									}
								}
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("setup-error")));
						}
					} else if (args[0].equalsIgnoreCase("set") && args.length == 3
							&& args[1].equalsIgnoreCase("waitingroom")) {
						Arena a = Main.getMain().getArena(args[2]);
						if (p.hasPermission("skywars.set.waiting.room") || p.isOp()) {
							if (a != null) {
								Main.getMain().getConfig().set("rooms." + a.getName(), p.getLocation());
								Main.getMain().saveConfig();
								p.sendMessage(ChatColor.GREEN + "Waiting room set");
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("setbound") && args.length == 2) {
						if (p.hasPermission("skywars.set.bound") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								Main.getMain().getConfig().set("bound." + a.getName(), p.getLocation().getY());
								Main.getMain().saveConfig();
								p.sendMessage(ChatColor.GREEN + "Set the Y-bound for the arena");
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("createsign") && args.length == 2) {
						if (p.hasPermission("skywars.create.sign") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								Block b = p.getTargetBlockExact(7);
								if (b != null && b.getType() != null && !b.getType().equals(Material.AIR)) {
									if (b.getRelative(0, -1, 0).getType().equals(Material.AIR)) {
										b.setType(Material.OAK_WALL_SIGN);
										org.bukkit.block.data.type.WallSign s = (org.bukkit.block.data.type.WallSign) b
												.getBlockData();
										s.setFacing(p.getFacing().getOppositeFace());
										b.setBlockData(s);
									} else {
										b.setType(Material.OAK_SIGN);
										org.bukkit.block.data.type.Sign s = (org.bukkit.block.data.type.Sign) b
												.getBlockData();

										s.setRotation(p.getFacing().getOppositeFace());
										b.setBlockData(s);
									}
									Sign si = (Sign) b.getState();
									si.setLine(0, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Click to join!");
									si.setLine(1, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + args[1]);
									si.setLine(2, ChatColor.GOLD + "" + ChatColor.BOLD + "Game in ");
									si.setLine(3, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Players: "
											+ a.getPlayers().size() + "/" + a.size());
									si.update();
									if (Main.getMain().signs.keySet().contains(Main.getMain().getArena(args[1]))) {
										Main.getMain().signs.get(Main.getMain().getArena(args[1])).add(si);
									} else {
										ArrayList<Sign> signs = new ArrayList<Sign>();
										signs.add(si);
										Main.getMain().signs.put(Main.getMain().getArena(args[1]), signs);
									}
									Main.getMain().updateSigns();
									p.sendMessage(ChatColor.GREEN + "Sign created");
								} else {
									p.sendMessage(ChatColor.DARK_RED
											+ "Error: You must target a block within 7 blocks of your location");
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("createkit") && args.length == 2) {
						if (p.hasPermission("skywars.create.kit") || p.isOp()) {
							if (!Main.getMain().getConfig().contains("kits." + args[1])) {
								Main.getMain().getConfig().set("kits." + args[1], "");
								Main.getMain().saveConfig();
								p.sendMessage(ChatColor.GREEN + "Created the kit " + args[1]);
							} else {
								p.sendMessage(ChatColor.DARK_RED + "That kit is already created");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("deletekit") && args.length == 2) {
						if (p.hasPermission("skywars.delete.kit") || p.isOp()) {
							if (Main.getMain().getConfig().contains("kits." + args[1])) {
								Main.getMain().getConfig().set("kits." + args[1], null);
								Main.getMain().saveConfig();
								for (Player kitPlayer : Main.getMain().kits.keySet()) {
									if (Main.getMain().kits.get(kitPlayer).equalsIgnoreCase(args[1])) {
										Main.getMain().kits.remove(kitPlayer);
									}
								}
								Main.getMain().updateKits();
								p.sendMessage(ChatColor.DARK_RED + "Deleted the kit " + args[1]);
							} else {
								p.sendMessage(ChatColor.DARK_RED + "That kit cannot be found");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("defaultkit") && args.length == 2) {
						if (p.hasPermission("skywars.default.kit") || p.isOp()) {
							if (Main.getMain().getConfig().contains("kits." + args[1])) {
								Main.getMain().getConfig().set("default-kit", args[1]);
								Main.getMain().saveConfig();
								Main.getMain().updateKits();
								p.sendMessage(ChatColor.GREEN + "Set the default kit to " + args[1]);
							} else {
								p.sendMessage(ChatColor.DARK_RED + "That kit cannot be found");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("additem") && args.length == 2) {
						if (p.hasPermission("skywars.add.item") || p.isOp()) {
							if (Main.getMain().getConfig().contains("kits." + args[1])) {
								if (p.getInventory().getItemInMainHand() != null
										&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
									ArrayList<ItemStack> items;
									if (Main.getMain().getConfig().get("kits." + args[1]).toString().length() > 10) {
										items = (ArrayList<ItemStack>) Main.getMain().getConfig()
												.get("kits." + args[1]);
									} else {
										items = new ArrayList<ItemStack>();
									}
									items.add(p.getInventory().getItemInMainHand());
									Main.getMain().getConfig().set("kits." + args[1], items);
									Main.getMain().saveConfig();
									p.sendMessage(ChatColor.GREEN + "Added item to the kit");
								} else {
									p.sendMessage(ChatColor.DARK_RED + "You do not have an item in your hand!");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + "That kit cannot be found");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("removeitem") && args.length == 2) {
						if (p.hasPermission("skywars.remove.item") || p.isOp()) {
							if (Main.getMain().getConfig().contains("kits." + args[1])) {
								if (p.getInventory().getItemInMainHand() != null
										&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
									ArrayList<ItemStack> items;
									if (Main.getMain().getConfig().get("kits." + args[1]).toString().length() > 10) {
										items = (ArrayList<ItemStack>) Main.getMain().getConfig()
												.get("kits." + args[1]);
										ItemStack hand = p.getInventory().getItemInMainHand();
										if (items.contains(hand)) {
											items.remove(hand);
											Main.getMain().getConfig().set("kits." + args[1], items);
											Main.getMain().saveConfig();
											p.sendMessage(ChatColor.GREEN + "Removed that item from the kit");
										} else {
											p.sendMessage(ChatColor.DARK_RED + "That kit doesn't have that item in it");
										}
									} else {
										p.sendMessage(ChatColor.DARK_RED + "That kit doesn't have any items in it");
									}
								} else {
									p.sendMessage(ChatColor.DARK_RED + "You do not have an item in your hand!");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + "That kit cannot be found");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("kit")) {
						Main.getMain().openKitGUI(p);
					} else if (args[0].equalsIgnoreCase("reload")) {
						if (p.hasPermission("skywars.reload") || p.isOp()) {
							Main.getMain().reloadConfig();
							Main.getMain().saveConfig();
							Main.getMain().updateKits();
							p.sendMessage(ChatColor.GREEN + "Skywars was reloaded");
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("addchestitem")) {
						if (p.hasPermission("skywars.add.chest.item") || p.isOp()) {
							if (p.getInventory().getItemInMainHand() != null
									&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
								ArrayList<ItemStack> items;
								if (Main.getMain().getConfig().contains("items.chests")
										&& Main.getMain().getConfig().get("items.chests").toString().length() > 10) {
									items = (ArrayList<ItemStack>) Main.getMain().getConfig().get("items.chests");
								} else {
									items = new ArrayList<ItemStack>();
								}
								items.add(p.getInventory().getItemInMainHand());
								Main.getMain().getConfig().set("items.chests", items);
								Main.getMain().saveConfig();
								p.sendMessage(
										ChatColor.GREEN + "Added the item in your hand to the chest item selector");
							} else {
								p.sendMessage(ChatColor.DARK_RED + "You do not have an item in your hand!");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("removechestitem")) {
						if (p.hasPermission("skywars.remove.chest.item") || p.isOp()) {
							if (p.getInventory().getItemInMainHand() != null
									&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
								ArrayList<ItemStack> items;
								if (Main.getMain().getConfig().contains("items.chests")
										&& Main.getMain().getConfig().get("items.chests").toString().length() > 10) {
									items = (ArrayList<ItemStack>) Main.getMain().getConfig().get("items.chests");
								} else {
									items = new ArrayList<ItemStack>();
								}
								if (items.contains(p.getInventory().getItemInMainHand())) {
									items.remove(p.getInventory().getItemInMainHand());
									Main.getMain().getConfig().set("items.chests", items);
									Main.getMain().saveConfig();
									p.sendMessage(ChatColor.GREEN
											+ "Removed the item in your hand from the chest item selector");
								} else {
									p.sendMessage(ChatColor.DARK_RED + "That item is not in the list");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + "You do not have an item in your hand!");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("addchest") && args.length == 2) {
						if (p.hasPermission("skywars.add.chest") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								ArrayList<Location> chestLocs = new ArrayList<Location>();
								if (Main.getMain().getConfig().contains("chests." + a.getName())) {
									chestLocs = (ArrayList<Location>) Main.getMain().getConfig()
											.get("chests." + a.getName());
								}
								Block b = p.getTargetBlockExact(7);
								if (b != null && b.getType() != null && !b.getType().equals(Material.AIR)) {
									if (b.getType().toString().contains("CHEST")) {
										if (!chestLocs.contains(b.getLocation())) {
											chestLocs.add(b.getLocation());
											Main.getMain().getConfig().set("chests." + a.getName(), chestLocs);
											Main.getMain().saveConfig();
											p.sendMessage(ChatColor.GREEN + "Chest added to arena " + a.getName());
										} else {
											p.sendMessage(ChatColor.DARK_RED + "That chest is already in the list!");
										}
									} else {
										p.sendMessage(ChatColor.DARK_RED + "That is not a chest!");
									}
								} else {
									p.sendMessage(ChatColor.DARK_RED + "Couldn't find a block within 7 blocks");
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("addrarechest") && args.length == 2) {
						if (p.hasPermission("skywars.add.rare.chest") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								ArrayList<Location> chestLocs = new ArrayList<Location>();
								if (Main.getMain().getConfig().contains("rareChests." + a.getName())) {
									chestLocs = (ArrayList<Location>) Main.getMain().getConfig()
											.get("rareChests." + a.getName());
								}
								Block b = p.getTargetBlockExact(7);
								if (b != null && b.getType() != null && !b.getType().equals(Material.AIR)) {
									if (b.getType().toString().contains("CHEST")) {
										if (!chestLocs.contains(b.getLocation())) {
											chestLocs.add(b.getLocation());
											Main.getMain().getConfig().set("rareChests." + a.getName(), chestLocs);
											Main.getMain().saveConfig();
											p.sendMessage(ChatColor.GREEN + "Rare chest added to arena " + a.getName());
										} else {
											p.sendMessage(ChatColor.DARK_RED + "That chest is already in the list!");
										}
									} else {
										p.sendMessage(ChatColor.DARK_RED + "That is not a chest!");
									}
								} else {
									p.sendMessage(ChatColor.DARK_RED + "Couldn't find a block within 7 blocks");
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("removeChest") && args.length == 2) {
						if (p.hasPermission("skywars.remove.chest") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								ArrayList<Location> chestLocs = new ArrayList<Location>();
								if (Main.getMain().getConfig().contains("chests." + a.getName())) {
									chestLocs = (ArrayList<Location>) Main.getMain().getConfig()
											.get("chests." + a.getName());
								}
								Block b = p.getTargetBlockExact(7);
								if (b != null && b.getType() != null && !b.getType().equals(Material.AIR)) {
									if (b.getType().toString().contains("CHEST")) {
										if (chestLocs.contains(b.getLocation())) {
											chestLocs.remove(b.getLocation());
											Main.getMain().getConfig().set("chests." + a.getName(), chestLocs);
											Main.getMain().saveConfig();
											p.sendMessage(ChatColor.GREEN + "Chest removed from arena " + a.getName());
										} else {
											p.sendMessage(ChatColor.DARK_RED + "That chest is not in the list!");
										}
									} else {
										p.sendMessage(ChatColor.DARK_RED + "That is not a chest!");
									}
								} else {
									p.sendMessage(ChatColor.DARK_RED + "Couldn't find a block within 7 blocks");
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("removerarechest") && args.length == 2) {
						if (p.hasPermission("skywars.remoev.rare.chest") || p.isOp()) {
							Arena a = Main.getMain().getArena(args[1]);
							if (a != null) {
								ArrayList<Location> chestLocs = new ArrayList<Location>();
								if (Main.getMain().getConfig().contains("rareChests." + a.getName())) {
									chestLocs = (ArrayList<Location>) Main.getMain().getConfig()
											.get("rareChests." + a.getName());
								}
								Block b = p.getTargetBlockExact(7);
								if (b != null && b.getType() != null && !b.getType().equals(Material.AIR)) {
									if (b.getType().toString().contains("CHEST")) {
										if (chestLocs.contains(b.getLocation())) {
											chestLocs.remove(b.getLocation());
											Main.getMain().getConfig().set("rareChests." + a.getName(), chestLocs);
											Main.getMain().saveConfig();
											p.sendMessage(
													ChatColor.GREEN + "Rare chest removed from arena " + a.getName());
										} else {
											p.sendMessage(ChatColor.DARK_RED + "That chest is not in the list!");
										}
									} else {
										p.sendMessage(ChatColor.DARK_RED + "That is not a chest!");
									}
								} else {
									p.sendMessage(ChatColor.DARK_RED + "Couldn't find a block within 7 blocks");
								}
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&',
										Main.getMain().getConfig().getString("missing-arena")));
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("addrarechestitem")) {
						if (p.hasPermission("skywars.add.rare.chest.item") || p.isOp()) {
							if (p.getInventory().getItemInMainHand() != null
									&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
								ArrayList<ItemStack> items;
								if (Main.getMain().getConfig().contains("items.rareChests") && Main.getMain()
										.getConfig().get("items.rareChests").toString().length() > 10) {
									items = (ArrayList<ItemStack>) Main.getMain().getConfig().get("items.rareChests");
								} else {
									items = new ArrayList<ItemStack>();
								}
								items.add(p.getInventory().getItemInMainHand());
								Main.getMain().getConfig().set("items.rareChests", items);
								Main.getMain().saveConfig();
								p.sendMessage(ChatColor.GREEN
										+ "Added the item in your hand to the rare chest item selector");
							} else {
								p.sendMessage(ChatColor.DARK_RED + "You do not have an item in your hand!");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("removerarechestitem")) {
						if (p.hasPermission("skywars.add.rare.chest.item") || p.isOp()) {
							if (p.getInventory().getItemInMainHand() != null
									&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
								ArrayList<ItemStack> items;
								if (Main.getMain().getConfig().contains("items.rareChests") && Main.getMain()
										.getConfig().get("items.rareChests").toString().length() > 10) {
									items = (ArrayList<ItemStack>) Main.getMain().getConfig().get("items.rareChests");
								} else {
									items = new ArrayList<ItemStack>();
								}
								if (items.contains(p.getInventory().getItemInMainHand())) {
									items.remove(p.getInventory().getItemInMainHand());
									Main.getMain().getConfig().set("items.rareChests", items);
									Main.getMain().saveConfig();
									p.sendMessage(ChatColor.GREEN
											+ "Removed the item in your hand from the rare chest item selector");
								} else {
									p.sendMessage(ChatColor.DARK_RED + "That item is not in the list");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + "You do not have an item in your hand!");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else if (args[0].equalsIgnoreCase("kitIcon") && args.length == 2) {
						if (p.hasPermission("skywars.add.kit.icon") || p.isOp()) {
							if (p.getInventory().getItemInMainHand() != null
									&& !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
								if (Main.getMain().getConfig().contains("kits." + args[1])) {
									Main.getMain().getConfig().set("icons." + args[1],
											p.getInventory().getItemInMainHand());
									Main.getMain().saveConfig();
									p.sendMessage(ChatColor.GREEN + "Set that item as the icon for " + args[1]);
								} else {
									p.sendMessage(ChatColor.DARK_RED + "That kit doesn't exist!");
								}
							} else {
								p.sendMessage(ChatColor.DARK_RED + "You do not have an item in your hand!");
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',
									Main.getMain().getConfig().getString("no-perm")));
						}
					} else {
						sendAvailableCommands(p);
					}
				} else {
					sendAvailableCommands(p);
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "You are not a player!");
			}
		}
		return false;
	}

	public boolean isStringInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

}
