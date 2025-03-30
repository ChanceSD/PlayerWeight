package me.chancesd.playerweight.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import me.chancesd.playerweight.PlayerWeight;
import me.chancesd.sdutils.scheduler.ScheduleUtils;

public class PlayerListener implements Listener {

	private final PlayerWeight plugin;

	public PlayerListener(final PlayerWeight plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		delay(p, 3);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof final Player player) {
			delay(player, 1);
		}
	}

	@EventHandler
	public void onPlayerDrop(final PlayerDropItemEvent event) {
		final Player p = event.getPlayer();
		plugin.wM.handler(p);
	}

	@EventHandler
	public void onPlayerPickup(final PlayerPickupItemEvent event) {
		final Player p = event.getPlayer();
		delay(p, 1);
	}

	public void delay(final Player p, final long ticks) {
		ScheduleUtils.runPlatformTaskLater(() -> plugin.wM.handler(p), p, ticks);
	}
}
