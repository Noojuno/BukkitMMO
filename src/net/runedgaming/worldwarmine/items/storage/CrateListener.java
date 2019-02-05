package net.runedgaming.worldwarmine.items.storage;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Random;

import net.runedgaming.worldwarmine.WWMMain;
import net.runedgaming.worldwarmine.libs.Util;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class CrateListener implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		Block block = event.getClickedBlock();
		Inventory inv = Bukkit.createInventory(null, 27, "Supply Crate");

		if ((event.getAction() == Action.RIGHT_CLICK_BLOCK)
				|| event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.PISTON_BASE
					&& event.getClickedBlock().getData() == (byte) 6) {

				for (int i = 0; i < Util.randInt(0, inv.getSize()); i++) {

					Random random = new Random();
					Material[] materials = Material.values();
					int size = materials.length;
					int index = random.nextInt(size);
					Material randomMaterial = materials[index];
					ItemStack item = new ItemStack(randomMaterial,
							Util.randInt(1, 64));
					inv.addItem(item);

				}

				for (ItemStack item : inv.getContents()) {
					if (item != null) {
						player.getWorld().dropItemNaturally(
								event.getClickedBlock().getLocation(), item);
					}
				}

				event.getClickedBlock().setType(Material.AIR);

			}
		}

	}
}
