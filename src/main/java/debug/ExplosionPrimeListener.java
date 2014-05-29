package debug;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.metadata.MetadataValue;

import com.gmail.uprial.customnukes.CustomNukes;

public final class ExplosionPrimeListener implements Listener {
	
	private final CustomNukes plugin;
	
	public ExplosionPrimeListener(CustomNukes plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onExplosionPrime(ExplosionPrimeEvent event) {
	    Entity entity = event.getEntity();
	    plugin.global_log("explosion-prime");
	    plugin.global_log(entity.getMetadata("explosive").toString());
	    plugin.global_log(entity.getType().toString());
	    //entity.getMetadata(arg0)
	    // If the event is about primed TNT (TNT that is about to explode), then do something
	    if (entity instanceof TNTPrimed) {
	    	TNTPrimed tnt = (TNTPrimed)entity;
		    plugin.global_log("tnt");
			
	    	Entity source = tnt.getSource();
	    	if(null != source) {
	    		List<MetadataValue> m = source.getMetadata("explosive");
	    		if(null != m)
	    			plugin.global_log(m.toString());
		    	plugin.global_log(source.getType().toString());
	    	}
	        //entity.getWorld().createExplosion(entity.getLocation(), 0);
	    }
	    else if(!event.isCancelled()) {
//	    	event.setRadius(0);
	    }
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		plugin.global_log(String.format("block-place: %s %d %d %d", event.getBlock().getType().toString(), event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ()));
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemSpawn(ItemSpawnEvent event) {
		plugin.global_log("entity-spawn" + event.getEntity().getType().toString());
		plugin.global_log("entity-spawn" + event.getEntity().getItemStack().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		plugin.global_log("entity-death" + event.getEntity().getType().toString());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
		plugin.global_log("entity-player-leash" + event.getEntity().getType().toString());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionSplash(PotionSplashEvent event) {
		plugin.global_log("entity-potion-splash" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		plugin.global_log("entity-pro-hits" + event.getEntity().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		plugin.global_log("entity-pro-lau" + event.getEntity().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSheepDyeWool(SheepDyeWoolEvent event) {
		plugin.global_log("entity-sheep-dye" + event.getEntity().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSheepRegrowWool(SheepRegrowWoolEvent event) {
		plugin.global_log("entity-sheep-regrow" + event.getEntity().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSlimeSplit(SlimeSplitEvent event) {
		plugin.global_log("entity-slime-split" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
//		//plugin.global_log("entity-explode" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		plugin.global_log("entity-change-e" + event.getEntity().getType().toString());
		plugin.global_log("entity-change-b" + event.getBlock().getType().toString());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		//plugin.global_log("creature-spawn" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCreatePortal(EntityCreatePortalEvent event) {
		plugin.global_log("entity-create-portal" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCombustByBlock(EntityCombustByBlockEvent event) {
		plugin.global_log("entity-combust-by-block-e" + event.getEntity().getType().toString());
		plugin.global_log("entity-combust-by-block-b" + event.getCombuster().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		plugin.global_log("entity-combust-by-entity-e1" + event.getEntity().getType().toString());
		plugin.global_log("entity-combust-by-entity-e2" + event.getCombuster().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		plugin.global_log("entity-damage-by-block-e" + event.getEntity().getType().toString());
		if(null != event.getDamager())
			plugin.global_log("entity-damage-by-block-b" + event.getDamager().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		plugin.global_log("entity-damage-by-entity-e1" + event.getEntity().getType().toString());
		if(null != event.getDamager())
			plugin.global_log("entity-damage-by-entity-e2" + event.getDamager().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreeperPower(CreeperPowerEvent event) {
		plugin.global_log("entity-creeper" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		plugin.global_log("block-break" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityBreakDoor(EntityBreakDoorEvent event) {
		plugin.global_log("block-break-door" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDispense(BlockDispenseEvent event) {
		plugin.global_log("block-disp" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockRedstone(BlockRedstoneEvent event) {
		plugin.global_log("block-redstone" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityCombust(EntityCombustEvent event) {
		//plugin.global_log("block-redstone" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockExp(BlockExpEvent event) {
		plugin.global_log("block-exp" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		plugin.global_log("block-can-build" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFade(BlockFadeEvent event) {
		plugin.global_log("block-fade" + event.getBlock().getType().toString());
	}
		
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBurn(BlockBurnEvent event) {
		plugin.global_log("block-burn" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDamage(BlockDamageEvent event) {
		plugin.global_log("block-damage" + event.getBlock().getType().toString());
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockForm(BlockFormEvent event) {
		plugin.global_log("block-form" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {
		plugin.global_log("block-form-to" + event.getBlock().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockIgnite(BlockIgniteEvent event) {
		plugin.global_log("block-ignite" + event.getBlock().getType().toString());
	}
	/*@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPiston(BlockPistonEvent event) {
		plugin.global_log("block-piston" + event.getBlock().getType().toString());
	}*/
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		plugin.global_log(String.format("block-piston-extend: %s %d %d", event.getBlock().getType().toString(), event.getBlock().getX(), event.getBlock().getZ()));
		plugin.global_log(String.format("block-piston-extend-d: %d %d", event.getDirection().getModX(), event.getDirection().getModZ()));  
		plugin.global_log("block-piston-extend-l" + String.valueOf(event.getLength()));
		for(int i = 0; i < event.getBlocks().size(); i++) {
			Block b = event.getBlocks().get(i);
			plugin.global_log(String.format("block-piston-extend# %d : %s %d %d",  i, b.getType().toString(), b.getX(), b.getZ()));
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		plugin.global_log(String.format("block-piston-retract: %s %d %d", event.getBlock().getType().toString(), event.getBlock().getX(), event.getBlock().getZ()));
		plugin.global_log(String.format("block-piston-retract-d: %d %d", event.getDirection().getModX(), event.getDirection().getModZ()));  
		plugin.global_log(String.format("block-piston-retract-l: %d %d", event.getRetractLocation().getBlockX(), event.getRetractLocation().getBlockZ()));
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		//plugin.global_log("block-phy");
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onLeavesDecay(LeavesDecayEvent event) {
		plugin.global_log("block-leaves-decay" + event.getBlock().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onNotePlay(NotePlayEvent event) {
		plugin.global_log("block-note-play" + event.getBlock().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		plugin.global_log("block-sign" + event.getBlock().getType().toString());
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockSpread(BlockSpreadEvent event) {
		plugin.global_log("block-spread" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockGrow(BlockGrowEvent event) {
		plugin.global_log("block-grow" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		plugin.global_log("entity-block-from" + event.getBlock().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityInteract(EntityInteractEvent event) {
		plugin.global_log("entity-interact" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityPortalEnter(EntityPortalEnterEvent event) {
		plugin.global_log("entity-portal-enter" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityPortalExit(EntityPortalExitEvent event) {
		plugin.global_log("entity-portal-exit" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		plugin.global_log("entity-regain-health" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityShootBow(EntityShootBowEvent event) {
		plugin.global_log("entity-shoot-bow" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTame(EntityTameEvent event) {
		plugin.global_log("entity-tame" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {
		plugin.global_log("entity-target" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTeleport(EntityTeleportEvent event) {
		plugin.global_log("entity-teleport" + event.getEntity().getType().toString());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		plugin.global_log("entity-target-living" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityUnleash(EntityUnleashEvent event) {
		plugin.global_log("entity-unleash" + event.getEntity().getType().toString());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onExpBottle(ExpBottleEvent event) {
		plugin.global_log("entity-exp-bottle" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		plugin.global_log("entity-food-level" + event.getEntity().getType().toString());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onHorseJumpEvent(HorseJumpEvent event) {
		plugin.global_log("entity-horse-jump" + event.getEntity().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemDespawn(ItemDespawnEvent event) {
		plugin.global_log("entity-item-despawn" + event.getEntity().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPigZap(PigZapEvent event) {
		plugin.global_log("entity-pig-zap" + event.getEntity().getType().toString());
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		plugin.global_log("entity-player-deapth" + event.getEntity().getType().toString());
	}
}
