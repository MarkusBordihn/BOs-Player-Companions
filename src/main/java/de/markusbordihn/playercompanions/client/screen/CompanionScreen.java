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

package de.markusbordihn.playercompanions.client.screen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.CompanionMenu;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

@OnlyIn(Dist.CLIENT)
public class CompanionScreen<T extends CompanionMenu> extends AbstractContainerScreen<T> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final float STATES_SCALE = 0.8f;

  private final Entity entity;
  private ResourceLocation texture =
      new ResourceLocation(Constants.MOD_ID, "textures/container/player_companion.png");
  private float xMouse;
  private float yMouse;

  public CompanionScreen(T menu, Inventory inventory, Component component,
      ResourceLocation texture) {
    super(menu, inventory, component);
    this.texture = texture;
    this.entity = menu.getPlayerCompanionEntity();
  }

  public CompanionScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.entity = menu.getPlayerCompanionEntity();
  }

  private void renderEntityStats(PlayerCompanionEntity playerCompanionEntity, PoseStack poseStack,
      int x, int y) {
    RenderSystem.setShaderTexture(0, this.texture);

    // Background
    fill(poseStack, x + 24, y + 17, x + 50, y + 105, 1325400064);

    // Stats Icons
    int leftPos = x + 27;
    poseStack.pushPose();
    poseStack.translate(0, 0, 100);
    this.blit(poseStack, leftPos, y + 22, 200, 22, 16, 16);
    this.blit(poseStack, leftPos, y + 39, 200, 39, 16, 16);
    this.blit(poseStack, leftPos, y + 56, 200, 56, 16, 16);
    this.blit(poseStack, leftPos, y + 93, 200, 93, 16, 16);
    poseStack.popPose();

    // Stats
    poseStack.pushPose();
    poseStack.translate(0, 0, 100);
    poseStack.scale(STATES_SCALE, STATES_SCALE, STATES_SCALE);
    leftPos = (int) ((x + 38) / STATES_SCALE);
    font.drawShadow(poseStack,
        new TextComponent("" + (int) playerCompanionEntity.getHealth() + " / "
            + (int) playerCompanionEntity.getMaxHealth()),
        leftPos, (int) ((y + 23) / STATES_SCALE), ChatFormatting.WHITE.getColor());
    font.draw(poseStack, new TextComponent("" + playerCompanionEntity.getArmorValue()), leftPos,
        (int) ((y + 41) / STATES_SCALE), ChatFormatting.WHITE.getColor());
    font.drawShadow(poseStack, new TextComponent("" + playerCompanionEntity.getArmorValue()),
        leftPos, (int) ((y + 41) / STATES_SCALE), ChatFormatting.WHITE.getColor());
    font.drawShadow(poseStack, new TextComponent("" + playerCompanionEntity.getAttackDamage()),
        leftPos, (int) ((y + 58) / STATES_SCALE), ChatFormatting.WHITE.getColor());
    font.drawShadow(poseStack, new TextComponent("" + playerCompanionEntity.getExperienceLevel()),
        leftPos, (int) ((y + 95) / STATES_SCALE), ChatFormatting.WHITE.getColor());
    poseStack.popPose();
  }

  private void renderEntity(int x, int y, float yRot, float xRot,
      PlayerCompanionEntity playerCompanionEntity) {
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
    Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
    Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
    quaternion.mul(quaternion1);
    poseStack1.mulPose(quaternion);
    float entityYBodyRot = playerCompanionEntity.yBodyRot;
    float entityYRot = playerCompanionEntity.getYRot();
    float entityXRot = playerCompanionEntity.getXRot();
    float entityYHeadRotO = playerCompanionEntity.yHeadRotO;
    float entityYHeadRot = playerCompanionEntity.yHeadRot;
    playerCompanionEntity.yBodyRot = 180.0F + f * 20.0F;
    playerCompanionEntity.setYRot(180.0F + f * 40.0F);
    playerCompanionEntity.setXRot(-f1 * 20.0F);
    playerCompanionEntity.yHeadRot = playerCompanionEntity.getYRot();
    playerCompanionEntity.yHeadRotO = playerCompanionEntity.getYRot();
    Component customName = playerCompanionEntity.getCustomName();
    playerCompanionEntity.setCustomName(null);
    Lighting.setupForEntityInInventory();
    EntityRenderDispatcher entityRenderDispatcher =
        Minecraft.getInstance().getEntityRenderDispatcher();
    quaternion1.conj();
    entityRenderDispatcher.overrideCameraOrientation(quaternion1);
    entityRenderDispatcher.setRenderShadow(false);
    MultiBufferSource.BufferSource multiBuffer =
        Minecraft.getInstance().renderBuffers().bufferSource();
    entityRenderDispatcher.render(playerCompanionEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, poseStack1,
        multiBuffer, 15728880);
    multiBuffer.endBatch();
    entityRenderDispatcher.setRenderShadow(true);
    playerCompanionEntity.yBodyRot = entityYBodyRot;
    playerCompanionEntity.setYRot(entityYRot);
    playerCompanionEntity.setXRot(entityXRot);
    playerCompanionEntity.yHeadRotO = entityYHeadRotO;
    playerCompanionEntity.yHeadRot = entityYHeadRot;
    playerCompanionEntity.setCustomName(customName);
    poseStack.popPose();
    RenderSystem.applyModelViewMatrix();
    Lighting.setupFor3DItems();
  }

  @Override
  public void init() {
    super.init();
    this.imageWidth = 194;
    this.imageHeight = 230;
    this.titleLabelX = 7;
    this.titleLabelY = 7;
    this.topPos = (this.height - this.imageHeight) / 2;
    this.inventoryLabelY = this.imageHeight - 92;
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    this.xMouse = x;
    this.yMouse = y;
    super.render(poseStack, x, y, partialTicks);
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
      renderEntityStats(playerCompanionEntity, poseStack, this.leftPos, this.topPos);
    }
    this.renderTooltip(poseStack, x, y);
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, this.texture);

    // Main screen
    this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

    // Entity Overview
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
      renderEntity(this.leftPos + 59, this.topPos + 70 + playerCompanionEntity.getEntityGuiTop(),
          this.leftPos + 51 - this.xMouse, this.topPos + 75 - 50 - this.yMouse,
          playerCompanionEntity);
    }
  }

}
