package net.runedgaming.worldwarmine.cmd;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.runedgaming.worldwarmine.WWMMain;
import net.runedgaming.worldwarmine.libs.Attributes;
import net.runedgaming.worldwarmine.libs.PacketUtils;
import net.runedgaming.worldwarmine.libs.Util;
import net.runedgaming.worldwarmine.libs.Attributes.Attribute;
import net.runedgaming.worldwarmine.libs.Attributes.AttributeType;
import net.runedgaming.worldwarmine.libs.particle.ParticleEffect;
import net.runedgaming.worldwarmine.quest.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftVillager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.utility.MinecraftReflection;

import ca.wacos.nametagedit.NametagAPI;

public class CommandExec implements CommandExecutor {

	private WWMMain plugin; // pointer to your main class, unrequired if you
							// don't need methods from the main class

	public CommandExec(WWMMain plugin) {
		this.plugin = plugin;
	}

	public int escudo = 0;

	public int getEscudo() {
		return escudo;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command c, String l,
			String[] args) {
		final Player player = (Player) cs;
		int length = args.length;

		String cmd = "null";

		if (l.equalsIgnoreCase("wwm")) {

			if (length >= 1) {
				cmd = args[0];
			} else if (length == 0) {
				player.sendMessage(ChatColor.RED + "Invalid command. Use"
						+ ChatColor.GOLD + " /www help" + ChatColor.RED
						+ " for help.");
			}

			if (cmd.equalsIgnoreCase("crate")) {

				player.getWorld().spawnFallingBlock(
						new Location(player.getLocation().getWorld(), player
								.getLocation().getBlockX(), player
								.getLocation().getBlockY() + 30, player
								.getLocation().getBlockZ()),
						Material.PISTON_BASE, (byte) 6);
				return true;

			} else if (cmd.equalsIgnoreCase("help")) {
				player.sendMessage(ChatColor.YELLOW
						+ "/--------- WorldWarMine Help ---------/");
				NametagAPI.setPrefix(player.getName(), "[§aDeveloper§f] ");

				return true;
			} else if (cmd.equalsIgnoreCase("item")) {
				if (length >= 1) {
					if (args[1].equalsIgnoreCase("create")) {

						StringBuilder ItemName = new StringBuilder();

						if (length >= 4) {

							ItemStack item = new ItemStack(
									Material.getMaterial(Integer
											.parseInt(args[3])), 1);

							ItemMeta meta = item.getItemMeta();

							for (int i = 0; i < length; i++) {

								if (i > 4) {

									ItemName.append(args[i] + " ");
								}

							}

							String name = ItemName.toString();

							meta.setDisplayName(name);

							// Set the item damage in the lore

							List<String> lore = Arrays.asList(null, null, "§c"
									+ args[2] + " - " + args[3]
									+ " Attack Damage");

							// Set the lore to the array we created

							meta.setLore(lore);

							item.setItemMeta(meta);

							Attributes attributes = new Attributes(item);

							attributes.add(Attribute.newBuilder()
									.name("Health")
									.type(AttributeType.GENERIC_ATTACK_DAMAGE)
									.amount(0).build());

							player.getInventory()
									.addItem(attributes.getStack());
							player.updateInventory();
						}
					}
				} else {

				}

				return true;
			} else if (cmd.equalsIgnoreCase("jumper")) {
				// PacketUtils.displayLoadingBar("Reloading...", "Reloaded!",
				// player, 10, true);

				ItemStack item = new ItemStack(Material.CLAY_BRICK, 1);
				ItemMeta itemm = item.getItemMeta();

				// item = MinecraftReflection.getBukkitItemStack(item);
				// item.addUnsafeEnchantment(Enchantment.getById(33), 32);

				itemm.setDisplayName("Jumper");
				item.setItemMeta(itemm);

				item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 32);

				player.getInventory().addItem(item);

				/*
				 * Villager villager = (Villager) player.getWorld().spawnEntity(
				 * player.getLocation(), EntityType.VILLAGER);
				 * villager.setProfession(Profession.FARMER);
				 * 
				 * PotionEffect p = new PotionEffect(PotionEffectType.SLOW,
				 * Integer.MAX_VALUE, 5);
				 * 
				 * villager.addPotionEffect(p);
				 */

				player.updateInventory();
				return true;
			} else if (cmd.equalsIgnoreCase("test")) {
				// Util.spawnTornado(plugin, player.getLocation(),
				// Material.LAVA, (byte) 0, null, 0.5, 200, 30000L, true, true);

				ItemMeta pmeta = player.getItemInHand().getItemMeta();
				java.util.List<String> rightclicklore = Arrays
						.asList("§7Right click to soar high in the sky!");

				pmeta.setDisplayName("§bJumper");
				pmeta.setLore(rightclicklore);
				player.getItemInHand().setItemMeta(pmeta);
				player.updateInventory();
				
			} else if (cmd.equalsIgnoreCase("chest")) {
				if (args[1].equalsIgnoreCase("r")) {
					World world;

					if (args.length == 3) {
						world = Bukkit.getWorld(args[2]);
					} else {
						world = player.getWorld();
					}
					;

					for (Chunk ch : world.getLoadedChunks()) {
						for (BlockState te : ch.getTileEntities()) {
							if (te instanceof Chest) {
								Chest chest = (Chest) te;
								Inventory ci = chest.getInventory();

								ci.clear();

								for (int i = 0; i < Util.randInt(0,
										ci.getSize()); i++) {

									Random random = new Random();
									Material[] materials = Material.values();
									int size = materials.length;
									int index = random.nextInt(size);
									Material randomMaterial = materials[index];
									if (!randomMaterial.isBlock()) {
										ItemStack item = new ItemStack(
												randomMaterial, Util.randInt(1,
														64));
										ci.addItem(item);
									}

								}
							}
						}

					}
					player.sendMessage("All chests set to repopulate.");
				} else {
					player.sendMessage(ChatColor.RED + "Invalid command. Use"
							+ ChatColor.GOLD + " /www help" + ChatColor.RED
							+ " for help.");
				}
				return true;
			} else if (args.length != 0) {
				player.sendMessage(ChatColor.RED + "Invalid command. Use"
						+ ChatColor.GOLD + " /www help" + ChatColor.RED
						+ " for help.");
				return true;
			}
		}
		return false;
	}
}
