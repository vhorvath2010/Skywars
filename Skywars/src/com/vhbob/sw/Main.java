package com.vhbob.sw;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	static Main main;
	ArrayList<Arena> arenas;
	public HashMap<Arena, ArrayList<Sign>> signs;
	public HashMap<Player, String> kits;
	private Economy econ;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		if (!setupEconomy()) {
			this.getLogger().severe("Disabled due to no Vault dependency found!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Bukkit.getPluginManager().registerEvents(new Events(this), this);
		main = this;
		signs = new HashMap<Arena, ArrayList<Sign>>();
		arenas = new ArrayList<Arena>();
		if (getConfig().contains("arenas")) {
			for (String name : getConfig().getConfigurationSection("arenas").getKeys(false)) {
				Arena a = new Arena(name);
				arenas.add(a);
				ArrayList<Sign> signsToAdd = new ArrayList<Sign>();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabled arena " + a.getName());
				if (getConfig().contains("signs." + a.getName())) {
					@SuppressWarnings("unchecked")
					ArrayList<Location> signLocs = (ArrayList<Location>) getConfig().get("signs." + a.getName());
					for (Location signLoc : signLocs) {
						if (signLoc.getBlock().getType().toString().contains("SIGN")) {
							Sign s = (Sign) signLoc.getBlock().getState();
							signsToAdd.add(s);
						}
					}
				}
				signs.put(a, signsToAdd);
			}
		}
		getCommand("Skywars").setExecutor(new ArenaSetup());
		getCommand("leave").setExecutor(new ArenaSetup());
		updateSigns();
		kits = new HashMap<Player, String>();
		updateKits();
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {

			@Override
			public void run() {
				for (Arena a : arenas) {
					if (a.isRunning()) {
						for (Player p : a.getPlayers()) {
							if (p.getLocation().getY() < getConfig().getDouble("bound." + a.getName())
									&& a.getAlive().contains(p)) {
								a.death(p);
							}
						}
					}
				}
			}
		}, 20, 20);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Skywars has been enabled");
	}

	@Override
	public void onDisable() {
		reloadConfig();
		saveConfig();
		for (Arena a : signs.keySet()) {
			ArrayList<Location> signLocs = new ArrayList<Location>();
			for (Sign s : signs.get(a)) {
				if (s.getLocation().getBlock().getType().toString().contains("SIGN")) {
					signLocs.add(s.getLocation());
				}
			}
			getConfig().set("signs." + a.getName(), signLocs);
		}
		for (Arena a : arenas) {
			a.endGame();
		}
		saveConfig();
	}

	public static Main getMain() {
		return main;
	}

	public ArrayList<Arena> getArenas() {
		return arenas;
	}

	public Arena getArena(String name) {
		for (Arena a : arenas) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}

	public void updateSigns() {
		for (Arena a : signs.keySet()) {
			for (Sign s : signs.get(a)) {
				s.setLine(0, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Click to join!");
				s.setLine(1, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + a.getName());
				if (!a.isRunning()) {
					s.setLine(2, ChatColor.GOLD + "" + ChatColor.BOLD + "Game in lobby");
				} else {
					s.setLine(2, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Game running!");
				}
				s.setLine(3, ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Players: " + a.getPlayers().size() + "/"
						+ a.size());
				s.update();
			}
		}
	}

	public void openKitGUI(Player p) {
		Inventory i = Bukkit.createInventory(null, 54,
				ChatColor.translateAlternateColorCodes('&', getConfig().getString("kit-gui-title")));
		if (getConfig().getConfigurationSection("kits") != null) {
			for (String kit : getConfig().getConfigurationSection("kits").getKeys(false)) {
				ItemStack item;
				if (getConfig().contains("icons." + kit) && getConfig().getItemStack("icons." + kit) != null) {
					item = getConfig().getItemStack("icons." + kit);
				} else if (p.hasPermission("kit." + kit)) {
					if (kits.keySet() != null && kits.keySet().contains(p) && kits.get(p) != null
							&& kits.get(p).equalsIgnoreCase(kit)) {
						item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
					} else {
						item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
					}
				} else {
					item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
				}
				ItemMeta im = item.getItemMeta();
				if (p.hasPermission("kit." + kit) || getConfig().getString("default-kit").equalsIgnoreCase(kit)) {
					if (kits.keySet().contains(p) && kits.get(p).equalsIgnoreCase(kit)) {
						im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + kit);
					} else {
						im.setDisplayName(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + kit);
					}
				} else {
					im.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + kit);
				}
				ArrayList<String> lore = new ArrayList<String>();
				if (p.hasPermission("kit." + kit) || getConfig().getString("default-kit").equalsIgnoreCase(kit)) {
					lore.add(ChatColor.WHITE + "Left click to choose this kit");
				} else {
					lore.add(ChatColor.RED + "You do not have permission to use this kit");
				}
				lore.add(ChatColor.WHITE + "Right click to see its contents");
				im.setLore(lore);
				item.setItemMeta(im);
				i.addItem(item);
			}
		}
		p.openInventory(i);
	}

	public void updateKits() {
		if (getConfig().contains("kits")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!Main.getMain().kits.containsKey(player) && Main.getMain().getConfig().contains("default-kit")) {
					Main.getMain().kits.put(player, Main.getMain().getConfig().getString("default-kit"));
				}
			}
		}
	}

	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public Economy getEconomy() {
		return econ;
	}

}
