/**
 * Copyright 2022 Markus Bordihn
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

package de.markusbordihn.playercompanions.client.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

@EventBusSubscriber(value = Dist.CLIENT)
public class PlayerCompanionHud {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  private static boolean hudEnabled = COMMON.hudEnabled.get();
  private static boolean hudNameTagEnabled = COMMON.hudNameTagEnabled.get();
  private static boolean hudOwnerEnabled = COMMON.hudOwnerEnabled.get();
  private static boolean hudStatusEnabled = COMMON.hudStatusEnabled.get();
  private static int hudDisplayRadius = COMMON.hudDisplayRadius.get();

  private final EntityRenderDispatcher entityRenderDispatcher;
  private final Font fontRender;
  private final Minecraft minecraft;

  public PlayerCompanionHud(Minecraft minecraft) {
    this.minecraft = minecraft;
    this.fontRender = minecraft.font;
    this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
    if (Constants.NEAT_LOADED) {
      log.info("{} enable Neat support for HUD ...", Constants.LOG_ICON_NAME);
    }
  }

  @SubscribeEvent
  public static void handleWorldEventLoad(WorldEvent.Load event) {
    if (!event.getWorld().isClientSide()) {
      return;
    }
    hudEnabled = COMMON.hudEnabled.get();
    hudNameTagEnabled = COMMON.hudNameTagEnabled.get();
    hudOwnerEnabled = COMMON.hudOwnerEnabled.get();
    hudStatusEnabled = COMMON.hudStatusEnabled.get();
    hudDisplayRadius = COMMON.hudDisplayRadius.get();

    // Automatic disable hud, if specific options are disabled.
    if (hudEnabled && ((!hudNameTagEnabled && !hudOwnerEnabled && !hudStatusEnabled)
        || hudDisplayRadius == 0)) {
      hudEnabled = false;
    }

    if (!hudEnabled) {
      log.info("{} disable HUD!", Constants.LOG_ICON_NAME);
    } else {
      log.info("{} enable HUD inside a {} radius!", Constants.LOG_ICON_NAME, hudDisplayRadius);
      if (hudNameTagEnabled) {
        log.info("{} enable Name Tag hud.", Constants.LOG_ICON_NAME);
      }
      if (hudOwnerEnabled) {
        log.info("{} enable Owner hud.", Constants.LOG_ICON_NAME);
      }
      if (hudStatusEnabled) {
        log.info("{} enable Status hud.", Constants.LOG_ICON_NAME);
      }
    }
  }

  @SubscribeEvent()
  public void handleRenderLevelLastEvent(RenderLevelLastEvent event) {
    if (!hudEnabled || !Minecraft.renderNames()) {
      return;
    }

    Minecraft.getInstance().getProfiler().push("Player Companions: Hud");

    Camera camera = this.minecraft.gameRenderer.getMainCamera();
    PoseStack poseStack = event.getPoseStack();
    float partialTicks = event.getPartialTick();
    Entity cameraEntity = camera.getEntity() != null ? camera.getEntity() : this.minecraft.player;

    Vec3 cameraPos = camera.getPosition();
    final Frustum frustum = new Frustum(poseStack.last().pose(), event.getProjectionMatrix());
    frustum.prepare(cameraPos.x(), cameraPos.y(), cameraPos.z());

    ClientLevel client = minecraft.level;
    if (!minecraft.level.isClientSide) {
      return;
    }

    // Render hud for all relevant entities inside display radius.
    for (Entity entity : client.entitiesForRendering()) {
      if (entity instanceof PlayerCompanionEntity playerCompanionEntity
          && playerCompanionEntity != cameraEntity && playerCompanionEntity.isAlive()
          && playerCompanionEntity.shouldRender(cameraPos.x(), cameraPos.y(), cameraPos.z())
          && (playerCompanionEntity.noCulling
              || frustum.isVisible(playerCompanionEntity.getBoundingBox()))
          && this.entityRenderDispatcher.distanceToSqr(playerCompanionEntity) <= hudDisplayRadius
              * 4) {
        PlayerCompanionData playerCompanionData =
            PlayerCompanionsClientData.getCompanion(playerCompanionEntity);
        if (playerCompanionData != null) {
          render(playerCompanionEntity, playerCompanionData, poseStack, partialTicks);
        }
      }
    }
    Minecraft.getInstance().getProfiler().pop();
  }

  public void render(PlayerCompanionEntity entity, PlayerCompanionData data, PoseStack poseStack,
      float partialTicks) {
    double x = entity.xo + (entity.getX() - entity.xo) * partialTicks;
    double y = entity.yo + (entity.getY() - entity.yo) * partialTicks;
    double z = entity.zo + (entity.getZ() - entity.zo) * partialTicks;

    EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    Vec3 renderPos = renderDispatcher.camera.getPosition();

    MultiBufferSource.BufferSource buffer = this.minecraft.renderBuffers().bufferSource();
    final int light = LightTexture.pack(15, 15);
    float entityHeight = entity.getBbHeight() + 0.5F;

    poseStack.pushPose();
    poseStack.translate((float) (x - renderPos.x()), (float) (y - renderPos.y()),
        (float) (z - renderPos.z()));

    // Render name tag, mostly used for debugging
    if (hudNameTagEnabled) {
      renderNameTag(entity, poseStack, buffer, light, entityHeight);
    }

    // Render owner
    if (hudOwnerEnabled && entity.getOwner() != null) {
      renderOwner(entity, poseStack, buffer, light, entityHeight);
    }

    // Render status like sitting
    if (hudStatusEnabled && data != null) {
      renderStatus(entity, data, poseStack, buffer, light, entityHeight);
    }
    poseStack.popPose();
  }

  public void renderNameTag(PlayerCompanionEntity entity, PoseStack poseStack,
      MultiBufferSource buffer, int light, float f) {
    Component name = entity.getName();
    int y = 0;
    poseStack.pushPose();
    poseStack.translate(0.0D, f, 0.0D);
    poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
    poseStack.scale(-0.025F, -0.025F, 0.025F);
    Matrix4f matrix4f = poseStack.last().pose();
    float x = (-fontRender.width(name) / 2);
    fontRender.drawInBatch(name, x, y, ChatFormatting.RED.getColor(), false, matrix4f, buffer,
        false, 0, light);
    poseStack.popPose();
  }

  public void renderOwner(PlayerCompanionEntity entity, PoseStack poseStack,
      MultiBufferSource buffer, int light, float f) {
    float ownerScale = 0.5f;
    if (entity.getOwner() == null) {
      return;
    }
    Component text = new TextComponent("Owner: ").append(entity.getOwner().getName());
    poseStack.pushPose();
    poseStack.translate(0.0D, f, 0.0D);
    poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
    poseStack.scale(-0.025F * ownerScale, -0.025F * ownerScale, 0.025F * ownerScale);
    Matrix4f matrix4f = poseStack.last().pose();
    float y = Constants.NEAT_LOADED ? 5f : (fontRender.lineHeight + 1) / ownerScale;
    float x = (-fontRender.width(text) / 2);
    fontRender.drawInBatch(text, x, y, ChatFormatting.RED.getColor(), false, matrix4f, buffer,
        false, 0, light);
    poseStack.popPose();
  }

  public void renderStatus(PlayerCompanionEntity entity, PlayerCompanionData data,
      PoseStack poseStack, MultiBufferSource buffer, int light, float f) {
    Component text = new TextComponent("");
    if (data.isOrderedToSit()) {
      text = new TextComponent("(Sit)");
    } else if (!entity.hasOwner()) {
      text = new TextComponent("Untamed");
    } else {
      return;
    }
    poseStack.pushPose();
    poseStack.translate(0.0D, f, 0.0D);
    poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
    poseStack.scale(-0.025F, -0.025F, 0.025F);
    Matrix4f matrix4f = poseStack.last().pose();
    float y = Constants.NEAT_LOADED ? -8f : 0;
    float x = Constants.NEAT_LOADED ? -30f - fontRender.width(text)
        : -fontRender.width(entity.getName()) / 2.0F - fontRender.width(text) - 2;
    fontRender.drawInBatch(text, x, y, ChatFormatting.RED.getColor(), false, matrix4f, buffer,
        false, 0, light);
    poseStack.popPose();
  }

}
