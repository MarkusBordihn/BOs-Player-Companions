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
import de.markusbordihn.easynpc.entity.easynpc.data.AttributeDataCapable;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.CompanionRelationship;
import de.markusbordihn.playercompanions.entity.taming.TamingHintHandler;
import de.markusbordihn.playercompanions.entity.taming.TamingInteractionHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface PlayerCompanion {

  RandomSource getRandom();

  Enum<?>[] getSkinVariantTypes();

  Enum<?> getDefaultSkinVariantType();

  CompanionRelationship getRelationship();

  String getCompanionTypeName();

  TamingHintHandler getTamingHintHandler();

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

  default Enum<?> getRandomSkinVariantType() {
    Enum<?>[] variants = getSkinVariantTypes();
    if (variants.length > 1 && getRandom().nextInt(2) == 0) {
      return variants[getRandom().nextInt(variants.length)];
    }
    return getDefaultSkinVariantType();
  }

  @SuppressWarnings("unchecked")
  default void initCompanionAttributes() {
    if (this instanceof AttributeDataCapable<?> attributeCapable) {
      EntityAttributes entityAttributes = attributeCapable.getEntityAttributes();

      entityAttributes.setInteractionAttributes(
        new InteractionAttributes(
          TamingConfig.COMPANION_PUSHABLE,
          true,
          true,
          TamingConfig.COMPANION_PUSHABLE
        ));

      entityAttributes.setCombatAttributes(
        new CombatAttributes(
          true,
          true,
          !TamingConfig.COMPANION_VULNERABLE,
          0.0
        ));
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
  }

  default void loadCompanionData(CompoundTag tag) {
    CompanionRelationship relationship = getRelationship();
    if (relationship != null) {
      relationship.load(tag);
    }
  }

  default InteractionResult handleCompanionInteraction(Player player, InteractionHand hand) {
    return TamingInteractionHandler.handleTamingInteraction(this, player, hand);
  }

  default boolean handleDamage(DamageSource damageSource, boolean defaultResult) {
    return de.markusbordihn.playercompanions.entity.damage.CompanionDamageHandler
      .isInvulnerableTo(this, damageSource, defaultResult);
  }
}
