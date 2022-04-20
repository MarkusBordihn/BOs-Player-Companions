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
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.CompanionMenu;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.network.NetworkHandler;
import de.markusbordihn.playercompanions.utils.PlayersUtils;

@OnlyIn(Dist.CLIENT)
public class CompanionScreen<T extends CompanionMenu> extends AbstractContainerScreen<T> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final ResourceLocation DIALOG_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/dialog.png");
  private static final float STATES_SCALE = 0.8f;

  private ResourceLocation backgroundTexture =
      new ResourceLocation(Constants.MOD_ID, "textures/container/player_companion.png");

  private final Entity entity;

  private Button closeTextureSettingsButton = null;
  private Button saveTextureSettingsButton = null;
  private EditBox textureSkinLocationBox;
  private String formerTextureSkinLocation = "";
  private boolean showTextureSettings = false;
  private float xMouse;
  private float yMouse;
  private int leftPosDialog = this.leftPos - 18;
  private int topPosDialog = this.topPos + 90;

  public CompanionScreen(T menu, Inventory inventory, Component component,
      ResourceLocation backgroundTexture) {
    super(menu, inventory, component);
    this.backgroundTexture = backgroundTexture;
    this.entity = menu.getPlayerCompanionEntity();
  }

  public CompanionScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.entity = menu.getPlayerCompanionEntity();
  }

  private void renderEntityStats(PlayerCompanionEntity playerCompanionEntity, PoseStack poseStack,
      int x, int y) {
    RenderSystem.setShaderTexture(0, this.backgroundTexture);

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

  private void renderTextureSettings(PoseStack poseStack, int x, int y, float partialTicks) {
    // Make sure to render above everything else
    poseStack.pushPose();
    poseStack.translate(0, 0, 900);

    // Render Dialog
    renderDialogBg(poseStack, leftPosDialog, topPosDialog,
        new TextComponent("Change Player Companion Skin"));

    // Render Options
    font.draw(poseStack, new TextComponent("Use a Player Name / Skin URL"), leftPosDialog + 10F,
        topPosDialog + 25F, 4210752);

    this.textureSkinLocationBox.render(poseStack, x, y, partialTicks);
    this.saveTextureSettingsButton.render(poseStack, x, y, partialTicks);
    this.closeTextureSettingsButton.render(poseStack, x, y, partialTicks);
    poseStack.popPose();
  }

  protected void renderDialogBg(PoseStack poseStack, int x, int y, Component title) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, DIALOG_TEXTURE);
    this.blit(poseStack, x, y, 0, 0, 230, 95);
    font.draw(poseStack, title, x + 10F, y + 10F, 4210752);
  }

  protected void showTextureSettings(boolean visible) {
    this.showTextureSettings = visible;
    this.saveTextureSettingsButton.visible = visible;
    this.closeTextureSettingsButton.visible = visible;
    this.textureSkinLocationBox.visible = visible;
    this.minecraft.keyboardHandler.setSendRepeatsToGui(visible);
    if (visible && entity instanceof PlayerCompanionEntity playerCompanionEntity) {
      this.formerTextureSkinLocation = playerCompanionEntity.getUserTexture();
      this.textureSkinLocationBox.setValue(formerTextureSkinLocation);
    }
  }

  private void saveTextureSkinLocation() {
    String textureSkinLocationValue = this.textureSkinLocationBox.getValue();
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity
        && textureSkinLocationValue != null
        && !textureSkinLocationValue.equals(this.formerTextureSkinLocation)
        && (textureSkinLocationValue.isEmpty()
            || PlayersUtils.isValidPlayerName(textureSkinLocationValue)
            || PlayersUtils.isValidUrl(textureSkinLocationValue))) {
      NetworkHandler.skinChangePlayerCompanion(playerCompanionEntity.getStringUUID(),
          textureSkinLocationValue);
    }
  }

  private void validateTextureSkinLocation() {
    String textureSkinLocationValue = this.textureSkinLocationBox.getValue();
    this.saveTextureSettingsButton.active = textureSkinLocationValue != null
        && !textureSkinLocationValue.equals(this.formerTextureSkinLocation)
        && (textureSkinLocationValue.isEmpty()
            || PlayersUtils.isValidPlayerName(textureSkinLocationValue)
            || PlayersUtils.isValidUrl(textureSkinLocationValue));
  }

  @Override
  public void init() {
    super.init();

    // Default stats
    this.imageWidth = 194;
    this.imageHeight = 230;
    this.titleLabelX = 7;
    this.titleLabelY = 7;
    this.topPos = (this.height - this.imageHeight) / 2;
    this.inventoryLabelY = this.imageHeight - 92;

    // Position for the dialog
    this.leftPosDialog = this.leftPos - 18;
    this.topPosDialog = this.topPos + 90;

    // Player Companions settings
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
      this.formerTextureSkinLocation = playerCompanionEntity.getUserTexture();
    }

    // Texture Settings
    this.textureSkinLocationBox = new EditBox(this.font, leftPosDialog + 10, topPosDialog + 42, 210,
        20, new TranslatableComponent("Texture URL"));
    this.textureSkinLocationBox.setMaxLength(256);
    this.textureSkinLocationBox.setValue(this.formerTextureSkinLocation);
    this.textureSkinLocationBox.setResponder(consumer -> this.validateTextureSkinLocation());
    this.addRenderableWidget(this.textureSkinLocationBox);
    this.textureSkinLocationBox.visible = false;

    // Texture Settings Buttons
    this.saveTextureSettingsButton = this.addRenderableWidget(new Button(this.leftPosDialog + 20,
        this.topPosDialog + 70, 80, 20, new TranslatableComponent("Save"), onPress -> {
          this.saveTextureSkinLocation();
          this.showTextureSettings(false);
        }));
    this.saveTextureSettingsButton.active = false;
    this.saveTextureSettingsButton.visible = false;

    this.closeTextureSettingsButton =
        this.addRenderableWidget(new Button(this.leftPosDialog + 130, this.topPosDialog + 70, 80,
            20, new TranslatableComponent("Close"), onPress -> this.showTextureSettings(false)));
    this.closeTextureSettingsButton.active = true;
    this.closeTextureSettingsButton.visible = false;
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    this.xMouse = x;
    this.yMouse = y;
    boolean renderTooltip = true;
    super.render(poseStack, x, y, partialTicks);
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
      renderEntityStats(playerCompanionEntity, poseStack, this.leftPos, this.topPos);
      if (this.showTextureSettings) {
        renderTextureSettings(poseStack, x, y, partialTicks);
        renderTooltip = false;
      }
    }
    if (renderTooltip) {
      this.renderTooltip(poseStack, x, y);
    }
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, this.backgroundTexture);

    // Main screen
    this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

    // Entity Overview
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
      renderEntity(this.leftPos + 59, this.topPos + 70 + playerCompanionEntity.getEntityGuiTop(),
          this.leftPos + 51 - this.xMouse, this.topPos + 75 - 50 - this.yMouse,
          playerCompanionEntity);
    }
  }

  @Override
  public boolean keyPressed(int keyCode, int unused1, int unused2) {
    if (!this.showTextureSettings || (keyCode != 257 && keyCode != 335 && keyCode != 69)) {
      return super.keyPressed(keyCode, unused1, unused2);
    } else if (keyCode == 256) {
      this.showTextureSettings(false);
      return true;
    } else if (keyCode == 257 || keyCode == 335) {
      this.showTextureSettings(false);
      this.saveTextureSkinLocation();
      return true;
    } else {
      return true;
    }
  }

  @Override
  public void removed() {
    this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    super.removed();
  }

}
