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

package de.markusbordihn.playercompanions.entity.damage;

import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerDataCapable;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

public class CompanionDamageHandler {

  private CompanionDamageHandler() {
  }

  public static boolean isInvulnerableTo(
    PlayerCompanion companion, DamageSource damageSource, boolean defaultInvulnerable) {

    if (damageSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      return false;
    }

    if (!TamingConfig.COMPANION_VULNERABLE) {
      return true;
    }

    Entity attacker = damageSource.getEntity();
    if (attacker == null) {
      return defaultInvulnerable;
    }

    if (attacker instanceof Player player) {
      return handlePlayerAttack(companion, player);
    }

    if (attacker instanceof LivingEntity livingAttacker) {
      return handleEntityAttack(companion, livingAttacker);
    }

    return defaultInvulnerable;
  }

  private static boolean handlePlayerAttack(PlayerCompanion companion, Player player) {
    UUID ownerUUID = getOwnerUUID(companion);
    if (ownerUUID == null) {
      return false;
    }

    if (ownerUUID.equals(player.getUUID())) {
      if (TamingConfig.OWNER_MUST_SNEAK_TO_HURT) {
        return !player.isShiftKeyDown();
      }
      return false;
    }

    return false;
  }

  private static boolean handleEntityAttack(PlayerCompanion companion, LivingEntity attacker) {
    UUID companionOwner = getOwnerUUID(companion);
    if (companionOwner == null) {
      return false;
    }

    if (attacker instanceof EasyNPC<?> attackerNPC) {
      OwnerDataCapable<?> attackerOwnerData = attackerNPC.getEasyNPCOwnerData();
      if (attackerOwnerData != null && companionOwner.equals(attackerOwnerData.getOwnerUUID())) {
        return true;
      }
    }

    if (attacker instanceof OwnableEntity ownable) {
      UUID attackerOwner = ownable.getOwnerUUID();
      if (companionOwner.equals(attackerOwner)) {
        return true;
      }
    }

    return false;
  }

  private static UUID getOwnerUUID(PlayerCompanion companion) {
    if (companion instanceof EasyNPC<?> easyNPC) {
      OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
      if (ownerData != null) {
        return ownerData.getOwnerUUID();
      }
    }
    return null;
  }
}
