package net.runedgaming.worldwarmine;

//~--- non-JDK imports --------------------------------------------------------

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import net.runedgaming.worldwarmine.cmd.CommandExec;
import net.runedgaming.worldwarmine.damage.DamageListener;
import net.runedgaming.worldwarmine.items.storage.BankListener;
import net.runedgaming.worldwarmine.items.storage.CrateListener;
import net.runedgaming.worldwarmine.libs.MySQL;
import net.runedgaming.worldwarmine.player.PlayerInventoryListener;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
//~--- JDK imports ------------------------------------------------------------

public final class WWMMain extends JavaPlugin {
	public static long cooldown = 300000L;
	public static Connection c = null;
	public static Map<Player, Long> lastTP = new HashMap();
	public MySQL MySQL = new MySQL("host", "3306", "db", "user", "pass"); //Connection with dummy password and test host
	public static final WWMMain instance = new WWMMain();
	public static PluginManager pm = Bukkit.getPluginManager();

	public static final WWMMain getPlugin() {
		return instance;
	}

	@Override
	public void onEnable() {

		getLogger().info("[RUNEDMMO] Runed MMO is starting up.");
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new DamageListener(), this);
		pm.registerEvents(new BankListener(), this);
		pm.registerEvents(new CrateListener(), this);
		pm.registerEvents(new PlayerInventoryListener(), this);
		pm.registerEvents(new EntityListener(), this);

		c = MySQL.open();
		setup();

		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {

			}
			// Do something
		}, 0L, 20L);

	}

	public void log(String s) {
		getLogger().info(s);
	}

	@Override
	public void onDisable() {
		getLogger().info("[WorldWarMine] WorldWarMine is starting up");

	}

	public int getBalance(Player p) {
		return 2000000;
	}

	public void setup() {
		setupGlow();

		getCommand("wwm").setExecutor(new CommandExec(this));
		getCommand("wwma").setExecutor(new CommandExec(this));

	}

	public void setupGlow() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
				this, ConnectionSide.SERVER_SIDE, ListenerPriority.HIGH, 
				Packets.Server.SET_SLOT, Packets.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketID() == Packets.Server.SET_SLOT) {
					addGlow(new ItemStack[] { event.getPacket().getItemModifier().read(0) });
				} else {
					addGlow(event.getPacket().getItemArrayModifier().read(0));
				}
			}
		});
	}
	
	private void addGlow(ItemStack[] stacks) {
		for (ItemStack stack : stacks) {
			if (stack != null) {
				// Only update those stacks that have our flag enchantment
				if (stack.getEnchantmentLevel(Enchantment.ARROW_FIRE) == 32) {
					NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(stack);
					compound.put(NbtFactory.ofList("ench"));
				}
			}
		}
	}
}


