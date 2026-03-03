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

package de.markusbordihn.playercompanions.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import org.joml.Quaternionf;

public class CompanionScreenHelper {

  private CompanionScreenHelper() {
  }

  public static void renderEntity(int x, int y, int scale, float yRot, float xRot, Mob entity) {
    float f = (float) Math.atan(yRot / 40.0F);
    float f1 = (float) Math.atan(xRot / 40.0F);

    PoseStack modelViewStack = RenderSystem.getModelViewStack();
    modelViewStack.pushPose();
    modelViewStack.translate(x, y, 1050.0);
    modelViewStack.scale(1.0F, 1.0F, -1.0F);
    RenderSystem.applyModelViewMatrix();

    PoseStack poseStack = new PoseStack();
    poseStack.translate(0.0, 0.0, 1000.0);
    poseStack.scale(scale, scale, scale);

    Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.PI);
    Quaternionf quaternion1 = new Quaternionf().rotateX(f1 * 20.0F * ((float) Math.PI / 180F));
    quaternion.mul(quaternion1);
    poseStack.mulPose(quaternion);

    Component customName = entity.getCustomName();
    boolean showName = entity.shouldShowName();
    float yBodyRot = entity.yBodyRot;
    float yRot0 = entity.getYRot();
    float xRot0 = entity.getXRot();
    float yHeadRotO = entity.yHeadRotO;
    float yHeadRot = entity.yHeadRot;

    entity.yBodyRot = 180.0F + f * 20.0F;
    entity.setYRot(180.0F + f * 40.0F);
    entity.setXRot(-f1 * 20.0F);
    entity.yHeadRot = entity.getYRot();
    entity.yHeadRotO = entity.getYRot();

    Minecraft mc = Minecraft.getInstance();
    boolean wasHideGui = mc.options.hideGui;
    mc.options.hideGui = true;

    Lighting.setupForEntityInInventory();
    EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
    quaternion1.conjugate();
    dispatcher.overrideCameraOrientation(quaternion1);
    dispatcher.setRenderShadow(false);

    MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
    dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, poseStack, buffers, 15728880);
    buffers.endBatch();
    dispatcher.setRenderShadow(true);

    entity.yBodyRot = yBodyRot;
    entity.setYRot(yRot0);
    entity.setXRot(xRot0);
    entity.yHeadRot = yHeadRot;
    entity.yHeadRotO = yHeadRotO;
    entity.setCustomName(customName);
    entity.setCustomNameVisible(showName);
    mc.options.hideGui = wasHideGui;

    modelViewStack.popPose();
    RenderSystem.applyModelViewMatrix();
    Lighting.setupFor3DItems();
  }
}
