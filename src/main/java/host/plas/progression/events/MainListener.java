package host.plas.progression.events;

import host.plas.progression.data.PlayerData;
import host.plas.progression.data.PlayerManager;
import host.plas.progression.data.stats.StatType;
import host.plas.progression.data.stats.StatsInstance;
import io.papermc.paper.event.entity.FishHookStateChangeEvent;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class MainListener extends AbstractConglomerate {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerData data = PlayerManager.getOrCreatePlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        PlayerData data = PlayerManager.getOrCreatePlayer(player);
        data.saveAndUnload();
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        PlayerData data = PlayerManager.getOrCreatePlayer(player);
        StatsInstance instance = data.getStats();

        instance.increment(StatType.BROKEN_BLOCKS);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        PlayerData data = PlayerManager.getOrCreatePlayer(player);
        StatsInstance instance = data.getStats();

        instance.increment(StatType.PLACED_BLOCKS);
    }

    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (! (entity instanceof Player)) return; // Not a player kill.
        Player player = (Player) entity;

        Player killer = player.getKiller();
        if (killer == null) return; // No player killer.

        PlayerData data = PlayerManager.getOrCreatePlayer(killer);
        StatsInstance instance = data.getStats();

        instance.increment(StatType.KILLS);
    }

    @EventHandler
    public void onOtherKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) return; // Not a mob kill.
        if (! (entity instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) entity;

        Player killer = livingEntity.getKiller();
        if (killer == null) return; // No player killer.

        PlayerData data = PlayerManager.getOrCreatePlayer(killer);
        StatsInstance instance = data.getStats();

        instance.increment(StatType.MOB_KILLS);
    }

    @EventHandler
    public void onFishCatch(FishHookStateChangeEvent event) {
        FishHook.HookState state = event.getNewHookState();
        if (state != FishHook.HookState.HOOKED_ENTITY) return;
        FishHook hook = event.getEntity();

        ProjectileSource shooter = hook.getShooter();
        if (! (shooter instanceof Player)) return;
        Player player = (Player) shooter;

        Entity entity = hook.getHookedEntity();
        if (entity == null) return;

        if (! (entity instanceof Item) && ! (entity instanceof Fish)) return;

        PlayerData data = PlayerManager.getOrCreatePlayer(player);
        StatsInstance instance = data.getStats();
        instance.increment(StatType.FISH_CAUGHT);
    }

    @EventHandler
    public void onItemEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();

        PlayerData data = PlayerManager.getOrCreatePlayer(player);
        StatsInstance instance = data.getStats();

        instance.increment(StatType.ITEMS_ENCHANTED);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        PlayerData data = PlayerManager.getOrCreatePlayer(player);
        StatsInstance instance = data.getStats();

        instance.increment(StatType.CHAT_MESSAGES_SENT);
    }
}
