package me.chancesd.playerweight;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.chancesd.playerweight.Updater.UpdateResult;
import me.chancesd.playerweight.listener.DebugListener;
import me.chancesd.playerweight.listener.PlayerListener;

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
				public void run() {
					updater();
				}
			}.runTaskAsynchronously(this);

//		try {
//			MetricsLite metrics = new MetricsLite(this);
//			metrics.start();
//		} catch (IOException e) {
//		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (command.getLabel().equals("pw") && sender instanceof Player) {
			final Player p = (Player) sender;
			if (args.length == 0) {
				final double weight = wM.getWeight(p);
				final DecimalFormatSymbols symbol = new DecimalFormatSymbols();
				symbol.setDecimalSeparator('.');
				final String message = translateColor(getConfig().getString("WeightCommand")).replace("<weight>", new DecimalFormat("#.##", symbol).format(weight))
						.replace("<maxweight>", String.valueOf(wM.getMaxW()))
						.replace("<weightpercent>", String.valueOf((int) (wM.calculateWeightPercentage(weight, p) * 100)));
				p.sendMessage(message);
				return true;
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("debug") && p.hasPermission("playerweight.debug")) {
					if (getConfig().getBoolean("Debug")) {
						if (!debug) {
							debug = true;
						} else if (debug) {
							debug = false;
						}
						return true;
					} else if (!getConfig().getBoolean("Debug")) {
						p.sendMessage("§4Debug needs to be enabled in config!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("reload") && p.hasPermission("playerweight.reload")) {
					getServer().getPluginManager().disablePlugin(this);
					getServer().getPluginManager().enablePlugin(this);
					for (final Player player : getServer().getOnlinePlayers()) {
						wM.handler(player);
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
		final Updater updater = new Updater(this, 69092, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
		if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
			getLogger().info("Update Available: " + updater.getLatestName());
			if (getConfig().getBoolean("Update Check.Auto Update")) {
				new Updater(this, 69092, this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
				return;
			}
			getLogger().info("Link: http://dev.bukkit.org/bukkit-plugins/playerweight/");
		} else
			getLogger().info("No update found");
	}
}