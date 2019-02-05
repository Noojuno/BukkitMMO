package net.runedgaming.worldwarmine;

//~--- non-JDK imports --------------------------------------------------------

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
//~--- JDK imports ------------------------------------------------------------

public class EntityListener implements Listener {
    private final Map<Player, Long> shot = new HashMap();

    @EventHandler
    public void onEntityDeath(ProjectileHitEvent event) {
        if (event.getEntity() instanceof WitherSkull) {
            for (Entity entity : event.getEntity().getNearbyEntities(7, 7, 7)) {
                if (entity instanceof Player) {
                    if (entity != event.getEntity().getShooter()) {
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 240, 1));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof WitherSkull) {
            event.blockList().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player          = (Player) event.getEntity();
            Long   lastPlayerPearl = WWMMain.lastTP.get(player);

            if (lastPlayerPearl == null) {
                lastPlayerPearl = 0L;
            }

            if ((event.getEntity() instanceof Player)
                    && (Long.valueOf(System.currentTimeMillis()).longValue() - lastPlayerPearl <= 10000)
                    && (event.getCause() == DamageCause.FALL)) {
                event.setCancelled(true);
            }

            String s = String.valueOf(Long.valueOf(System.currentTimeMillis()).longValue() - lastPlayerPearl);
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
