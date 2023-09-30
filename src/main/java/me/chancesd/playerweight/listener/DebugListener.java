package me.chancesd.playerweight.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.chancesd.playerweight.PlayerWeight;

public class DebugListener implements Listener {

	private final PlayerWeight plugin;

	public DebugListener(final PlayerWeight plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		final Player p = event.getPlayer();
		if (plugin.debug) {
			p.sendMessage("Speed: " + p.getWalkSpeed());
			p.sendMessage("Percentage: " + plugin.wM.calculateWeightPercentage(plugin.wM.getWeight(p), p)+ "%");
		}
	}
}
