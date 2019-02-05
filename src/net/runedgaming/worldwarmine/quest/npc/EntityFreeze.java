package net.runedgaming.worldwarmine.quest.npc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.runedgaming.worldwarmine.WWMMain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
 
/**
*
* @author Goblom
*/
public class EntityFreeze implements Listener {
 
    public static Map<UUID, Location> frozen = new HashMap();
    private Set<BukkitTask> tasks = new HashSet<BukkitTask>();
 
    private BukkitScheduler bs = Bukkit.getScheduler();
    private PluginManager pm = Bukkit.getPluginManager();
    
    public EntityFreeze() {
        tasks.add(bs.runTaskTimer(WWMMain.getPlugin(), new Freeze(), 0, 20L)); //plugin represents your JavaPlugin
        pm.registerEvents(this, WWMMain.getPlugin());
    }
 
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }
 
    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        for (LivingEntity ent : event.getWorld().getLivingEntities()) {
            UUID id = ent.getUniqueId();
            if (frozen.containsKey(id)) frozen.remove(id);
        }
    }
 
    private class Freeze implements Runnable {
        @Override
        public void run() {
            for (World world : Bukkit.getWorlds()) {
                for (LivingEntity ent : world.getLivingEntities()) {
                    UUID id = ent.getUniqueId();
                    if (frozen.containsKey(id)) ent.teleport(frozen.get(id));
                }
            }
        }
    }
}
