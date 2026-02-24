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

import de.markusbordihn.playercompanions.Constants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionAdvancements {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[Advancements]";

  private static final ResourceLocation ADV_ROOT =
    new ResourceLocation(Constants.MOD_ID, "taming/root");
  private static final ResourceLocation ADV_TRUST_25 =
    new ResourceLocation(Constants.MOD_ID, "taming/trust_25");
  private static final ResourceLocation ADV_TRUST_50 =
    new ResourceLocation(Constants.MOD_ID, "taming/trust_50");
  private static final ResourceLocation ADV_TRUST_75 =
    new ResourceLocation(Constants.MOD_ID, "taming/trust_75");
  private static final ResourceLocation ADV_TAMED =
    new ResourceLocation(Constants.MOD_ID, "taming/tamed");

  private CompanionAdvancements() {
  }

  public static void grantRoot(Player player) {
    grantAdvancement(player, ADV_ROOT);
  }

  public static void grantMilestone(Player player, int milestone) {
    ResourceLocation advancementId =
      switch (milestone) {
        case 25 -> ADV_TRUST_25;
        case 50 -> ADV_TRUST_50;
        case 75 -> ADV_TRUST_75;
        default -> null;
      };

    if (advancementId != null) {
      grantAdvancement(player, advancementId);
    }
  }

  public static void grantTamed(Player player) {
    grantAdvancement(player, ADV_TAMED);
  }

  private static void grantAdvancement(Player player, ResourceLocation advancementId) {
    if (!(player instanceof ServerPlayer serverPlayer)) {
      return;
    }

    MinecraftServer server = serverPlayer.getServer();
    if (server == null) {
      return;
    }

    Advancement advancement = server.getAdvancements().getAdvancement(advancementId);
    if (advancement == null) {
      log.debug("{} Advancement not found: {}", LOG_PREFIX, advancementId);
      return;
    }

    AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);
    if (progress.isDone()) {
      return; // Already granted
    }

    for (String criterion : progress.getRemainingCriteria()) {
      serverPlayer.getAdvancements().award(advancement, criterion);
    }
  }
}
