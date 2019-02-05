package net.runedgaming.worldwarmine;

//~--- non-JDK imports --------------------------------------------------------

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.runedgaming.worldwarmine.libs.IconMenu;
import net.runedgaming.worldwarmine.libs.IconMenu.Row;
import net.runedgaming.worldwarmine.libs.IconMenu.onClick;
import net.runedgaming.worldwarmine.libs.Util;
import net.runedgaming.worldwarmine.libs.particle.FireworkEffectPlayer;
import net.runedgaming.worldwarmine.libs.particle.ParticleEffect;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

//~--- JDK imports ------------------------------------------------------------

public class PlayerListener implements Listener {
	int timesJumped = 0;
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	Date date = new Date();
	private final Map<Player, Long> jumps = new HashMap();
	private HashMap<String, Inventory> quiver = new HashMap<String, Inventory>();
	Random ran = new Random();

	private long remainingCooldown(Player player, long tptime)
			throws SQLException {
		Long lastPlayerPearl = WWMMain.lastTP.get(player);
		long cooldown = WWMMain.cooldown;

		if (player.hasPermission("wwm.vip")) {
			cooldown = WWMMain.cooldown / 2;
		}

		return (cooldown - (tptime - lastPlayerPearl.longValue()));
	}

	private boolean validthrow(Player player, long throwTime)
			throws SQLException {
		Long lastPlayerPearl = WWMMain.lastTP.get(player);
		long cooldown = WWMMain.cooldown;

		if (player.hasPermission("wwm.vip")) {
			cooldown = WWMMain.cooldown / 2;
		}

		if ((lastPlayerPearl == null)
				|| ((throwTime - lastPlayerPearl.longValue() >= cooldown))) {
			return true;
		}

		String test = String
				.format("%d " + ChatColor.RED + "minutes, " + "%d"
						+ ChatColor.RED + " seconds",
						TimeUnit.MILLISECONDS.toMinutes(remainingCooldown(
								player, throwTime)),
						TimeUnit.MILLISECONDS.toSeconds(remainingCooldown(
								player, throwTime))
								- TimeUnit.MINUTES
										.toSeconds(TimeUnit.MILLISECONDS
												.toMinutes(remainingCooldown(
														player, throwTime))));

		player.sendMessage(ChatColor.RED + "Teleport cooldown remaining: "
				+ test);

		return false;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Material to = event.getTo().getBlock().getType();

		if (player.getLocation().getY() <= -20) {
			player.teleport(player.getWorld().getSpawnLocation());
		}

		if (to.equals(Material.GOLD_PLATE)) {

			Vector normal = Util.VectorU.calculateLookVector(player
					.getLocation());

			normal.setY(0.75 + Math.abs(normal.getY()) * 2);
			event.getPlayer().setVelocity(normal);

			player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 10f, 1f);
			player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);
			player.getWorld().playEffect(player.getLocation(),
					Effect.MOBSPAWNER_FLAMES, 1);
			ParticleEffect.ANGRY_VILLAGER.display(player.getLocation(), 0, 0,
					0, 5, 100);

		} else if (to.equals(Material.IRON_PLATE)) {

			PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 20, 5);

			player.addPotionEffect(speed, true);
		} else if (to.equals(Material.ACTIVATOR_RAIL)) {
			Location location = new Location(player.getWorld(), event.getTo()
					.getX() - 1, event.getTo().getY(), event.getTo().getZ());
			double speed = 0.5;
			Vector dir = location.toVector()
					.subtract(player.getLocation().toVector()).normalize();
			player.setVelocity(dir.multiply(speed));
			;
		}
	}

	@EventHandler
	public void onVehicleExit(EntityDismountEvent event) {
		event.getDismounted().remove();
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();

		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				if (item.getItemMeta().getDisplayName().contains("Wand")) {
					IconMenu menu = new IconMenu("IconMenu", 1, new onClick() {
						@Override
						public boolean click(Player p, IconMenu menu, Row row,
								int slot, ItemStack item) {

							if (row.getRow() == 0) {
								Bukkit.broadcastMessage(Integer.toString(slot));
							}
							return true;
						}
					});
					menu.addButton(menu.getRow(0), 0, new ItemStack(
							Material.WOOL, 8), "Stone Button ;)");
					menu.addButton(menu.getRow(0), 1, new ItemStack(
							Material.WOOD), "Wood Button ;)");
					menu.addButton(menu.getRow(0), 2, new ItemStack(
							Material.DIAMOND), "Diamond Button ;)");
					menu.addButton(menu.getRow(0), 3, new ItemStack(
							Material.GOLD_BLOCK), "Gold Button ;)");
					menu.addButton(menu.getRow(0), 4, new ItemStack(
							Material.IRON_BLOCK), "Iron Button ;)");
					menu.addButton(menu.getRow(0), 5, new ItemStack(
							Material.OBSIDIAN), "Obby Button ;)");
					menu.addButton(menu.getRow(0), 6, new ItemStack(
							Material.ANVIL), "Anvil Button ;)");
					menu.addButton(menu.getRow(0), 7, new ItemStack(
							Material.STONE_BUTTON), "Button Button ;)");
					menu.addButton(menu.getRow(0), 8, new ItemStack(
							Material.PORTAL), "Portal Button ;)");
					menu.open(player);

					event.getItemDrop().getItemStack();

					int slot = 0;

					for (int i = 0; i < player.getInventory().getSize(); i++) {
						if (player.getInventory().getItem(i) == event
								.getItemDrop().getItemStack()) {

						}
					}
					player.updateInventory();
					event.setCancelled(true);
				}
			}
		}

	}

	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand() != null) {
			if (player.getItemInHand().getEnchantmentLevel(
					Enchantment.SILK_TOUCH) == 32) {
				(player.getWorld().spawn(player.getLocation().add(1, 0, 0),
						ExperienceOrb.class)).setExperience(event
						.getExpToDrop());

			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
			throws IllegalArgumentException, Exception {
		Long now = Long.valueOf(System.currentTimeMillis());
		Player player = event.getPlayer();

		if (player.getItemInHand().getType() != Material.AIR) {
			World world = player.getWorld();
			Block block = event.getClickedBlock();
			if (player.getItemInHand() != null) {
				if (player.getItemInHand().getItemMeta().hasDisplayName()) {
					if ((event.getAction() == Action.RIGHT_CLICK_BLOCK)
							|| (event.getAction() == Action.RIGHT_CLICK_AIR)) {

						if (player.getItemInHand().getItemMeta()
								.getDisplayName().equals("Wand")) {
							FireworkEffectPlayer.playFirework(
									player.getWorld(),
									player.getTargetBlock(null, 15)
											.getLocation(), FireworkEffect
											.builder().with(Type.BURST)
											.withColor(Color.WHITE).build());
							Util.createEffectLine(player);

							player.getWorld().createExplosion(
									player.getTargetBlock(null, 15)
											.getLocation(), 5);
						}
						
						if (player.getItemInHand().getItemMeta()
								.getDisplayName().contains("Smooch Wand")) {
							FireworkEffectPlayer.playFirework(player
									.getWorld(),
									player.getTargetBlock(null, 15)
											.getLocation(),
									Util.getRandomEffect());
							player.sendMessage("hi smoocfddhghssssss");
						}

						if (player.getItemInHand().getType() == Material.CLAY_BRICK) {
							if (player.getItemInHand().getItemMeta()
									.getDisplayName().equals("Jumper")) {
								Vector normal = Util.VectorU
										.calculateLookVector(player
												.getLocation());
															
								normal.setY(0.75 + Math.abs(normal.getY()) * 1);
								event.getPlayer().setVelocity(normal);

								player.playSound(player.getLocation(),
										Sound.WITHER_SHOOT, 10f, 1f);
								player.getWorld().playEffect(
										player.getLocation(), Effect.SMOKE, 1);
								player.getWorld().playEffect(
										player.getLocation(),
										Effect.MOBSPAWNER_FLAMES, 1);

							}
							
						}

						if (player.getItemInHand().getType() == Material.PAPER) {
							if (player.getItemInHand().getItemMeta()
									.getDisplayName()
									.equals("�bBlank �6Teleportation �bScroll")) {
								ItemStack testpaper = new ItemStack(
										Material.PAPER, player.getItemInHand()
												.getAmount() - 1);
								ItemMeta pmeta = testpaper.getItemMeta();
								java.util.List<String> rightclicklore = Arrays
										.asList("�7Right click to set the teleport location");

								pmeta.setDisplayName("�bBlank �6Teleportation �bScroll");
								pmeta.setLore(rightclicklore);
								testpaper.setItemMeta(pmeta);
								player.setItemInHand(testpaper);
								player.updateInventory();
								player.getItemInHand().setDurability(
										(short) (player.getItemInHand()
												.getDurability() + 10));

								Location loc = player.getLocation();
								Location b = loc.getBlock().getLocation();
								ItemStack boots = new ItemStack(Material.PAPER,
										1);
								ItemMeta item = boots.getItemMeta();

								item.setDisplayName("�6Teleportation �bScroll");

								java.util.List<String> listlore = Arrays
										.asList("�fOwner: " + player.getName(),
												"�fX: "
														+ player.getLocation()
																.getX(),
												"�fY: "
														+ player.getLocation()
																.getY(),
												"�fZ: "
														+ player.getLocation()
																.getZ());

								player.getLocation().getBlock().getLightLevel();

								item.setLore(listlore);
								boots.setItemMeta(item);

								if (player
										.getItemInHand()
										.getItemMeta()
										.getDisplayName()
										.equals("�bBlank �6Teleportation �bScroll")) {
									player.getInventory().addItem(boots);
								} else {
									player.setItemInHand(boots);
								}

								player.updateInventory();
							}

							if (player.getItemInHand().getItemMeta()
									.getDisplayName()
									.equals("�6Teleportation �bScroll")
									&& validthrow(player, now.longValue())) {
								String ots = player.getItemInHand()
										.getItemMeta().getLore().get(0);
								String xtosplit = player.getItemInHand()
										.getItemMeta().getLore().get(1);
								String ytosplit = player.getItemInHand()
										.getItemMeta().getLore().get(2);
								String ztosplit = player.getItemInHand()
										.getItemMeta().getLore().get(3);
								String[] xs = xtosplit.split("�fX: ");
								String[] os = ots.split("�fOwner: ");
								String[] ys = ytosplit.split("�fY: ");
								String[] zs = ztosplit.split("�fZ: ");
								double xd = Double.parseDouble(xs[1]);
								double yd = Double.parseDouble(ys[1]);
								double zd = Double.parseDouble(zs[1]);
								int x = (int) xd;
								int y = (int) yd;
								int z = (int) zd;
								Location scrloc = new Location(
										player.getWorld(), x, y, z);

								if (player.getName().contentEquals(os[1])) {
									player.teleport(scrloc);

									ItemStack testpaper = new ItemStack(
											Material.PAPER, player
													.getItemInHand()
													.getAmount() - 1);
									ItemMeta pmeta = testpaper.getItemMeta();

									WWMMain.lastTP.put(player, now);
									pmeta.setDisplayName("�6Teleportation �bScroll");
									pmeta.setLore(player.getItemInHand()
											.getItemMeta().getLore());
									testpaper.setItemMeta(pmeta);
									player.setItemInHand(testpaper);
									player.updateInventory();
									player.playEffect(player.getLocation(),
											Effect.ENDER_SIGNAL, 3);
									player.playEffect(player.getLocation(),
											Effect.GHAST_SHOOT, 10);
									player.setFallDistance(0);
								} else {
									player.sendMessage("Lol u rnt owner jerk. nyc tri");
								}
							}

							if (player.getItemInHand().getItemMeta()
									.getDisplayName().equals("Arrow")) {
								if (player.getInventory().contains(
										Material.ARROW)) {
									player.launchProjectile(Arrow.class);
									player.getInventory().removeItem(
											new ItemStack(Material.ARROW, 1));
									player.updateInventory();
								}
							}
						}
					}
				}
			}
		}
	}


	public void spawnHorseOn(Player p, Block b) {
		Bukkit.getWorld("world").playEffect(b.getLocation(),
				Effect.MOBSPAWNER_FLAMES, 0);

		p.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() - 1)
				.setType(Material.WOOL);
		p.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() + 1)
				.setType(Material.WOOL);
		p.getWorld().getBlockAt(b.getX(), b.getY() + 1, b.getZ())
				.setType(Material.WOOL);
		p.getWorld().getBlockAt(b.getX(), b.getY() + 2, b.getZ())
				.setType(Material.WOOL);
		p.getWorld().getBlockAt(b.getX(), b.getY() + 3, b.getZ())
				.setType(Material.WOOL);
		p.getWorld().getBlockAt(b.getX(), b.getY() + 4, b.getZ())
				.setType(Material.WOOL);
		p.getWorld().getBlockAt(b.getX(), b.getY() + 5, b.getZ())
				.setType(Material.WOOL);

		// p.getWorld().getBlockAt(b.getX(), b.getY(), b.getZ() -
		// 1).setData((byte) 6);

		// menu.open(p);
		/*
		 * HorseModifier hm = HorseModifier.spawn(p.getLocation());
		 * hm.setType(HorseType.NORMAL); hm.setVariant(HorseVariant.BLACK);
		 * hm.setTamed(true); hm.setSaddled(true);
		 * hm.getHorse().setPassenger(p); hm.getHorse().setMaxHealth(20);
		 */
	}
}