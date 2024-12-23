package me.chancesd.playerweight;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class WeightManager {

	private final PlayerWeight plugin;
	private int maxWeight;
	private double lessThan;
	private double between1;
	private double between1_1;
	private double between2;
	private double between2_1;
	private double biggerThan;
	private double speedPercent;
	private double speedPercent1;
	private double speedPercent2;
	private double speedPercent3;
	private final HashMap<String, Integer> previousWeight = new HashMap<>();

	public WeightManager(final PlayerWeight plugin) {
		this.plugin = plugin;
		loadConfigVariables();
	}

	public double getWeight(final Player p) {
		double weight = 0;
		for (final ItemStack i : p.getInventory().getContents()) {
			weight += ItemWeight.getItemWeight(i);
		}
		for (final ItemStack i : p.getInventory().getArmorContents()) {
			weight += ItemWeight.getItemWeight(i);
		}
		return weight;
	}

	public void handler(final Player p) {
		if (p.hasPermission("playerweight.bypass"))
			return;
		int previousSector = 0;
		final int presentSector = getSector(calculateWeightPercentage(getWeight(p), p));
		if (!plugin.getConfig().getBoolean("Disable Messages", false)) {
			if (previousWeight.containsKey(p.getName()))
				previousSector = previousWeight.put(p.getName(), presentSector);
			else
				previousWeight.put(p.getName(), presentSector);

			if (presentSector > previousSector || previousSector > 1 && presentSector == 1) {
				p.sendMessage(announce(presentSector));
			}
		}
		calculateSpeed(presentSector, p);
	}

	private String announce(final int sector) {
		switch (sector) {
		case 1:
			return plugin.translateColor(plugin.getConfig().getString("Less And Equal To.Message"));
		case 2:
			return plugin.translateColor(plugin.getConfig().getString("Between.Message"));
		case 3:
			return plugin.translateColor(plugin.getConfig().getString("Between1.Message"));
		case 4:
			return plugin.translateColor(plugin.getConfig().getString("Bigger Than.Message"));
		}
		return null;
	}

	public Float calculateWeightPercentage(final double weight, final Player p) {
		final float weightPercent = (float) weight / getMaxW(p);
		if (plugin.getConfig().getBoolean("Enable XP Bar", true)) {
			setExp(weightPercent, p);
		}
		return weightPercent;
	}

	public void setExp(final Float weightPercent, final Player p) {
		if (weightPercent > 1)
			p.sendExperienceChange(1);
		else
			p.sendExperienceChange(Math.max(0, weightPercent));
	}

	public void calculateSpeed(final int sector, final Player p) {
		switch (sector) {
		case 1:
			p.setWalkSpeed(speed(speedPercent));
			break;
		case 2:
			p.setWalkSpeed(speed(speedPercent1));
			break;
		case 3:
			p.setWalkSpeed(speed(speedPercent2));
			break;
		case 4:
			p.setWalkSpeed(speed(speedPercent3));
			break;
		}
	}

	private int getSector(final float weightPercent) {
		if (weightPercent <= lessThan)
			return 1;
		if (weightPercent >= between1 && weightPercent <= between1_1)
			return 2;
		if (weightPercent >= between2 && weightPercent <= between2_1)
			return 3;
		if (weightPercent > biggerThan)
			return 4;
		else
			return 0;
	}

	public int getMaxW(final Player player) {
		return getAmount(player, this.maxWeight);
	}

	public int getAmount(final Player player, final int defaultValue) {
		final String permissionPrefix = "playerweight.max.";

		for (final PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
			final String permission = attachmentInfo.getPermission();
			if (permission.startsWith(permissionPrefix)) {
				return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
			}
		}

		return defaultValue;
	}

	public float speed(final double percent) {
		return (float) (0.2 * percent);
	}

	public void loadConfigVariables() {
		maxWeight = plugin.getConfig().getInt("Max Weight");
		lessThan = plugin.getConfig().getDouble("Less And Equal To.Percentage") / 100;
		speedPercent = plugin.getConfig().getDouble("Less And Equal To.SpeedPercent") / 100;
		splitBetween(plugin.getConfig().getString("Between.Percentage"), 1);
		speedPercent1 = plugin.getConfig().getDouble("Between.SpeedPercent") / 100;
		splitBetween(plugin.getConfig().getString("Between1.Percentage"), 2);
		speedPercent2 = plugin.getConfig().getDouble("Between1.SpeedPercent") / 100;
		biggerThan = plugin.getConfig().getDouble("Bigger Than.Percentage") / 100;
		speedPercent3 = plugin.getConfig().getDouble("Bigger Than.SpeedPercent") / 100;
	}

	public void splitBetween(final String a, final int p) {
		final String[] between = a.split(",");
		if (p == 1) {
			between1 = Double.parseDouble(between[0]) / 100;
			between1_1 = Double.parseDouble(between[1]) / 100;
		}
		if (p == 2) {
			between2 = Double.parseDouble(between[0]) / 100;
			between2_1 = Double.parseDouble(between[1]) / 100;
		}
	}
}
