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

import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.companion.PigCompanion;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.entity.companion.RoosterCompanion;
import de.markusbordihn.playercompanions.entity.companion.SmallSlimeCompanion;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TamingEffects {

  private TamingEffects() {
  }

  public static void sendParticles(PlayerCompanion companion, ParticleOptions particle, int count) {
    Entity entity = companion.asEntity();
    if (!TamingConfig.SHOW_TRUST_PARTICLES) {
      return;
    }

    if (entity.level() instanceof ServerLevel serverLevel) {
      double x = entity.getX();
      double y = entity.getY() + entity.getBbHeight() / 2;
      double z = entity.getZ();

      serverLevel.sendParticles(
        particle,
        x,
        y,
        z,
        count,
        0.5,
        0.5,
        0.5,
        0.05
      );
    }
  }

  public static void sendParticleAt(
    PlayerCompanion companion, ParticleOptions particle, double x, double y, double z) {
    Entity entity = companion.asEntity();
    if (!TamingConfig.SHOW_TRUST_PARTICLES) {
      return;
    }

    if (entity.level() instanceof ServerLevel serverLevel) {
      serverLevel.sendParticles(particle, x, y, z, 1, 0, 0, 0, 0);
    }
  }

  public static void sendHeartCircle(PlayerCompanion companion, int heartCount) {
    Entity entity = companion.asEntity();
    if (!TamingConfig.SHOW_TRUST_PARTICLES) {
      return;
    }

    for (int i = 0; i < heartCount; i++) {
      double angle = (2 * Math.PI * i) / heartCount;
      double xOffset = Math.cos(angle) * 0.7;
      double zOffset = Math.sin(angle) * 0.7;
      sendParticleAt(
        companion,
        ParticleTypes.HEART,
        entity.getX() + xOffset,
        entity.getY() + 0.5,
        entity.getZ() + zOffset);
    }
  }

  public static void sendTamingExplosion(PlayerCompanion companion) {
    Entity entity = companion.asEntity();
    if (!TamingConfig.SHOW_TRUST_PARTICLES) {
      return;
    }

    if (entity.level() instanceof ServerLevel serverLevel) {
      Vec3 pos = entity.position().add(0, entity.getBbHeight() / 2, 0);

      serverLevel.sendParticles(
        ParticleTypes.HEART, pos.x, pos.y, pos.z, 50, 0.8, 0.8, 0.8, 0.1);
      serverLevel.sendParticles(
        ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y, pos.z, 30, 0.6, 0.6, 0.6, 0.05);
      serverLevel.sendParticles(
        ParticleTypes.TOTEM_OF_UNDYING, pos.x, pos.y, pos.z, 20, 0.5, 0.5, 0.5, 0.08);
      serverLevel.sendParticles(
        ParticleTypes.WITCH, pos.x, pos.y, pos.z, 10, 0.3, 0.3, 0.3, 0.02);
    }
  }

  public static void playFeedingSound(PlayerCompanion companion, int trustValue) {
    Entity entity = companion.asEntity();
    float pitch = 1.0f + (trustValue / 50f);
    entity.playSound(SoundEvents.PLAYER_BURP, 0.8f, pitch);

    if (companion instanceof PigCompanion) {
      entity.playSound(SoundEvents.PIG_AMBIENT, 0.5f, 1.2f);
    } else if (companion instanceof RoosterCompanion) {
      entity.playSound(SoundEvents.CHICKEN_AMBIENT, 0.5f, 1.1f);
    } else if (companion instanceof SmallSlimeCompanion) {
      entity.playSound(SoundEvents.SLIME_SQUISH_SMALL, 0.7f, 1.3f);
    }
  }

  public static void playSound(PlayerCompanion companion, SoundEvent sound, float volume,
    float pitch) {
    Entity entity = companion.asEntity();
    entity.level()
      .playSound(null, entity.blockPosition(), sound, SoundSource.NEUTRAL, volume, pitch);
  }

  public static void playTamingSuccessSounds(PlayerCompanion companion) {
    playSound(companion, SoundEvents.PLAYER_LEVELUP, 1.0f, 1.0f);
    playSound(companion, SoundEvents.VILLAGER_CELEBRATE, 1.0f, 1.0f);
  }

  public static void playNegativeSound(PlayerCompanion companion) {
    playSound(companion, SoundEvents.VILLAGER_NO, 0.6f, 1.0f);
  }

  public static void playMilestoneSound(PlayerCompanion companion) {
    playSound(companion, SoundEvents.NOTE_BLOCK_CHIME.value(), 0.8f, 1.5f);
  }
}
