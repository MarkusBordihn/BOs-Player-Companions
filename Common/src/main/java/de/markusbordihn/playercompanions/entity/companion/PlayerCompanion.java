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

package de.markusbordihn.playercompanions.entity.companion;

import de.markusbordihn.easynpc.data.attribute.CombatAttributes;
import de.markusbordihn.easynpc.data.attribute.EntityAttributes;
import de.markusbordihn.easynpc.data.attribute.InteractionAttributes;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.AttributeDataCapable;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerDataCapable;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.CompanionBehaviorHandler;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.CompanionMenuHandler;
import de.markusbordihn.playercompanions.entity.CompanionRelationship;
import de.markusbordihn.playercompanions.entity.CompanionRole;
import de.markusbordihn.playercompanions.entity.damage.CompanionDeathHandler;
import de.markusbordihn.playercompanions.entity.taming.TamingHintHandler;
import de.markusbordihn.playercompanions.entity.taming.TamingInteractionHandler;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface PlayerCompanion {

  RandomSource getRandom();

  Enum<?>[] getSkinVariantTypes();

  Enum<?> getDefaultSkinVariantType();

  CompanionRelationship getRelationship();

  String getCompanionTypeName();

  TamingHintHandler getTamingHintHandler();

  CompanionRole getCompanionRole();

  CompanionCommand getCompanionCommand();

  void setCompanionCommand(CompanionCommand command);

  /**
   * Returns the sound played when the companion is fed during taming.
   */
  default SoundEvent getFeedingSound() {
    return SoundEvents.PLAYER_BURP;
  }

  default AggressionLevel getAggressionLevel() {
    return AggressionLevel.NEUTRAL;
  }

  default void setAggressionLevel(AggressionLevel level) {
  }

  /**
   * Returns whether the collector role is actively picking up items. Default: true.
   */
  default boolean isCollectorActive() {
    return true;
  }

  /**
   * Toggles the collector active state. No-op unless overridden.
   */
  default void setCollectorActive(boolean active) {
  }

  /**
   * Resolves the variant type from a string name, falling back to the default variant.
   */
  default Enum<?> getSkinVariantType(String name) {
    if (name == null || name.isEmpty()) {
      return getDefaultSkinVariantType();
    }
    for (Enum<?> variant : getSkinVariantTypes()) {
      if (variant.name().equals(name)) {
        return variant;
      }
    }
    return getDefaultSkinVariantType();
  }

  /**
   * Resolves the online owner ServerPlayer for this companion.
   */
  default Optional<ServerPlayer> getOnlineOwner() {
    if (level().isClientSide || !(this instanceof EasyNPC<?> easyNPC)) {
      return Optional.empty();
    }
    OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
    if (ownerData == null || ownerData.getOwnerUUID() == null || level().getServer() == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(
      level().getServer().getPlayerList().getPlayer(ownerData.getOwnerUUID()));
  }

  /**
   * Handles mobInteract delegation: taming for wild, command toggle / menu for tamed.
   */
  default InteractionResult handleMobInteract(Player player, InteractionHand hand) {
    InteractionResult tamingResult = handleCompanionInteraction(player, hand);
    if (tamingResult.consumesAction()) {
      return tamingResult;
    }
    return InteractionResult.PASS;
  }

  /**
   * Handles die delegation: records death and notifies owner.
   */
  default void handleCompanionDeath(DamageSource damageSource) {
    CompanionDeathHandler.handleDeath(this, damageSource);
  }

  /**
   * Handles isInvulnerableTo delegation.
   */
  default boolean handleCompanionDamage(DamageSource damageSource, boolean defaultResult) {
    return handleDamage(damageSource, defaultResult);
  }

  /**
   * Called on level-up. Sends a chat message to the owner and re-applies scaled attribute
   * modifiers.
   */
  default void notifyOwnerLevelUp(int newLevel) {
    getOnlineOwner().ifPresent(owner -> {
      owner.sendSystemMessage(
        Component.translatable(
            "playercompanions.level_up", asMob().getDisplayName(), newLevel)
          .withStyle(ChatFormatting.GOLD));
      applyLevelAttributes(newLevel);
    });
  }

  /**
   * Applies health (and attack for guards) attribute modifiers scaled to the given level. Uses
   * linear interpolation up to TamingConfig.MAX_LEVEL.
   */
  default void applyLevelAttributes(int level) {
    UUID healthModId = UUID.nameUUIDFromBytes("companion_level_health".getBytes());
    AttributeInstance healthAttr = asMob().getAttribute(Attributes.MAX_HEALTH);
    if (healthAttr != null) {
      healthAttr.removeModifier(healthModId);
      double healthBonus = level * TamingConfig.HEALTH_BOOST_PER_LEVEL;
      if (healthBonus > 0) {
        healthAttr.addPermanentModifier(new AttributeModifier(
          healthModId, "Companion Level Health",
          healthBonus, AttributeModifier.Operation.ADDITION));
      }
    }
    if (getCompanionRole() == CompanionRole.GUARD) {
      UUID attackModId = UUID.nameUUIDFromBytes("companion_level_attack".getBytes());
      AttributeInstance attackAttr = asMob().getAttribute(Attributes.ATTACK_DAMAGE);
      if (attackAttr != null) {
        attackAttr.removeModifier(attackModId);
        double attackBonus = level * TamingConfig.GUARD_ATTACK_BOOST_PER_LEVEL;
        if (attackBonus > 0) {
          attackAttr.addPermanentModifier(new AttributeModifier(
            attackModId, "Companion Level Attack",
            attackBonus, AttributeModifier.Operation.ADDITION));
        }
      }
    }
  }

  default Entity asEntity() {
    return (Entity) this;
  }

  default Mob asMob() {
    return (Mob) this;
  }

  default EntityType<?> getType() {
    return asEntity().getType();
  }

  default Level level() {
    return asEntity().level();
  }

  default boolean isOwned() {
    if (this instanceof EasyNPC<?> easyNPC) {
      OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
      return ownerData != null && ownerData.getOwnerUUID() != null;
    }
    return false;
  }

  default boolean isOwner(Player player) {
    if (this instanceof EasyNPC<?> easyNPC) {
      OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
      return ownerData != null && player.getUUID().equals(ownerData.getOwnerUUID());
    }
    return false;
  }

  default Enum<?> getRandomSkinVariantType() {
    Enum<?>[] variants = getSkinVariantTypes();
    if (variants.length > 1 && getRandom().nextInt(2) == 0) {
      return variants[getRandom().nextInt(variants.length)];
    }
    return getDefaultSkinVariantType();
  }

  default void initCompanionAttributes() {
    if (this instanceof AttributeDataCapable<?> attributeCapable) {
      EntityAttributes entityAttributes = attributeCapable.getEntityAttributes();
      entityAttributes.setInteractionAttributes(
        new InteractionAttributes(
          TamingConfig.COMPANION_PUSHABLE, true, true, TamingConfig.COMPANION_PUSHABLE));
      entityAttributes.setCombatAttributes(
        new CombatAttributes(true, true, !TamingConfig.COMPANION_VULNERABLE, 0.0));
    }
    if (TamingConfig.COMPANION_VULNERABLE) {
      asMob().setInvulnerable(false);
    }
  }

  default void tickCompanion() {
    if (!level().isClientSide) {
      CompanionRelationship relationship = getRelationship();
      if (relationship != null) {
        relationship.tick(level().getGameTime());
      }
      TamingHintHandler hintHandler = getTamingHintHandler();
      if (hintHandler != null) {
        hintHandler.tick(this);
      }
    }
  }

  default void saveCompanionData(CompoundTag tag) {
    CompanionRelationship relationship = getRelationship();
    if (relationship != null) {
      relationship.save(tag);
    }
    tag.putString("CompanionCommand", getCompanionCommand().name());
  }

  default void loadCompanionData(CompoundTag tag) {
    CompanionRelationship relationship = getRelationship();
    if (relationship != null) {
      relationship.load(tag);
    }
    if (tag.contains("CompanionCommand")) {
      try {
        setCompanionCommand(CompanionCommand.valueOf(tag.getString("CompanionCommand")));
      } catch (IllegalArgumentException ignored) {
        setCompanionCommand(CompanionCommand.FOLLOW);
      }
    }
  }

  default InteractionResult handleCompanionInteraction(Player player, InteractionHand hand) {
    if (isOwned()) {
      if (!isOwner(player)) {
        return InteractionResult.PASS;
      }
      if (!level().isClientSide) {
        if (player.isShiftKeyDown()) {
          // Sneak+rightclick: open role-specific menu
          CompanionMenuHandler.openMenu(this, (ServerPlayer) player);
        } else if (player.getItemInHand(hand).isEmpty()) {
          // Normal rightclick: toggle FOLLOW/SIT
          CompanionCommand next =
            getCompanionCommand() == CompanionCommand.SIT
              ? CompanionCommand.FOLLOW
              : CompanionCommand.SIT;
          CompanionBehaviorHandler.applyCommand(this, next);
          level().broadcastEntityEvent(asEntity(),
            next == CompanionCommand.SIT ? (byte) 4 : (byte) 6);
        }
      }
      return InteractionResult.sidedSuccess(level().isClientSide);
    }
    return TamingInteractionHandler.handleTamingInteraction(this, player, hand);
  }

  default boolean handleDamage(DamageSource damageSource, boolean defaultResult) {
    return de.markusbordihn.playercompanions.entity.damage.CompanionDamageHandler
      .isInvulnerableTo(this, damageSource, defaultResult);
  }
}
