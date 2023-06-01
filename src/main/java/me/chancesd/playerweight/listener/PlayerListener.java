package me.chancesd.playerweight.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import me.chancesd.playerweight.PlayerWeight;

public class PlayerListener implements Listener {

	private PlayerWeight plugin;

	public PlayerListener(PlayerWeight plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		delay(p, 3);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			delay(p, 1);
		}
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		plugin.wM.handler(p);
	}

	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		delay(p, 1);
	}
	
	@EventHandler
	public void onPlayerExpGain(PlayerExpChangeEvent event) {
		if(!plugin.getConfig().getBoolean("Enable XP Bar", true))
			return;
		event.setAmount(0);
	}

	public void delay(final Player p, long ticks) {
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				plugin.wM.handler(p);
			}
		}, ticks);
	}
}
