/*
 * Copyright 2026 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.playercompanions.entity.taming;

import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerDataCapable;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TamingHintHandler {

  private static final int FOOD_CHECK_INTERVAL = 20;
  private static final int CURIOSITY_MIN_INTERVAL = 100;
  private static final int CURIOSITY_MAX_INTERVAL = 200;
  private static final double HINT_RANGE = 6.0;
  private static final int NAMETAG_UPDATE_INTERVAL = 40;
  private static final int FOOD_HINT_DISPLAY_TICKS = 60;

  private int foodCheckTimer = 0;
  private int curiosityTimer = 0;
  private int nextCuriosityInterval;
  private int nametagUpdateTimer = 0;
  private int foodHintTimer = 0;
  private boolean showingFoodHint = false;
  private Component originalName = null;

  public TamingHintHandler() {
    this.nextCuriosityInterval =
      CURIOSITY_MIN_INTERVAL
        + (int) (Math.random() * (CURIOSITY_MAX_INTERVAL - CURIOSITY_MIN_INTERVAL));
  }

  public void tick(PlayerCompanion companion) {
    if (!TamingConfig.SHOW_TAMING_HINTS) {
      return;
    }

    if (isOwned(companion)) {
      clearFoodHint(companion);
      return;
    }

    Mob mob = companion.asMob();
    Entity entity = companion.asEntity();
    if (!(entity.level() instanceof ServerLevel serverLevel)) {
      return;
    }

    // Food-aware particle check
    foodCheckTimer++;
    if (foodCheckTimer >= FOOD_CHECK_INTERVAL) {
      foodCheckTimer = 0;
      tickFoodAwareness(companion, mob, serverLevel);
    }

    // Ambient curiosity
    curiosityTimer++;
    if (curiosityTimer >= nextCuriosityInterval) {
      curiosityTimer = 0;
      nextCuriosityInterval =
        CURIOSITY_MIN_INTERVAL
          + mob.getRandom().nextInt(CURIOSITY_MAX_INTERVAL - CURIOSITY_MIN_INTERVAL);
      tickCuriosity(companion, mob, serverLevel);
    }

    // Food hint nametag timer
    if (showingFoodHint) {
      foodHintTimer++;
      if (foodHintTimer >= FOOD_HINT_DISPLAY_TICKS) {
        clearFoodHint(companion);
      }
    }

    // Nametag update
    nametagUpdateTimer++;
    if (nametagUpdateTimer >= NAMETAG_UPDATE_INTERVAL) {
      nametagUpdateTimer = 0;
      tickNametag(companion, mob, serverLevel);
    }
  }

  private void tickFoodAwareness(
    PlayerCompanion companion, Mob mob, ServerLevel serverLevel) {
    Vec3 pos = mob.position();
    AABB searchBox = new AABB(pos, pos).inflate(HINT_RANGE);

    List<Player> nearbyPlayers =
      serverLevel.getEntitiesOfClass(Player.class, searchBox, p -> !p.isSpectator());

    for (Player player : nearbyPlayers) {
      ItemStack mainHand = player.getMainHandItem();
      ItemStack offHand = player.getOffhandItem();

      Item foodItem = null;
      if (CompanionFoodRegistry.isValidFood(companion.getType(), mainHand.getItem())) {
        foodItem = mainHand.getItem();
      } else if (CompanionFoodRegistry.isValidFood(companion.getType(), offHand.getItem())) {
        foodItem = offHand.getItem();
      }

      if (foodItem != null) {
        mob.getLookControl().setLookAt(player, 30.0F, 30.0F);
        serverLevel.sendParticles(
          ParticleTypes.HAPPY_VILLAGER,
          mob.getX(),
          mob.getY() + mob.getBbHeight(),
          mob.getZ(),
          3,
          0.3, 0.2, 0.3,
          0.02
        );

        // Small note particle above head (musical interest)
        serverLevel.sendParticles(
          ParticleTypes.NOTE,
          mob.getX(),
          mob.getY() + mob.getBbHeight() + 0.3,
          mob.getZ(),
          1, 0, 0, 0, 0);

        // Quiet ambient sound
        mob.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.15f, 1.8f);

        // Update nametag to reveal food hint
        setFoodHint(companion, foodItem);
        return;
      }
    }
  }

  private void tickCuriosity(
    PlayerCompanion companion, Mob mob, ServerLevel serverLevel) {
    Vec3 pos = mob.position();
    AABB searchBox = new AABB(pos, pos).inflate(8.0);

    List<Player> nearbyPlayers =
      serverLevel.getEntitiesOfClass(Player.class, searchBox, p -> !p.isSpectator());

    if (!nearbyPlayers.isEmpty()) {
      Player target = nearbyPlayers.get(mob.getRandom().nextInt(nearbyPlayers.size()));
      mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

      // "?" particle effect (witch sparkle above head)
      serverLevel.sendParticles(
        ParticleTypes.WITCH,
        mob.getX(),
        mob.getY() + mob.getBbHeight() + 0.4,
        mob.getZ(),
        2, 0.1, 0.1, 0.1, 0.01);

      // Occasional small note particle (10% chance)
      if (mob.getRandom().nextInt(10) == 0) {
        serverLevel.sendParticles(
          ParticleTypes.NOTE,
          mob.getX(),
          mob.getY() + mob.getBbHeight() + 0.5,
          mob.getZ(),
          1, 0, 0, 0, 0);
      }
    }
  }

  private void tickNametag(
    PlayerCompanion companion, Mob mob, ServerLevel serverLevel) {
    if (showingFoodHint) {
      return;
    }

    // Set "Wild [Name]" as default for untamed companions
    if (mob.getCustomName() == null || !showingFoodHint) {
      ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
      String wildNameKey = Constants.MOD_ID + ".wild." + entityTypeId.getPath();
      Component wildName =
        Component.translatable(wildNameKey)
          .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
      mob.setCustomName(wildName);
      mob.setCustomNameVisible(true);
    }
  }

  private void setFoodHint(PlayerCompanion companion, Item foodItem) {
    Mob mob = companion.asMob();
    if (!showingFoodHint) {
      originalName = mob.getCustomName();
    }

    showingFoodHint = true;
    foodHintTimer = 0;

    Component foodName = foodItem.getDefaultInstance().getHoverName();
    Component hintName =
      Component.translatable("player_companions.hint.likes_food", foodName)
        .withStyle(ChatFormatting.GREEN);
    mob.setCustomName(hintName);
    mob.setCustomNameVisible(true);
  }

  private void clearFoodHint(PlayerCompanion companion) {
    if (showingFoodHint) {
      showingFoodHint = false;
      foodHintTimer = 0;

      Mob mob = companion.asMob();
      if (isOwned(companion)) {
        mob.setCustomName(null);
        mob.setCustomNameVisible(false);
      } else if (originalName != null) {
        mob.setCustomName(originalName);
      }
      originalName = null;
    }
  }

  private boolean isOwned(PlayerCompanion companion) {
    if (companion instanceof EasyNPC<?> easyNPC) {
      OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
      return ownerData != null && ownerData.getOwnerUUID() != null;
    }
    return false;
  }

  public void onTamed(PlayerCompanion companion) {
    clearFoodHint(companion);
    Mob mob = companion.asMob();
    mob.setCustomName(null);
    mob.setCustomNameVisible(false);
  }
}
