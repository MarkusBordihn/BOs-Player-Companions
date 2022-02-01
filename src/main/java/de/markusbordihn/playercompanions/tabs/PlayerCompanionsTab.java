package de.markusbordihn.playercompanions.tabs;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.playercompanions.item.ModItems;

public class PlayerCompanionsTab {

  protected PlayerCompanionsTab() {}

  public static final CreativeModeTab TAB_SPAWN_EGGS = new CreativeModeTab("companion_spawn_eggs") {
    public ItemStack makeIcon() {
      return new ItemStack(ModItems.SMALL_GHAST_SPAWN_EGG.get());
    }
  };

}
