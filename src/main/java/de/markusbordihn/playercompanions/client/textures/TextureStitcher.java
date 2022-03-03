package de.markusbordihn.playercompanions.client.textures;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class TextureStitcher {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected TextureStitcher() {}

  @SubscribeEvent
  public static void handleTextureStitchEvent(TextureStitchEvent.Pre event) {
    ResourceLocation stitching = event.getAtlas().location();
    if (!stitching.equals(InventoryMenu.BLOCK_ATLAS)) {
      return;
    }
    log.info("{} Texture to stitcher {}", Constants.LOG_REGISTER_PREFIX, InventoryMenu.BLOCK_ATLAS);

    // Adding empty armor slots icons.
    event.addSprite(new ResourceLocation(Constants.MOD_ID, "item/empty_armor_slot_boots"));
    event.addSprite(new ResourceLocation(Constants.MOD_ID, "item/empty_armor_slot_chestplate"));
    event.addSprite(new ResourceLocation(Constants.MOD_ID, "item/empty_armor_slot_helmet"));
    event.addSprite(new ResourceLocation(Constants.MOD_ID, "item/empty_armor_slot_leggings"));
    event.addSprite(new ResourceLocation(Constants.MOD_ID, "item/empty_armor_slot_shield"));
    event.addSprite(new ResourceLocation(Constants.MOD_ID, "item/empty_armor_slot_weapon"));
  }
}
