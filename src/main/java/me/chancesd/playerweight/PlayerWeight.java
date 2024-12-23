package me.chancesd.playerweight;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.chancesd.playerweight.listener.DebugListener;
import me.chancesd.playerweight.listener.PlayerListener;
import me.chancesd.sdutils.metrics.Metrics;
import me.chancesd.sdutils.updater.BukkitUpdater;
import me.chancesd.sdutils.updater.Updater;
import me.chancesd.sdutils.updater.Updater.UpdateResult;
import me.chancesd.sdutils.updater.Updater.UpdateType;

public class PlayerWeight extends JavaPlugin {

	public boolean debug;
	public WeightManager wM;
	public static PlayerWeight plugin;

	@Override
	public void onEnable() {
		PlayerWeight.plugin = this;
		saveDefaultConfig();
		if (getConfig().getInt("Config Version", 0) < 5) {
			getConfig().options().copyDefaults(true);
			getConfig().set("Config Version", 5);
			saveConfig();
		}
		this.reloadConfig();
		if (getConfig().getBoolean("Debug"))
			new DebugListener(this);
		new PlayerListener(this);
		this.wM = new WeightManager(this);
		if (getConfig().getBoolean("Update Check.Enabled"))
			new BukkitRunnable() {
				@Override
				public void run() {
					updater();
				}
			}.runTaskAsynchronously(this);

		new Metrics(this, 19930, !getConfig().getBoolean("Update Check.Enabled"));
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (command.getLabel().equals("pw") && sender instanceof final Player player) {
			if (args.length == 0) {
				final double weight = wM.getWeight(player);
				final DecimalFormatSymbols symbol = new DecimalFormatSymbols();
				symbol.setDecimalSeparator('.');
				final String message = translateColor(getConfig().getString("WeightCommand")).replace("<weight>", new DecimalFormat("#.##", symbol).format(weight))
						.replace("<maxweight>", String.valueOf(wM.getMaxW(player)))
						.replace("<weightpercent>", String.valueOf((int) (wM.calculateWeightPercentage(weight, player) * 100)));
				player.sendMessage(message);
				return true;
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("debug") && player.hasPermission("playerweight.debug")) {
					if (getConfig().getBoolean("Debug")) {
						if (!debug) {
							debug = true;
						} else if (debug) {
							debug = false;
						}
						return true;
					} else if (!getConfig().getBoolean("Debug")) {
						player.sendMessage("§4Debug needs to be enabled in config!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("reload") && player.hasPermission("playerweight.reload")) {
					getServer().getPluginManager().disablePlugin(this);
					getServer().getPluginManager().enablePlugin(this);
					for (final Player p : getServer().getOnlinePlayers()) {
						wM.handler(p);
					}
					sender.sendMessage("§6[§7PlayerWeight§6] §fPlayerWeight Reloaded!");
					return true;
				}
			}
		}
		sender.sendMessage("§4You don't have Permission!");
		return false;
	}

	public String translateColor(final String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public void updater() {
		getLogger().info("Checking for updates...");
		final Updater updater = new BukkitUpdater(this, 69092, UpdateType.VERSION_CHECK);
		if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			getLogger().info("Update Available: " + updater.getLatestName());
			if (getConfig().getBoolean("Update Check.Auto Update") && updater.downloadFile()) {
				getLogger().info("Update downloaded to your update folder, it will be applied automatically on the next server restart");
				return;
			}
			getLogger().info("Link: " + updater.getUpdateLink());
		} else
			getLogger().info("No update found");
	}
}