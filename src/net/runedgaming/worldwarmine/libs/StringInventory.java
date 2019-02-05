package net.runedgaming.worldwarmine.libs;

import net.runedgaming.worldwarmine.WWMMain;
import net.runedgaming.worldwarmine.player.PlayerInventoryListener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

//~--- JDK imports ------------------------------------------------------------

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StringInventory {
	public static String StringFromInventory(Inventory inventory, int type) {
		List<String> x = new ArrayList<String>();
		int i = 0;

		for (ItemStack stackList : inventory.getContents()) {
			if (type != 2) {
				if ((stackList == null)
						|| (stackList.getType() == Material.AIR)) {
					i++;
				}
			}

			if ((stackList == null)) {
				x.add("blank");
			} else {
				if (stackList != null) {
					int itemid = stackList.getTypeId();
					int count = stackList.getAmount();
					int slot = i;
					int durability = stackList.getDurability();
					ItemMeta itemmeta = stackList.getItemMeta();
					String name = "";
					String enchants = "empty&empty";
					int enchant1 = 0;
					Map<Enchantment, Integer> enchantments = stackList
							.getEnchantments();

					for (Enchantment cEnchantment : enchantments.keySet()) {
						enchants = enchants + "@" + cEnchantment.getName()
								+ "&"
								+ enchantments.get(cEnchantment).intValue();
					}

					if (itemmeta.getDisplayName() == null) {
						name = "empty";
					} else {
						name = itemmeta.getDisplayName();
					}

					String lore = "empty";
					String endstring = "thismustbeset";
					int loresize = 0;

					if (itemmeta.getLore() != null) {
						if (itemmeta.getLore().size() != 0) {
							loresize = itemmeta.getLore().size();
						}
					}

					if (loresize >= 0) {
						for (int y = 0; y < loresize; y = y + 1) {
							if (y == 0) {
								lore = itemmeta.getLore().get(y);
							} else {
								lore = lore + ";" + itemmeta.getLore().get(y);
							}
						}

						endstring = itemid + ";" + count + ";" + slot + ";"
								+ durability + ";" + name + ";" + enchants
								+ ";" + lore;
					} else {
						endstring = itemid + ";" + count + ";" + slot + ";"
								+ durability + ";" + name + ";" + enchants
								+ "empty" + ";" + "empty" + ";" + "empty" + ";"
								+ "empty" + ";" + "empty";
					}

					x.add(endstring);
					i++;
				}
			}
		}

		String teststring = "";

		if (x.size() > 0) {
			teststring = x.get(0);

			for (int y = 1; y < x.size(); y = y + 1) {
				teststring = teststring + "~" + x.get(y);
			}

			return teststring;
		} else {
			return null;
		}
	}

	public static Inventory StringToInventory(String inventory, int size) {
		String[] is = inventory.split("~");
		Inventory bank1 = Bukkit.createInventory(null, size);

		if (inventory.contentEquals("empty")) {
			Inventory bank11 = Bukkit.createInventory(null, size);

			return bank11;
		} else {
			for (int i = 0; i < is.length; i++) {
				if (is[i].toString().contentEquals("blank")) {
					bank1.addItem(new ItemStack(Material.AIR));
				} else {
					String[] itemarray = is[i].split(";");
					int itemid = Integer.parseInt(itemarray[0]);
					int count = Integer.parseInt(itemarray[1]);
					int slot = Integer.parseInt(itemarray[2]);
					ItemStack itemstack = new ItemStack(itemid, count);
					ItemMeta itemmeta = itemstack.getItemMeta();

					itemstack.setDurability(Short.parseShort(itemarray[3]));

					if (itemarray[4].toString().contentEquals("empty")) {
					} else {
						itemmeta.setDisplayName(itemarray[4]);
					}

					java.util.List<String> listlore = new ArrayList<String>();
					String[] enchantments = itemarray[5].split("@");

					for (int e = 0; e < enchantments.length; e++) {
						String[] enchantmentset = enchantments[e].split("&");

						if ((enchantmentset[0].toString().contentEquals("empty"))) {
						} else {
							itemmeta.addEnchant(
									Enchantment.getByName(enchantmentset[0]),
									Integer.parseInt(enchantmentset[1]), true);
						}
					}

					if (itemarray.length > 6) {
						if (itemarray[6].toString().contentEquals("empty")) {
						} else {
							listlore.add(itemarray[6]);
						}
					}

					if (itemarray.length > 7) {
						if (itemarray[7].toString().contentEquals("empty")) {
						} else {
							listlore.add(itemarray[7]);
						}
					}

					if (itemarray.length > 8) {
						if (itemarray[8].toString().contentEquals("empty")) {
						} else {
							listlore.add(itemarray[8]);
						}
					}

					if (itemarray.length > 9) {
						if (itemarray[9].toString().contentEquals("empty")) {
						} else {
							listlore.add(itemarray[9]);
						}
					}

					itemmeta.setLore(listlore);
					itemstack.setItemMeta(itemmeta);
					bank1.setItem(slot, itemstack);
				}
			}
		}

		return bank1;
	}

	public static Inventory getPlayerInventory(Player player, String iname, int put, int size)
			throws SQLException {
		Player p = player;
		Statement statement = WWMMain.c.createStatement();
		ResultSet res = statement
				.executeQuery("SELECT * FROM inventorys WHERE player = '"
						+ p.getName() + "';");

		res.next();

		if (iname != "inventory") {
			if (res.getString(iname) == null) {
				PlayerInventoryListener.bank.put(p, put);

				Inventory inv = Bukkit.createInventory(null, size);

				return inv;
			} else if (res.getString(iname) != null) {
				PlayerInventoryListener.bank.put(p, put);

				return StringInventory.StringToInventory(res.getString(iname), size);
			}

			return null;
		}
		return null;
	}
}