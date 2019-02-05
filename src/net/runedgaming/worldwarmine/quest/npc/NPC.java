package net.runedgaming.worldwarmine.quest.npc;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NPC {
	public NPC(Location l, EntityType et, String n) {
		LivingEntity e = (LivingEntity) l.getWorld().spawnEntity(l, et);
		
		e.setCustomName(n);
		e.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128));
		e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 6));
		e.setCustomNameVisible(true);
		
		EntityFreeze.frozen.put(e.getUniqueId(), l);
			
		//System.out.println(e.getUniqueId());
	}
}
