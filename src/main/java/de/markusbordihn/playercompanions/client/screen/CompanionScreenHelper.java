/*
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

package de.markusbordihn.playercompanions.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;

public class CompanionScreenHelper {

  public static void renderEntity(
      int x, int y, float yRot, float xRot, PlayerCompanionEntity playerCompanionEntity) {
    // Prepare Renderer
    Minecraft minecraft = Minecraft.getInstance();
    float f = (float) Math.atan(yRot / 40.0F);
    float f1 = (float) Math.atan(xRot / 40.0F);
    int scale = playerCompanionEntity.getEntityGuiScaling();
    PoseStack poseStack = RenderSystem.getModelViewStack();
    poseStack.pushPose();
    poseStack.translate(x, y, 1050.0D);
    poseStack.scale(1.0F, 1.0F, -1.0F);
    RenderSystem.applyModelViewMatrix();
    PoseStack poseStack1 = new PoseStack();
    poseStack1.translate(0.0D, 0.0D, 1000.0D);
    poseStack1.scale(scale, scale, scale);
    Quaternionf quaternionf = (new Quaternionf()).rotateZ((float) Math.PI);
    Quaternionf quaternionf1 = (new Quaternionf()).rotateX(f1 * 20.0F * ((float) Math.PI / 180F));
    quaternionf.mul(quaternionf1);
    poseStack1.mulPose(quaternionf);

    // Backup entity information
    Component entityCustomName = playerCompanionEntity.getCustomName();
    boolean entityShouldShowName = playerCompanionEntity.shouldShowName();
    float entityYBodyRot = playerCompanionEntity.yBodyRot;
    float entityYRot = playerCompanionEntity.getYRot();
    float entityXRot = playerCompanionEntity.getXRot();
    float entityYHeadRotO = playerCompanionEntity.yHeadRotO;
    float entityYHeadRot = playerCompanionEntity.yHeadRot;

    // Adjust entity information for rendering
    playerCompanionEntity.yBodyRot = 180.0F + f * 20.0F;
    playerCompanionEntity.setYRot(180.0F + f * 40.0F);
    playerCompanionEntity.setXRot(-f1 * 20.0F);
    playerCompanionEntity.yHeadRot = playerCompanionEntity.getYRot();
    Component customName = playerCompanionEntity.getCustomName();

    // Hide gui elements or remove custom name
    boolean minecraftHideGui = false;
    if (minecraft != null) {
      minecraftHideGui = minecraft.options.hideGui;
      minecraft.options.hideGui = true;
    } else {
      playerCompanionEntity.setCustomName(null);
      playerCompanionEntity.setCustomNameVisible(false);
    }

    // Render Entity
    Lighting.setupForEntityInInventory();
    EntityRenderDispatcher entityRenderDispatcher =
        Minecraft.getInstance().getEntityRenderDispatcher();
    quaternionf1.conjugate();
    entityRenderDispatcher.overrideCameraOrientation(quaternionf1);
    entityRenderDispatcher.setRenderShadow(false);
    MultiBufferSource.BufferSource multiBuffer =
        Minecraft.getInstance().renderBuffers().bufferSource();
    entityRenderDispatcher.render(
        playerCompanionEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack1, multiBuffer, 15728880);
    multiBuffer.endBatch();
    entityRenderDispatcher.setRenderShadow(true);

    // Restore entity information
    playerCompanionEntity.yBodyRot = entityYBodyRot;
    playerCompanionEntity.setYRot(entityYRot);
    playerCompanionEntity.setXRot(entityXRot);
    playerCompanionEntity.yHeadRot = entityYHeadRot;
    playerCompanionEntity.yHeadRotO = entityYHeadRotO;
    playerCompanionEntity.setCustomName(customName);

    // Restore gui elements or custom name
    if (minecraft != null) {
      minecraft.options.hideGui = minecraftHideGui;
    } else {
      playerCompanionEntity.setCustomName(entityCustomName);
      playerCompanionEntity.setCustomNameVisible(entityShouldShowName);
    }

    poseStack.popPose();
    RenderSystem.applyModelViewMatrix();
    Lighting.setupFor3DItems();
  }
}
