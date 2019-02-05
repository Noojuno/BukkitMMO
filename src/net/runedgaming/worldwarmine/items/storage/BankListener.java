package net.runedgaming.worldwarmine.items.storage;

import java.sql.SQLException;

import net.runedgaming.worldwarmine.WWMMain;
import net.runedgaming.worldwarmine.libs.StringInventory;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class BankListener implements Listener {
	@EventHandler
	public void PlayerInteractSign(PlayerInteractEvent event)
			throws SQLException {
		Long now = Long.valueOf(System.currentTimeMillis());
		Player player = event.getPlayer();
		World world = player.getWorld();
		Block block = event.getClickedBlock();

		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK)
				|| (event.getAction() == Action.LEFT_CLICK_BLOCK)) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) event.getClickedBlock().getState();

				if (s.getLine(0).toString().contentEquals("[Bank1]")) {
					player.openInventory(StringInventory.getPlayerInventory(
							player, "bank1", 1, 54));
				}

				if (s.getLine(0).toString()
						.contentEquals(ChatColor.DARK_BLUE + "[Bank2]")) {
					player.openInventory(StringInventory.getPlayerInventory(
							player, "bank2", 2, 54));
				}

				if (player.hasPermission("worldwarmine.vip")) {
					if (s.getLine(0).toString().contentEquals("[VIPBank1]")) {
						player.openInventory(StringInventory
								.getPlayerInventory(player, "bank3", 3, 54));
					}

					if (s.getLine(0).toString()
							.contentEquals(ChatColor.DARK_BLUE + "[VIPBank2]")) {
						player.openInventory(StringInventory
								.getPlayerInventory(player, "bank4", 4, 54));
					}

					if (s.getLine(0).toString()
							.contentEquals(ChatColor.DARK_BLUE + "[VIPBank3]")) {
						player.openInventory(StringInventory
								.getPlayerInventory(player, "bank5", 5, 54));
					}

					if (s.getLine(0).toString()
							.contentEquals(ChatColor.DARK_BLUE + "[VIPBank4]")) {
						player.openInventory(StringInventory
								.getPlayerInventory(player, "bank6", 6, 54));
					}
				} else {
					player.sendMessage(ChatColor.RED + "You must be a "
							+ ChatColor.GOLD + "VIP " + ChatColor.RED
							+ "to do that!");
				}
			}
		}
	}
}
