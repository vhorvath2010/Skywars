package com.vhbob.sw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Arena {

	ArrayList<Player> players, alive;
	ArrayList<Entity> animals;
	String name;
	private HashMap<Player, ItemStack[]> invs;
	Main main;
	boolean starting, running;
	HashMap<Location, Material> reset = new HashMap<Location, Material>();
	HashMap<Location, BlockData> resetData = new HashMap<Location, BlockData>();
	HashMap<Player, Player> lastHit = new HashMap<Player, Player>();
	HashMap<Player, Integer> kills;

	public Arena(String name) {
		animals = new ArrayList<Entity>();
		main = Main.getMain();
		invs = new HashMap<Player, ItemStack[]>();
		this.name = name;
		alive = new ArrayList<Player>();
		players = new ArrayList<Player>();
		lastHit = new HashMap<Player, Player>();
		kills = new HashMap<Player, Integer>();
	}

	public String getName() {
		return name;
	}

	public int size() {
		if (Main.getMain().getConfig().contains("arenas." + name + ".spawns")) {
			@SuppressWarnings("unchecked")
			ArrayList<Location> spawnsConfig = (ArrayList<Location>) Main.getMain().getConfig()
					.get("arenas." + name + ".spawns");
			return spawnsConfig.size();
		} else {
			return 0;
		}
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isStarting() {
		return starting;
	}

	public ArrayList<Player> getAlive() {
		return alive;
	}

	@SuppressWarnings("unchecked")
	public void addPlayer(Player p) {
		if (!players.contains(p)) {
			invs.put(p, p.getInventory().getContents());
			p.getInventory().clear();
			players.add(p);
			alive = (ArrayList<Player>) players.clone();
			Location loc = (Location) main.getConfig().get("rooms." + name);
			p.teleport(loc);
			for (Player player : players) {
				player.sendMessage(org.bukkit.ChatColor.GREEN + p.getDisplayName() + " has joined the game!");
				player.sendMessage(org.bukkit.ChatColor.GREEN + "Players: " + players.size() + "/" + size());
				if (players.size() < main.getConfig().getInt("min-players")) {
					player.sendMessage(org.bukkit.ChatColor.GREEN + "" + main.getConfig().getInt("min-players")
							+ " Players are needed to start the game.");
				}
			}
			main.updateSigns();
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {

				@Override
				public void run() {
					ItemStack leave = new ItemStack(Material.COMPASS);
					ItemMeta leaveMeta = leave.getItemMeta();
					leaveMeta.setDisplayName(
							org.bukkit.ChatColor.GOLD + "" + org.bukkit.ChatColor.BOLD + "Return to lobby");
					ArrayList<String> lore = new ArrayList<String>();
					lore.add("click to teleport to the lobby");
					leaveMeta.setLore(lore);
					leave.setItemMeta(leaveMeta);
					p.getInventory().setItem(8, leave);
					ItemStack kit = new ItemStack(Material.BLAZE_POWDER);
					ItemMeta kitMeta = kit.getItemMeta();
					kitMeta.setDisplayName(org.bukkit.ChatColor.GOLD + "" + org.bukkit.ChatColor.BOLD + "Select a kit");
					ArrayList<String> loreKit = new ArrayList<String>();
					loreKit.add("click to select a kit!");
					kitMeta.setLore(loreKit);
					kit.setItemMeta(kitMeta);
					p.getInventory().setItem(0, kit);
				}
			}, 1);
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	@SuppressWarnings("unchecked")
	public void start() {

		// normal chests

		ArrayList<Location> chestLocs = new ArrayList<Location>();
		if (main.getConfig().get("chests." + name) != null) {
			chestLocs = (ArrayList<Location>) main.getConfig().get("chests." + name);
		}

		if (chestLocs.size() > 0) {
			for (Location l : chestLocs) {
				if (l.getBlock() != null && l.getBlock().getType() != null
						&& l.getBlock().getType().toString().contains("CHEST")) {
					ArrayList<ItemStack> chestItemsC = new ArrayList<ItemStack>();
					if (Main.getMain().getConfig().contains("items.chests")
							&& Main.getMain().getConfig().get("items.chests").toString().length() > 10) {
						chestItemsC = (ArrayList<ItemStack>) Main.getMain().getConfig().get("items.chests");
					}
					ArrayList<ItemStack> chestItems = (ArrayList<ItemStack>) chestItemsC.clone();
					if (chestItems.size() > 0) {
						Chest c = (Chest) l.getBlock().getState();
						c.getInventory().clear();
						int items = new Random().nextInt(
								main.getConfig().getInt("max-items") - main.getConfig().getInt("min-items") + 1)
								+ main.getConfig().getInt("min-items");
						for (int i = 0; i < items && chestItems.size() > 0; i++) {
							ItemStack item = chestItems.get(new Random().nextInt(chestItems.size()));
							int slot = new Random().nextInt(c.getInventory().getSize());
							while (c.getInventory().getItem(slot) != null) {
								slot = new Random().nextInt(c.getInventory().getSize());
							}
							c.getInventory().setItem(slot, item);
							while (chestItems.contains(item)) {
								chestItems.remove(item);
							}
						}
					}
				}
			}
		}

		// rare chests
		ArrayList<Location> rareChestLocs = new ArrayList<Location>();
		if (main.getConfig().get("rareChests." + name) != null) {
			rareChestLocs = (ArrayList<Location>) main.getConfig().get("rareChests." + name);
		}

		if (rareChestLocs.size() > 0) {
			for (Location l : rareChestLocs) {
				if (l.getBlock() != null && l.getBlock().getType() != null
						&& l.getBlock().getType().toString().contains("CHEST")) {
					ArrayList<ItemStack> rareChestItemsC = new ArrayList<ItemStack>();
					if (Main.getMain().getConfig().contains("items.rareChests")
							&& Main.getMain().getConfig().get("items.rareChests").toString().length() > 10) {
						rareChestItemsC = (ArrayList<ItemStack>) Main.getMain().getConfig().get("items.rareChests");
					}
					ArrayList<ItemStack> rareChestItems = (ArrayList<ItemStack>) rareChestItemsC.clone();
					if (rareChestItems.size() > 0) {
						Chest c = (Chest) l.getBlock().getState();
						c.getInventory().clear();
						int items = new Random().nextInt(
								main.getConfig().getInt("max-items") - main.getConfig().getInt("min-items") + 1)
								+ main.getConfig().getInt("min-items");
						for (int i = 0; i < items && rareChestItems.size() > 0; i++) {
							ItemStack item = rareChestItems.get(new Random().nextInt(rareChestItems.size()));
							int slot = new Random().nextInt(c.getInventory().getSize());
							while (c.getInventory().getItem(slot) != null) {
								slot = new Random().nextInt(c.getInventory().getSize());
							}
							c.getInventory().setItem(slot, item);
							while (rareChestItems.contains(item)) {
								rareChestItems.remove(item);
							}
						}
					}
				}
			}
		}

		starting = true;
		for (Player p : players) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("game-start")));
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(org.bukkit.ChatColor.DARK_GREEN + "" + org.bukkit.ChatColor.BOLD + "5");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 100, 0.529732f);
				}
			}, 20);
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(org.bukkit.ChatColor.DARK_GREEN + "" + org.bukkit.ChatColor.BOLD + "4");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 100, 0.594604f);
				}
			}, 40);
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(org.bukkit.ChatColor.DARK_GREEN + "" + org.bukkit.ChatColor.BOLD + "3!");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 100, 0.667420f);
				}
			}, 60);
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(org.bukkit.ChatColor.DARK_GREEN + "" + org.bukkit.ChatColor.BOLD + "2!!");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 100, 0.707107f);
				}
			}, 80);
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					p.sendMessage(org.bukkit.ChatColor.DARK_GREEN + "" + org.bukkit.ChatColor.BOLD + "1!!!");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 100, 0.890899f);
				}
			}, 100);
		}
		Bukkit.getScheduler().runTaskLater(main, new Runnable() {

			@Override
			public void run() {
				spawnPlayers();
			}

		}, 120);
	}

	@SuppressWarnings("unchecked")
	public void spawnPlayers() {
		if (Main.getMain().getConfig().contains("arenas." + name + ".spawns")
				&& main.getConfig().getConfigurationSection("kits") != null
				&& main.getConfig().getString("default-kit") != null) {
			ArrayList<Location> spawnsConfig = (ArrayList<Location>) Main.getMain().getConfig()
					.get("arenas." + name + ".spawns");
			ArrayList<Location> spawns = (ArrayList<Location>) spawnsConfig.clone();
			kills.clear();
			for (Player p : players) {
				kills.put(p, 0);
				p.getInventory().clear();
				// Give Kit
				if (Main.getMain().kits.containsKey(p)
						&& Main.getMain().getConfig().get("kits." + Main.getMain().kits.get(p)) != null) {
					String kit = Main.getMain().kits.get(p);
					ArrayList<ItemStack> items = (ArrayList<ItemStack>) Main.getMain().getConfig().get("kits." + kit);
					for (ItemStack item : items) {
						if (item.getType().toString().contains("HELMET")) {
							p.getInventory().setHelmet(item);
						} else if (item.getType().toString().contains("CHESTPLATE")) {
							p.getInventory().setChestplate(item);
						} else if (item.getType().toString().contains("LEGGINGS")) {
							p.getInventory().setLeggings(item);
						} else if (item.getType().toString().contains("BOOTS")) {
							p.getInventory().setBoots(item);
						} else {
							p.getInventory().addItem(item);
						}
					}
				} else {
					p.sendMessage(
							ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("kit-not-found")));
				}
				p.setGameMode(GameMode.valueOf(main.getConfig().getString("game-gamemode")));
				int rand = new Random().nextInt(spawns.size());
				p.teleport(spawns.get(rand));
				spawns.remove(rand);
				p.setHealth(20);
				p.setFoodLevel(20);
			}
			updateScores();
			running = true;
			main.updateSigns();
			checkPlayerNumber();
		} else {
			for (Player p : players) {
				p.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
						main.getConfig().getString("setup-error")));
			}
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {

				@Override
				public void run() {
					endGame();
				}
			}, 100);
		}
	}

	public ArrayList<Location> getReset() {
		ArrayList<Location> locs = new ArrayList<Location>();
		for (Location l : reset.keySet()) {
			if (!locs.contains(l)) {
				locs.add(l);
			}
		}
		return locs;
	}

	public void removePlayer(Player p) {
		if (players.contains(p)) {
			players.remove(p);
		}
		if (alive.contains(p)) {
			alive.remove(p);
		}
		if (main.getConfig().contains("lobby")) {
			Location loc = (Location) main.getConfig().get("lobby");
			p.teleport(loc);
		} else {
			p.sendMessage(
					org.bukkit.ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("setup-error")));
		}
		if (invs.containsKey(p)) {
			p.getInventory().setContents(invs.get(p));
			invs.remove(p);
		}
		p.setGameMode(GameMode.valueOf(main.getConfig().getString("lobby-gamemode")));
		p.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
		for (PotionEffect effect : p.getActivePotionEffects()) {
			if (p.hasPotionEffect(effect.getType())) {
				p.removePotionEffect(effect.getType());
			}
		}
		checkPlayerNumber();
		main.updateSigns();
	}

	public void checkPlayerNumber() {
		if (running) {
			if (players.size() < 2 || alive.size() < 2) {
				for (Player p : players) {
					p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GAME OVER!",
							ChatColor.GREEN + "" + ChatColor.BOLD + alive.get(0).getName() + " WINS!!!", 10, 70, 20);
					p.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&',
							main.getConfig().getString("low-players")));
					main.getEconomy().depositPlayer(alive.get(0), main.getConfig().getInt("win-reward"));
					alive.get(0)
							.sendMessage(ChatColor.GOLD + "+" + main.getConfig().getInt("win-reward") + " coins!!!");
				}
				Bukkit.getScheduler().runTaskLater(main, new Runnable() {

					@Override
					public void run() {
						endGame();
					}
				}, 100);
			}
		}
	}

	public void logSpawn(Entity e) {
		animals.add(e);
	}

	public void endGame() {
		starting = false;
		running = false;
		ArrayList<Player> toRemove = new ArrayList<Player>();
		for (Player pl : players) {
			pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
			pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.FIREWORK);
			toRemove.add(pl);
		}
		for (Player player : toRemove) {
			removePlayer(player);
		}
		players.clear();
		for (Location loc : reset.keySet()) {
			if (reset.get(loc) != null) {
				loc.getBlock().setType(reset.get(loc));
			}
			if (resetData.get(loc) != null) {
				loc.getBlock().setBlockData(resetData.get(loc));
			}
		}
		for (Entity e : animals) {
			if (e != null) {
				if (!e.isDead()) {
					e.remove();
				}
			}
		}
		animals.clear();
		reset = new HashMap<Location, Material>();
		resetData = new HashMap<Location, BlockData>();
		kills.clear();
		main.updateSigns();
	}

	public void death(Player killed) {
		alive.remove(killed);
		for (ItemStack item : killed.getInventory().getContents()) {
			if (item != null) {
				killed.getWorld().dropItem(killed.getLocation(), item);
			}
		}
		for (Player player : players) {
			player.sendMessage(ChatColor.DARK_RED + killed.getName() + " has died");
		}

		killed.setGameMode(GameMode.SPECTATOR);
		if (lastHit.containsKey(killed) && lastHit.get(killed) != null) {
			Player damager = lastHit.get(killed);
			Main.getMain().getEconomy().depositPlayer(damager, Main.getMain().getConfig().getInt("kill-reward"));
			damager.sendMessage(ChatColor.GOLD + "+" + Main.getMain().getConfig().getInt("kill-reward") + " coins!");
			damager.playSound(damager.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
			kills.put(damager, kills.get(damager) + 1);
		}
		if (alive.size() >= 1) {
			killed.teleport(alive.get(0).getLocation());
		}
		if (alive.size() == 1) {
			for (Player player : players) {
				player.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GAME OVER!",
						ChatColor.GREEN + "" + ChatColor.BOLD + alive.get(0).getName() + " WINS!!!", 10, 70, 20);
			}
			main.getEconomy().depositPlayer(alive.get(0), main.getConfig().getInt("win-reward"));
			alive.get(0).sendMessage(ChatColor.GOLD + "+" + main.getConfig().getInt("win-reward") + " coins!!!");
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {

				@Override
				public void run() {
					endGame();
				}
			}, 100);
		}
		updateScores();
	}

	public void blockModified(Block block, boolean placement) {
		if (reset.isEmpty() || !reset.keySet().contains(block.getLocation())) {
			if (placement) {
				reset.put(block.getLocation(), Material.AIR);
				resetData.put(block.getLocation(), null);
			} else {
				reset.put(block.getLocation(), block.getType());
				resetData.put(block.getLocation(), block.getBlockData());
			}

		}
	}

	public void damage(Player damaged, Player damager) {
		lastHit.put(damaged, damager);
	}

	public void updateScores() {
		// get highest scores
		ArrayList<Player> topScorers = new ArrayList<Player>();
		for (int i = 100; i >= 0; i--) {
			for (Player p : kills.keySet()) {
				if (kills.get(p) >= i && !topScorers.contains(p)) {
					if (topScorers.size() < 10) {
						topScorers.add(p);
					} else {
						break;
					}
				}
			}
		}
		ScoreboardManager scoreboardManager = main.getServer().getScoreboardManager();
		Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("game", "sidebar",
				org.bukkit.ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("leaderboard")));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		for (Player scorer : topScorers) {
			boolean found = false;
			for (Team t : scoreboard.getTeams()) {
				if (t.getName().equalsIgnoreCase(scorer.getName())) {
					found = true;
				}
			}
			if (!found) {
				Team pScore = scoreboard.registerNewTeam(scorer.getName());
				String score = "";
				if (alive.contains(scorer)) {
					pScore.addEntry(org.bukkit.ChatColor.WHITE + scorer.getName() + ": ");
					score = org.bukkit.ChatColor.WHITE + scorer.getName() + ": ";
				} else {
					pScore.addEntry(
							org.bukkit.ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + scorer.getName() + ": ");
					score = org.bukkit.ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + scorer.getName() + ": ";
				}
				pScore.setSuffix("");
				pScore.setPrefix("");
				objective.getScore(score).setScore(kills.get(scorer));
			}
		}

		for (Player player : players) {
			player.setScoreboard(scoreboard);
		}
	}

}
