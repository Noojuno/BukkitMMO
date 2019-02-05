package net.runedgaming.worldwarmine.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.runedgaming.worldwarmine.WWMMain;
import net.runedgaming.worldwarmine.libs.StringInventory;
import net.runedgaming.worldwarmine.libs.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PlayerInventoryListener implements Listener {
	public static HashMap<Player, Integer> bank = new HashMap<Player, Integer>();
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	Date date = new Date();

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		Player player = event.getPlayer();

		ScoreboardManager scorem = Bukkit.getScoreboardManager();
		Scoreboard board = scorem.getNewScoreboard();

		Statement statement = WWMMain.c.createStatement();

		Objective objective = board.registerNewObjective("light", "dummy");

		objective.setDisplayName(ChatColor.BLUE + "WorldWarMine");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score = objective.getScore(Bukkit
				.getOfflinePlayer(ChatColor.GREEN + "Mana:")); // Get a fake
																// offline
																// player
		score.setScore(1000);

		player.setScoreboard(board);

		bank.put(player, 0);

		try {
			ResultSet res = statement
					.executeQuery("SELECT * FROM playerinfo WHERE name = '"
							+ player.getName() + "';");

			res.next();

			if (res.getString("name") == null) {
				event.setJoinMessage(player.getName()
						+ " has joined the adventure for the first time!");
				statement
						.executeUpdate("INSERT INTO playerinfo (`name`, `joindate`, `lastvisit`, `x`, `y`, `z`, `vip`) VALUES ('"
								+ player.getName()
								+ "', '"
								+ dateFormat.format(date)
								+ "', '"
								+ dateFormat.format(date)
								+ "', '"
								+ player.getLocation().getX()
								+ "', '"
								+ player.getLocation().getY()
								+ "', '"
								+ player.getLocation().getZ() + "', '1')");
				statement
						.executeUpdate("INSERT INTO inventorys (`player`, `bank1`, `bank2`, `bank3`, `bank4`, `bank5`, `bank6`, `inventory`, `inventoryarmor`) VALUES ('"
								+ player.getName()
								+ "', 'empty', 'empty', 'empty', 'empty', 'empty', 'empty', 'empty', 'empty')");
			} else {
				event.setJoinMessage(player.getName()
						+ " has returned to the adventure!");

				ResultSet res1 = statement
						.executeQuery("SELECT * FROM playerinfo WHERE name = '"
								+ player.getName() + "';");

				res1.next();

				Location spawnloc = new Location(player.getWorld(),
						res1.getInt("x"), res1.getInt("y"), res1.getInt("z"));

				player.teleport(spawnloc);

				ResultSet res2 = statement
						.executeQuery("SELECT * FROM inventorys WHERE player = '"
								+ player.getName() + "';");

				res2.next();

				ItemStack playerItems[] = StringInventory.StringToInventory(
						res2.getString("inventory"), 36).getContents();
				ItemStack playerAItems[] = StringInventory.StringToInventory(
						res2.getString("inventoryarmor"), 9).getContents();

				player.getInventory().clear();
				player.getInventory().setContents(playerItems);
				/*
				 * player.getInventory().setBoots(playerAItems[0]);
				 * player.getInventory().setLeggings(playerAItems[1]);
				 * player.getInventory().setChestplate(playerAItems[2]);
				 * player.getInventory().setHelmet(playerAItems[3]);
				 */
			}
		} catch (SQLException e) {
			event.setJoinMessage(player.getName()
					+ " has joined the adventure for the first time!");
			statement
					.executeUpdate("INSERT INTO playerinfo (`name`, `joindate`, `lastvisit`, `x`, `y`, `z`, `vip`, `linkedaccount`) VALUES ('"
							+ player.getName()
							+ "', '"
							+ dateFormat.format(date)
							+ "', '"
							+ dateFormat.format(date)
							+ "', '"
							+ player.getLocation().getX()
							+ "', '"
							+ player.getLocation().getY()
							+ "', '"
							+ player.getLocation().getZ() + "', '1', 'none')");
			statement
					.executeUpdate("INSERT INTO inventorys (`player`, `bank1`, `bank2`, `bank3`, `bank4`, `bank5`, `bank6`, `inventory`, `inventoryarmor`) VALUES ('"
							+ player.getName()
							+ "', 'empty', 'empty', 'empty', 'empty', 'empty', 'empty', 'empty', 'empty')");
		}
	}

	@EventHandler
	public void BankSave(InventoryCloseEvent event) throws SQLException {
		Player player = (Player) event.getPlayer();
		Statement statement = WWMMain.c.createStatement();

		if (bank.get(player) == 1) {
			statement.executeUpdate("UPDATE inventorys SET bank1 = '"
					+ StringInventory.StringFromInventory(event.getInventory(),
							1) + "' WHERE player = '" + player.getName() + "'");
			System.out.println("[RUNEDMMO] Saved " + player.getName()
					+ "'s Bank 1");
		}

		else if (bank.get(player) == 2) {
			statement.executeUpdate("UPDATE inventorys SET bank2 = '"
					+ StringInventory.StringFromInventory(event.getInventory(),
							1) + "' WHERE player = '" + player.getName() + "'");
			System.out.println("[RUNEDMMO] Saved " + player.getName()
					+ "'s Bank 2");
		}

		else if (bank.get(player) == 3) {
			statement.executeUpdate("UPDATE inventorys SET bank3 = '"
					+ StringInventory.StringFromInventory(event.getInventory(),
							1) + "' WHERE player = '" + player.getName() + "'");
			System.out.println("[RUNEDMMO] Saved " + player.getName()
					+ "'s VIP Bank 1");
		}

		else if (bank.get(player) == 4) {
			statement.executeUpdate("UPDATE inventorys SET bank4 = '"
					+ StringInventory.StringFromInventory(event.getInventory(),
							1) + "' WHERE player = '" + player.getName() + "'");
			System.out.println("[RUNEDMMO] Saved " + player.getName()
					+ "'s VIP Bank 2");
		}

		else if (bank.get(player) == 5) {
			statement.executeUpdate("UPDATE inventorys SET bank5 = '"
					+ StringInventory.StringFromInventory(event.getInventory(),
							1) + "' WHERE player = '" + player.getName() + "'");
			System.out.println("[RUNEDMMO] Saved " + player.getName()
					+ "'s VIP Bank 3");
		}

		else if (bank.get(player) == 6) {
			statement.executeUpdate("UPDATE inventorys SET bank6 = '"
					+ StringInventory.StringFromInventory(event.getInventory(),
							1) + "' WHERE player = '" + player.getName() + "'");
			System.out.println("[RUNEDMMO] Saved " + player.getName()
					+ "'s VIP Bank 4");
		} else if (event.getInventory() == event.getPlayer().getInventory()) {
			statement.executeUpdate("UPDATE inventorys SET inventory = '"
					+ StringInventory.StringFromInventory(event.getInventory(),
							1) + "' WHERE player = '" + player.getName() + "'");
			System.out.println("[RUNEDMMO] Saved " + player.getName()
					+ "'s Inventory");
		}

		bank.put(player, 0);
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) throws SQLException {
		Player player = event.getPlayer();
		Statement statement = WWMMain.c.createStatement();

		try {
			statement.executeUpdate("UPDATE playerinfo SET lastvisit = '"
					+ dateFormat.format(date) + "', x = '"
					+ player.getLocation().getX() + "' , y = '"
					+ player.getLocation().getY() + "' , z = '"
					+ player.getLocation().getZ() + "' WHERE name = '"
					+ player.getName() + "'");

			Inventory armorinv = Bukkit.createInventory(null, 9);

			if (player.getInventory().getBoots() != null) {
				armorinv.addItem(player.getInventory().getBoots());
			} else {
				armorinv.addItem(new ItemStack(Material.AIR));
			}

			if (player.getInventory().getLeggings() != null) {
				armorinv.addItem(player.getInventory().getLeggings());
			} else {
				armorinv.addItem(new ItemStack(Material.AIR));
			}

			if (player.getInventory().getChestplate() != null) {
				armorinv.addItem(player.getInventory().getChestplate());
			} else {
				armorinv.addItem(new ItemStack(Material.AIR));
			}

			if (player.getInventory().getHelmet() != null) {
				armorinv.addItem(player.getInventory().getHelmet());
			} else {
				armorinv.addItem(new ItemStack(Material.AIR));
			}

			statement.executeUpdate("UPDATE inventorys SET inventory = '"
					+ StringInventory.StringFromInventory(event.getPlayer()
							.getInventory(), 1) + "' WHERE player = '"
					+ player.getName() + "'");
			statement.executeUpdate("UPDATE inventorys SET inventoryarmor = '"
					+ StringInventory.StringFromInventory(armorinv, 2)
					+ "' WHERE player = '" + player.getName() + "'");
		} catch (SQLException e) {
		}
	}
}
