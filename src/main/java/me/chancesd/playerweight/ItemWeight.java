package me.chancesd.playerweight;

import org.bukkit.inventory.ItemStack;

public class ItemWeight extends ItemStack {

	private double weight;

	public ItemWeight(final ItemStack i) {
		super(i);
		this.weight = getConfigWeight() * this.getAmount();
	}

	public ItemWeight() {
		this.weight = 0;
	}

	public static double getItemWeight(final ItemStack i) {
		if (i == null)
			return 0;
		else
			return new ItemWeight(i).getWeight();
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(final int weight) {
		this.weight = weight;
	}

	public String getMaterial() {
		final short durability = this.getDurability();
		if (durability > 0 && this.getType().getMaxDurability() <= 0)
			return this.getType().toString() + "," + durability;
		return this.getType().toString();
	}

	private double getConfigWeight() {
		return PlayerWeight.plugin.getConfig().getDouble(getMaterial());
	}
}
