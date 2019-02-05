package net.runedgaming.worldwarmine.libs;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTTagList;
import net.runedgaming.worldwarmine.libs.particle.ParticleEffect;

import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Util {
	public static int randInt(int min, int max) {

		// Usually this can be a field rather than a method variable
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static class VectorU {
		public static @Nonnull
		Vector calculateLookVector(@Nonnull Location location) {
			double pitch = Math.toRadians(location.getPitch());
			double yaw = Math.toRadians(location.getYaw());

			Vector normal = new Vector(-(Math.cos(pitch) * Math.sin(yaw)),
					-Math.sin(pitch), Math.cos(pitch) * Math.cos(yaw));

			return normal;
		}
	}

	/**
	 * 
	 * @param centerLoc
	 *            - Central Location
	 * @param radius
	 *            - Distance in blocks from the "centerLoc"
	 * @return Circle
	 * @note - it will return only the blocks that are in the "radius" position.
	 */
	public static List<Location> getCircle(Location centerLoc, int radius) {
		List<Location> circle = new ArrayList<Location>();
		World world = centerLoc.getWorld();
		int x = 0;
		int z = radius;
		int error = 0;
		int d = 2 - 2 * radius;
		while (z >= 0) {
			circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc
					.getY(), centerLoc.getBlockZ() + z));
			circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc
					.getY(), centerLoc.getBlockZ() + z));
			circle.add(new Location(world, centerLoc.getBlockX() - x, centerLoc
					.getY(), centerLoc.getBlockZ() - z));
			circle.add(new Location(world, centerLoc.getBlockX() + x, centerLoc
					.getY(), centerLoc.getBlockZ() - z));
			error = 2 * (d + z) - 1;
			if ((d < 0) && (error <= 0)) {
				x++;
				d += 2 * x + 1;
			} else {
				error = 2 * (d - x) - 1;
				if ((d > 0) && (error > 0)) {
					z--;
					d += 1 - 2 * z;
				} else {
					x++;
					d += 2 * (x - z);
					z--;
				}
			}
		}
		return circle;
	}

	/**
	 * 
	 * @param position1
	 *            - First position
	 * @param position2
	 *            - Second position
	 * @return Cuboid
	 * @note - it will return all the blocks from "position1" till "position2".
	 * @credits - CaptainBern
	 */
	public static List<Location> getCuboid(Location position1,
			Location position2) {

		if (position1.getWorld().getName() != position2.getWorld().getName()) {
			throw new UnsupportedOperationException(
					"'Position1' and 'Position2' location need to be in the same world!");
		}

		List<Location> cube = new ArrayList<Location>();

		int minX = (int) Math.min(position1.getX(), position2.getX());
		int maxX = (int) Math.max(position1.getX(), position2.getX());

		int minY = (int) Math.min(position1.getY(), position2.getY());
		int maxY = (int) Math.max(position1.getY(), position2.getY());

		int minZ = (int) Math.min(position1.getZ(), position2.getZ());
		int maxZ = (int) Math.max(position1.getZ(), position2.getZ());

		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					cube.add(new Location(position1.getWorld(), x, y, z));
				}
			}
		}
		return cube;
	}

	/**
	 * 
	 * @param position1
	 *            - First position
	 * @param position2
	 *            - Second position
	 * @return Plain Cuboid
	 * @note - it will return only the blocks that are in the same level as the
	 *       "position1" and till the "position2".
	 */
	public static List<Location> getPlain(Location position1, Location position2) {
		List<Location> plain = new ArrayList<Location>();
		if (position1 == null)
			return plain;
		if (position2 == null)
			return plain;
		for (int x = Math.min(position1.getBlockX(), position2.getBlockX()); x <= Math
				.max(position1.getBlockX(), position2.getBlockX()); x++)
			for (int z = Math.min(position1.getBlockZ(), position2.getBlockZ()); z <= Math
					.max(position1.getBlockZ(), position2.getBlockZ()); z++)
				plain.add(new Location(position1.getWorld(), x, position1
						.getBlockY(), z));
		return plain;
	}

	/**
	 * 
	 * @param position1
	 *            - First position
	 * @param position2
	 *            - Second position
	 * @param getOnlyAboveGround
	 *            - boolean (see the notes);
	 * @return Cuboid
	 * @note1 - if "land" is activated, it will return air blocks only one block
	 *        above the ground;
	 * @note2 - if "land" is deactivated, it will return only the air blocks in
	 *        the cuboid.
	 */
	public static List<Location> getBlocks(Location position1,
			Location position2, boolean getOnlyAboveGround) {
		List<Location> blocks = new ArrayList<Location>();
		if (position1 == null)
			return blocks;
		if (position2 == null)
			return blocks;

		for (int x = Math.min(position1.getBlockX(), position2.getBlockX()); x <= Math
				.max(position1.getBlockX(), position2.getBlockX()); x++)
			for (int z = Math.min(position1.getBlockZ(), position2.getBlockZ()); z <= Math
					.max(position1.getBlockZ(), position2.getBlockZ()); z++)
				for (int y = Math.min(position1.getBlockY(),
						position2.getBlockY()); y <= Math.max(
						position1.getBlockY(), position2.getBlockY()); y++) {
					Block b = position1.getWorld().getBlockAt(x, y, z);
					if ((b.getType() == Material.AIR)
							&& ((!getOnlyAboveGround) || (b.getRelative(
									BlockFace.DOWN).getType() != Material.AIR)))
						blocks.add(b.getLocation());
				}
		return blocks;
	}

	/**
	 * 
	 * @param position1
	 *            - First position
	 * @param position2
	 *            - Second position
	 * @return Line
	 * @note - it will return only the blocks that are in diagonal from
	 *       "position1" till "position2".
	 */
	public static List<Location> getLine(Location position1, Location position2) {
		List<Location> line = new ArrayList<Location>();
		int dx = Math.max(position1.getBlockX(), position2.getBlockX())
				- Math.min(position1.getBlockX(), position2.getBlockX());
		int dy = Math.max(position1.getBlockY(), position2.getBlockY())
				- Math.min(position1.getBlockY(), position2.getBlockY());
		int dz = Math.max(position1.getBlockZ(), position2.getBlockZ())
				- Math.min(position1.getBlockZ(), position2.getBlockZ());
		int x1 = position1.getBlockX();
		int x2 = position2.getBlockX();
		int y1 = position1.getBlockY();
		int y2 = position2.getBlockY();
		int z1 = position1.getBlockZ();
		int z2 = position2.getBlockZ();
		int x = 0;
		int y = 0;
		int z = 0;
		int i = 0;
		int d = 1;
		switch (getHighest(dx, dy, dz)) {
		case 1:
			i = 0;
			d = 1;
			if (x1 > x2)
				d = -1;
			x = position1.getBlockX();
			do {
				i++;
				y = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
				z = z1 + (x - x1) * (z2 - z1) / (x2 - x1);
				line.add(new Location(position1.getWorld(), x, y, z));
				x += d;
			} while (i <= Math.max(x1, x2) - Math.min(x1, x2));
			break;
		case 2:
			i = 0;
			d = 1;
			if (y1 > y2)
				d = -1;
			y = position1.getBlockY();
			do {
				i++;
				x = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
				z = z1 + (y - y1) * (z2 - z1) / (y2 - y1);
				line.add(new Location(position1.getWorld(), x, y, z));
				y += d;
			} while (i <= Math.max(y1, y2) - Math.min(y1, y2));
			break;
		case 3:
			i = 0;
			d = 1;
			if (z1 > z2)
				d = -1;
			z = position1.getBlockZ();
			do {
				i++;
				y = y1 + (z - z1) * (y2 - y1) / (z2 - z1);
				x = x1 + (z - z1) * (x2 - x1) / (z2 - z1);
				line.add(new Location(position1.getWorld(), x, y, z));
				z += d;
			} while (i <= Math.max(z1, z2) - Math.min(z1, z2));
		}

		return line;
	}

	// support
	public static int getHighest(int x, int y, int z) {
		if ((x >= y) && (x >= z))
			return 1;
		if ((y >= x) && (y >= z))
			return 2;
		return 3;
	}

	/**
	 * 
	 * @param location
	 *            - Initial location
	 * @param radius
	 *            - distance from the "location" that will return all the
	 *            entities from each block;
	 * @return HashSet(LivingEntity)
	 * @note - it will return only Living Entities in a radius, such as players,
	 *       mobs and animals.
	 * @credits - skore87 (little modification by me)
	 */
	public static HashSet<LivingEntity> getNearbyEntities(Location location,
			int radius) {
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16)) / 16;
		HashSet<LivingEntity> radiusEntities = new HashSet<LivingEntity>();

		for (int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
			for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
				int x = (int) location.getX(), y = (int) location.getY(), z = (int) location
						.getZ();
				for (Entity e : new Location(location.getWorld(), x
						+ (chX * 16), y, z + (chZ * 16)).getChunk()
						.getEntities()) {
					if (e.getLocation().distance(location) <= radius
							&& e.getLocation().getBlock() != location
									.getBlock())
						if (e instanceof LivingEntity) {
							radiusEntities.add((LivingEntity) e);
						}
				}
			}
		}
		return radiusEntities;
	}
	
	public static void createEffectLine(Player player) {
		Location pl = player.getEyeLocation();
		 
		double px = pl.getX();
		double py = pl.getY();
		double pz = pl.getZ();
		 
		double yaw  = Math.toRadians(pl.getYaw() + 90);
		double pitch = Math.toRadians(pl.getPitch() + 90);
		 
		double x = Math.sin(pitch) * Math.cos(yaw);
		double y = Math.sin(pitch) * Math.sin(yaw);
		double z = Math.cos(pitch);
		 
		for(int i = 1 ; i <= 10 ; i++) {
		    Location loc = new Location(player.getWorld(), px + i*x, py + i*z, pz + i*y);
		    if(loc.getBlock().getType() == Material.AIR)
		    	ParticleEffect.BUBBLE.display(loc, 5, 5, 5, 10, 20);
		    else break;
		}
	}
	
	public static ItemStack addGlow(ItemStack item){   
		net.minecraft.server.v1_6_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
	    NBTTagCompound tag = null;
	    if (!nmsStack.hasTag()) {
	        tag = new NBTTagCompound();
	        nmsStack.setTag(tag);
	    }
	    if (tag == null) tag = nmsStack.getTag();
	    NBTTagList ench = new NBTTagList();
	    tag.set("ench", ench);
	    nmsStack.setTag(tag);
	    return CraftItemStack.asCraftMirror(nmsStack);
	}
	
	public static FireworkEffect getRandomEffect() {

		Random r = new Random();

		// Get the type
		int rt = r.nextInt(4) + 1;
		Type type = Type.BALL;
		if (rt == 1)
			type = Type.BALL;
		if (rt == 2)
			type = Type.BALL_LARGE;
		if (rt == 3)
			type = Type.BURST;
		if (rt == 4)
			type = Type.CREEPER;
		if (rt == 5)
			type = Type.STAR;

		// Get our random colours
		int r1i = r.nextInt(17) + 1;
		int r2i = r.nextInt(17) + 1;
		Color c1 = getColor(r1i);
		Color c2 = getColor(r2i);

		// Create our effect with this
		FireworkEffect effect = FireworkEffect.builder()
				.flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type)
				.trail(r.nextBoolean()).build();

		return effect;
	}

	private static Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}
	
}
