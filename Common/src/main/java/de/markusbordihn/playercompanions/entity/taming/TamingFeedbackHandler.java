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
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.text.TamingMessages;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class TamingFeedbackHandler {

  private TamingFeedbackHandler() {
  }

  public static void handleSuccessFeedback(
    PlayerCompanion companion, Player player, int trustAdded, int currentTrust) {
    TamingEffects.playFeedingSound(companion, trustAdded);
    TamingEffects.sendParticles(companion, ParticleTypes.HEART, 3 + (trustAdded / 5));
    TamingMessages.sendTrustProgress(player, trustAdded, currentTrust, TamingConfig.TRUST_REQUIRED);

    CompanionAdvancements.grantRoot(player);

    int previousTrust = currentTrust - trustAdded;
    int milestone25 = (int) (TamingConfig.TRUST_REQUIRED * 0.25);
    int milestone50 = (int) (TamingConfig.TRUST_REQUIRED * 0.50);
    int milestone75 = (int) (TamingConfig.TRUST_REQUIRED * 0.75);
    checkMilestone(companion, player, previousTrust, currentTrust, milestone25, 25);
    checkMilestone(companion, player, previousTrust, currentTrust, milestone50, 50);
    checkMilestone(companion, player, previousTrust, currentTrust, milestone75, 75);
  }

  private static void checkMilestone(
    PlayerCompanion companion, Player player, int previousTrust, int currentTrust,
    int trustThreshold, int milestonePercent) {
    if (previousTrust < trustThreshold && currentTrust >= trustThreshold) {
      TamingEffects.sendParticles(companion, ParticleTypes.HAPPY_VILLAGER, 10);
      TamingEffects.playMilestoneSound(companion);
      CompanionAdvancements.grantMilestone(player, milestonePercent);
    }
  }

  public static void handleTamedFeedback(PlayerCompanion companion, Player player) {
    TamingEffects.sendTamingExplosion(companion);
    TamingEffects.playTamingSuccessSounds(companion);
    TamingMessages.sendTamedActionBar(player);
    TamingMessages.sendTamedMessage(player, companion.asEntity().getDisplayName());
  }

  public static void handleCooldownFeedback(
    PlayerCompanion companion, Player player, int remainingTicks) {
    TamingEffects.playNegativeSound(companion);
    TamingEffects.sendParticles(companion, ParticleTypes.SMOKE, 5);
    TamingMessages.sendCooldownMessage(player, (remainingTicks + 19) / 20);
  }

  public static void handleWrongPlayerFeedback(
    PlayerCompanion companion, Player player, UUID trustedPlayer) {
    TamingEffects.playNegativeSound(companion);
    TamingEffects.sendParticles(companion, ParticleTypes.ANGRY_VILLAGER, 3);

    String trustedName = "Unknown";
    if (player.level().getPlayerByUUID(trustedPlayer) instanceof ServerPlayer trustedServerPlayer) {
      trustedName = trustedServerPlayer.getDisplayName().getString();
    }
    TamingMessages.sendWrongPlayerMessage(player, trustedName);
  }

  public static void handleLimitReachedFeedback(PlayerCompanion companion, Player player) {
    TamingEffects.playSound(companion, net.minecraft.sounds.SoundEvents.VILLAGER_NO, 0.6f, 0.8f);
    TamingMessages.sendLimitReachedMessage(player, TamingConfig.COMPANION_LIMIT_PER_PLAYER);
  }

  public static void handleAlreadyTamedFeedback(PlayerCompanion companion, Player player) {
    TamingEffects.playNegativeSound(companion);
  }
}
