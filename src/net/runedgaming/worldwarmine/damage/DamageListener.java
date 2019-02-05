package net.runedgaming.worldwarmine.damage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.LivingEntity;

public class DamageListener implements Listener {

	Random ran = new Random();

	@EventHandler
	public void playerHitPlayerEvent(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Player) {
			Player player = (Player) damager;
			LivingEntity e = (LivingEntity) event.getEntity();
			boolean hasCustomName = e.getCustomName() != null;
			if (hasCustomName) {
				if (e.getCustomName().contains("Quest")) {
					event.setCancelled(true);
				} else {
					if (player.getItemInHand() != null) {
						if (player.getItemInHand().getItemMeta() != null) {
							if (player.getItemInHand().getItemMeta().getLore() != null) {
								if (player.getItemInHand().getItemMeta()
										.getLore().get(2) != null) {
									if (player.getItemInHand().getItemMeta()
											.getLore().get(2)
											.contains("Damage")) {

										String dm = player.getItemInHand()
												.getItemMeta().getLore().get(2);
										String[] zs = dm.split(" Attack ");
										String[] md = zs[0].split("c");
										String[] both = md[1].split(" - ");
										int zd = Integer.parseInt(both[1]);
										int zd1 = Integer.parseInt(both[0]);
										int x = ran.nextInt(zd1) + zd;
										int zd2 = x / 5;

										event.setDamage(zd2);
										player.sendMessage("You did " + zd2
												+ " Damage!");
									} else {
										event.setDamage(2D);
									}
								} else {
									event.setDamage(2D);
								}
							} else {
								event.setDamage(2D);
							}
						} else {
							event.setDamage(2D);
						}
					}
				}
			}

		}

	}
}
