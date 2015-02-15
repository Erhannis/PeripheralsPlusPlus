package com.austinv11.peripheralsplusplus.recipe;

import com.austinv11.peripheralsplusplus.api.satellites.upgrades.ISatelliteUpgrade;
import com.austinv11.peripheralsplusplus.init.ModItems;
import com.austinv11.peripheralsplusplus.items.SatelliteUpgradeBase;
import com.austinv11.peripheralsplusplus.reference.Reference;
import com.austinv11.peripheralsplusplus.utils.NBTHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UpgradeRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		int sats = 0;
		float weight = 0.0F;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) != null)
				if (inventory.getStackInSlot(i).isItemEqual(new ItemStack(ModItems.satellite)))
					sats++;
				else if (!(inventory.getStackInSlot(i).getItem() instanceof SatelliteUpgradeBase))
					return false;
				else
					weight += ((SatelliteUpgradeBase)inventory.getStackInSlot(i).getItem()).getUpgrade().getAddonWeight();
		}
		return (sats == 1 && weight <= 1.0F);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		List<ISatelliteUpgrade> upgrades = new ArrayList<ISatelliteUpgrade>();
		ItemStack result = new ItemStack(ModItems.satellite);
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i) == null)
				continue;
			if (inventory.getStackInSlot(i).isItemEqual(new ItemStack(ModItems.satellite)))
				result = inventory.getStackInSlot(i).copy();
			else if (!(inventory.getStackInSlot(i).getItem() instanceof SatelliteUpgradeBase))
				upgrades.add(((SatelliteUpgradeBase)inventory.getStackInSlot(i).getItem()).getUpgrade());
		}
		HashMap<ISatelliteUpgrade, Integer> map = new HashMap<ISatelliteUpgrade, Integer>();
		for (ISatelliteUpgrade s : upgrades) {
			if (map.containsKey(s))
				map.put(s, map.get(s)+1);
			else
				map.put(s, 1);
		}
		List<String> text = new ArrayList<String>();
		if (map.keySet().size() > 0 && !result.hasTagCompound())
			text.add(Reference.Colors.RESET+Reference.Colors.UNDERLINE+"Current Upgrades:");
		NBTTagList ids = new NBTTagList();
		for (ISatelliteUpgrade s : map.keySet()) {
			int lvl = map.get(s);
			if (NBTHelper.hasTag(result, StatCollector.translateToLocal(s.getUnlocalisedName())))
				lvl = lvl + NBTHelper.getInt(result, StatCollector.translateToLocal(s.getUnlocalisedName()));
			NBTHelper.setInteger(result, StatCollector.translateToLocal(s.getUnlocalisedName()), lvl);
			map.put(s, lvl);
			text.add(Reference.Colors.RESET+StatCollector.translateToLocal(s.getUnlocalisedName())+": "+lvl);
			ids.appendTag(new NBTTagString(String.valueOf(s.getUpgradeID())));
		}
		NBTHelper.addInfo(result, text);
		NBTHelper.setList(result, "upgradeIds", ids);
		return result;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ModItems.satellite);
	}
}
