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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

// import org.anti_ad.mc.ipn.api.IPNIgnore;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.CompanionMenu;
import de.markusbordihn.playercompanions.data.Experience;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.entity.ActionType;
import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.PlayerCompanionCommand;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.network.NetworkHandler;
import de.markusbordihn.playercompanions.utils.PlayersUtils;

// @IPNIgnore
@OnlyIn(Dist.CLIENT)
public class CompanionScreen<T extends CompanionMenu> extends AbstractContainerScreen<T> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final float STATES_SCALE = 0.8f;

  private static final ResourceLocation DIALOG_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/dialog.png");
  private static final ResourceLocation SYMBOLS_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/symbols.png");
  private ResourceLocation backgroundTexture =
      new ResourceLocation(Constants.MOD_ID, "textures/container/player_companion.png");

  protected final Entity entity;
  protected final PlayerCompanionEntity playerCompanionEntity;

  private Button clearTextureSettingsButton = null;
  private Button closeTextureSettingsButton = null;
  private Button saveTextureSettingsButton = null;

  private Button actionTypeFollowButton = null;
  private Button actionTypeSitButton = null;

  private Button aggressionLevelDefaultButton = null;
  private ImageButton aggressiveLevelPreviousButton = null;
  private ImageButton aggressiveLevelNextButton = null;

  private EditBox textureSkinLocationBox;
  private String formerTextureSkinLocation = "";
  private boolean showTextureSettings = false;
  private boolean canTextureSkinLocationChange = true;
  private float xMouse;
  private float yMouse;
  private int leftPosDialog = this.leftPos - 18;
  private int nextTextureSkinLocationChange = (int) java.time.Instant.now().getEpochSecond();
  private int topPosDialog = this.topPos + 90;

  public CompanionScreen(T menu, Inventory inventory, Component component,
      ResourceLocation backgroundTexture) {
    this(menu, inventory, component);
    this.backgroundTexture = backgroundTexture;
  }

  public CompanionScreen(T menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.entity = menu.getPlayerCompanionEntity();
    if (entity instanceof PlayerCompanionEntity playerCompanionEntityCast) {
      this.playerCompanionEntity = playerCompanionEntityCast;
    } else {
      this.playerCompanionEntity = null;
    }
  }

  private void renderEntityActions(PlayerCompanionData playerCompanionData, PoseStack poseStack,
      int x, int y) {
    poseStack.pushPose();
    font.drawShadow(poseStack, new TextComponent("Infos and Control"), x + 25f, y,
        Constants.FONT_COLOR_WHITE);
    y += 15;
    font.draw(poseStack, new TextComponent("Action: " + playerCompanionData.getEntityActionType()),
        x, y, Constants.FONT_COLOR_DEFAULT);
    y += 15;
    if (playerCompanionEntity != null) {
      font.draw(poseStack, new TextComponent("Type: " + playerCompanionEntity.getCompanionType()),
          x, y, Constants.FONT_COLOR_DEFAULT);
      y += 15;
      font.draw(poseStack, new TextComponent("Variant: " + playerCompanionEntity.getVariant()), x,
          y, Constants.FONT_COLOR_DEFAULT);
      y += 15;
      font.draw(poseStack,
          new TranslatableComponent(Constants.TEXT_PREFIX + "tamed_companion_level",
              playerCompanionEntity.getExperienceLevel(), playerCompanionEntity.getExperience(),
              Experience.getExperienceForNextLevel(playerCompanionEntity.getExperienceLevel())),
          x, y, Constants.FONT_COLOR_DEFAULT);
      y += 15;
    }
    poseStack.popPose();

    // Actions like order to sit, follow, patrol, attack, ...
    this.actionTypeFollowButton.x = x;
    this.actionTypeFollowButton.y = y;
    this.actionTypeFollowButton.visible = true;
    this.actionTypeFollowButton.active =
        !ActionType.FOLLOW.equals(playerCompanionData.getEntityActionType());
    y += 25;

    this.actionTypeSitButton.x = x;
    this.actionTypeSitButton.y = y;
    this.actionTypeSitButton.visible = true;
    this.actionTypeSitButton.active =
        !ActionType.SIT.equals(playerCompanionData.getEntityActionType());
    y += 30;

    // Aggression Level
    poseStack.pushPose();
    font.draw(poseStack, new TextComponent("Aggression Level (WIP)"), x, y,
        Constants.FONT_COLOR_DEFAULT);
    poseStack.popPose();
    y += 12;

    AggressionLevel aggressionLevel = playerCompanionData.getEntityAggressionLevel();
    if (aggressionLevel != playerCompanionEntity.getFirstAggressionLevel()) {
      this.aggressiveLevelPreviousButton.x = x;
      this.aggressiveLevelPreviousButton.y = y - 1;
      this.aggressiveLevelPreviousButton.visible = true;
    } else {
      RenderSystem.setShaderTexture(0, SYMBOLS_TEXTURE);
      poseStack.pushPose();
      this.blit(poseStack, x, y + -1, 18, 19, 7, 9);
      this.aggressiveLevelNextButton.visible = false;
      poseStack.popPose();
      this.aggressiveLevelPreviousButton.visible = false;
    }

    if (aggressionLevel != playerCompanionEntity.getLastAggressionLevel()) {
      this.aggressiveLevelNextButton.x = x + 125;
      this.aggressiveLevelNextButton.y = y - 1;
      this.aggressiveLevelNextButton.visible = true;
    } else {
      RenderSystem.setShaderTexture(0, SYMBOLS_TEXTURE);
      poseStack.pushPose();
      this.blit(poseStack, x + 125, y + -1, 25, 19, 7, 9);
      this.aggressiveLevelNextButton.visible = false;
      poseStack.popPose();
    }

    poseStack.pushPose();
    fill(poseStack, x, y - 2, x + 132, y - 1, 0xFF000000);
    fill(poseStack, x + 7, y - 1, x + 125, y + 8, 0xFF6F6F6F);
    fill(poseStack, x, y + 8, x + 132, y + 9, 0xFF333333);
    font.drawShadow(poseStack,
        new TranslatableComponent(Constants.AGGRESSION_LEVEL_PREFIX + aggressionLevel.name()),
        x + 9f, y, Constants.FONT_COLOR_WHITE);
    poseStack.popPose();
    y += 10;

    aggressionLevelDefaultButton.x = x;
    aggressionLevelDefaultButton.y = y;
    aggressionLevelDefaultButton.visible = true;
  }

  private void renderEntityStats(PlayerCompanionEntity playerCompanionEntity, PoseStack poseStack,
      int x, int y) {
    RenderSystem.setShaderTexture(0, this.backgroundTexture);

    // Background
    fill(poseStack, x + 24, y + 17, x + 50, y + 105, 1325400064);

    // Stats Icons
    int leftPos = x + 23;
    RenderSystem.setShaderTexture(0, SYMBOLS_TEXTURE);
    poseStack.pushPose();
    poseStack.translate(0, 0, 100);
    this.blit(poseStack, leftPos, y + 19, 1, 1, 16, 16); // Health
    this.blit(poseStack, leftPos, y + 36, 1, 18, 16, 16); // Armor
    this.blit(poseStack, leftPos, y + 53, 1, 35, 16, 16); // Attack Damage
    this.blit(poseStack, leftPos, y + 90, 1, 69, 16, 16); // Experience Level
    poseStack.popPose();

    // Stats
    poseStack.pushPose();
    poseStack.translate(0, 0, 100);
    poseStack.scale(STATES_SCALE, STATES_SCALE, STATES_SCALE);
    leftPos = (int) ((x + 38) / STATES_SCALE);
    font.drawShadow(poseStack,
        new TextComponent("" + (int) playerCompanionEntity.getHealth() + " / "
            + (int) playerCompanionEntity.getMaxHealth()),
        leftPos, (int) ((y + 23) / STATES_SCALE), Constants.FONT_COLOR_WHITE);
    font.drawShadow(poseStack, new TextComponent("" + playerCompanionEntity.getArmorValue()),
        leftPos, (int) ((y + 41) / STATES_SCALE), Constants.FONT_COLOR_WHITE);
    font.drawShadow(poseStack, new TextComponent("" + playerCompanionEntity.getAttackDamage()),
        leftPos, (int) ((y + 58) / STATES_SCALE), Constants.FONT_COLOR_WHITE);
    font.drawShadow(poseStack, new TextComponent("" + playerCompanionEntity.getExperienceLevel()),
        leftPos, (int) ((y + 95) / STATES_SCALE), Constants.FONT_COLOR_WHITE);
    poseStack.popPose();
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
        topPosDialog + 25F, Constants.FONT_COLOR_DEFAULT);

    this.textureSkinLocationBox.render(poseStack, x, y, partialTicks);
    this.clearTextureSettingsButton.render(poseStack, x, y, partialTicks);
    this.saveTextureSettingsButton.render(poseStack, x, y, partialTicks);
    this.closeTextureSettingsButton.render(poseStack, x, y, partialTicks);

    // Render Status Symbol, if needed.
    if (!this.canTextureSkinLocationChange) {
      RenderSystem.setShaderTexture(0, DIALOG_TEXTURE);
      poseStack.translate(0, 0, 100);
      this.blit(poseStack, this.leftPosDialog + 78, this.topPosDialog + 73, 236, 17, 7, 10);
    }

    poseStack.popPose();
  }

  protected void renderDialogBg(PoseStack poseStack, int x, int y, Component title) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, DIALOG_TEXTURE);
    this.blit(poseStack, x, y, 0, 0, 230, 95);
    font.draw(poseStack, title, x + 10F, y + 10F, Constants.FONT_COLOR_DEFAULT);
  }

  protected void showTextureSettings(boolean visible) {
    this.showTextureSettings = visible;
    this.clearTextureSettingsButton.visible = visible;
    this.saveTextureSettingsButton.visible = visible;
    this.closeTextureSettingsButton.visible = visible;
    this.textureSkinLocationBox.visible = visible;
    this.minecraft.keyboardHandler.setSendRepeatsToGui(visible);
    if (visible && playerCompanionEntity != null) {
      this.formerTextureSkinLocation = playerCompanionEntity.getCustomTextureSkin();
      this.textureSkinLocationBox.setValue(formerTextureSkinLocation);
    }
  }

  private void clearTextureSkinLocation() {
    if (!this.textureSkinLocationBox.getValue().isEmpty()) {
      this.textureSkinLocationBox.setValue("");
    }
  }

  private void saveTextureSkinLocation() {
    String textureSkinLocationValue = this.textureSkinLocationBox.getValue();
    if (playerCompanionEntity != null && textureSkinLocationValue != null
        && !textureSkinLocationValue.equals(this.formerTextureSkinLocation)
        && (textureSkinLocationValue.isEmpty()
            || PlayersUtils.isValidPlayerName(textureSkinLocationValue)
            || PlayersUtils.isValidUrl(textureSkinLocationValue))) {
      NetworkHandler.skinChangePlayerCompanion(playerCompanionEntity.getStringUUID(),
          textureSkinLocationValue);
      this.nextTextureSkinLocationChange = (int) java.time.Instant.now().getEpochSecond() + 30;
    }
  }

  private void validateTextureSkinLocation() {
    String textureSkinLocationValue = this.textureSkinLocationBox.getValue();
    this.canTextureSkinLocationChange =
        java.time.Instant.now().getEpochSecond() >= this.nextTextureSkinLocationChange;

    // Additional check to make sure that the server is not spammed with requests.
    if (this.canTextureSkinLocationChange) {
      this.saveTextureSettingsButton.active = textureSkinLocationValue != null
          && !textureSkinLocationValue.equals(this.formerTextureSkinLocation)
          && (textureSkinLocationValue.isEmpty()
              || PlayersUtils.isValidPlayerName(textureSkinLocationValue)
              || PlayersUtils.isValidUrl(textureSkinLocationValue));
    } else if (textureSkinLocationValue.isEmpty()) {
      this.saveTextureSettingsButton.active = true;
    } else {
      this.saveTextureSettingsButton.active = false;
    }
    this.clearTextureSettingsButton.active =
        textureSkinLocationValue != null && !textureSkinLocationValue.isEmpty();
  }

  @Override
  public void init() {
    super.init();

    // Default stats
    this.imageWidth = 350;
    this.imageHeight = 230;
    this.titleLabelX = 6;
    this.titleLabelY = 6;
    this.topPos = (this.height - this.imageHeight) / 2;
    this.leftPos = (this.width - this.imageWidth) / 2;
    this.inventoryLabelX = 6;
    this.inventoryLabelY = this.imageHeight - 91;

    // Position for the dialog
    this.leftPosDialog = this.leftPos - 18;
    this.topPosDialog = this.topPos + 85;

    // Player Companions settings
    if (playerCompanionEntity != null) {
      this.formerTextureSkinLocation = playerCompanionEntity.getCustomTextureSkin();
    }

    // Texture Settings
    this.textureSkinLocationBox = new EditBox(this.font, leftPosDialog + 10, topPosDialog + 42, 190,
        20, new TranslatableComponent("Texture URL"));
    this.textureSkinLocationBox.setMaxLength(256);
    this.textureSkinLocationBox.setValue(this.formerTextureSkinLocation);
    this.textureSkinLocationBox.setResponder(consumer -> this.validateTextureSkinLocation());
    this.addRenderableWidget(this.textureSkinLocationBox);
    this.textureSkinLocationBox.visible = false;

    // Texture Settings Buttons
    this.clearTextureSettingsButton =
        this.addRenderableWidget(new Button(this.leftPosDialog + 205, this.topPosDialog + 42, 20,
            20, new TextComponent("X"), onPress -> this.clearTextureSkinLocation()));
    this.clearTextureSettingsButton.visible = false;

    // Save Button
    this.saveTextureSettingsButton = this.addRenderableWidget(new Button(this.leftPosDialog + 10,
        this.topPosDialog + 68, 80, 20, new TranslatableComponent("Save"), onPress -> {
          this.saveTextureSkinLocation();
          this.showTextureSettings(false);
        }));
    this.saveTextureSettingsButton.active = false;
    this.saveTextureSettingsButton.visible = false;

    // Close Button
    this.closeTextureSettingsButton =
        this.addRenderableWidget(new Button(this.leftPosDialog + 145, this.topPosDialog + 68, 80,
            20, new TranslatableComponent("Cancel"), onPress -> this.showTextureSettings(false)));
    this.closeTextureSettingsButton.active = true;
    this.closeTextureSettingsButton.visible = false;

    // Action Type: Follow
    this.actionTypeFollowButton = this.addRenderableWidget(new Button(this.leftPosDialog + 10,
        this.topPosDialog + 68, 132, 20, new TranslatableComponent("Follow"), onPress -> {
          NetworkHandler.commandPlayerCompanion(playerCompanionEntity.getStringUUID(),
              PlayerCompanionCommand.FOLLOW);
        }));
    this.actionTypeFollowButton.visible = false;

    // Action Type: Sit
    this.actionTypeSitButton = this.addRenderableWidget(new Button(this.leftPosDialog + 10,
        this.topPosDialog + 68, 132, 20, new TranslatableComponent("Sit"), onPress -> {
          NetworkHandler.commandPlayerCompanion(playerCompanionEntity.getStringUUID(),
              PlayerCompanionCommand.SIT);
        }));
    this.actionTypeSitButton.visible = false;

    // Aggressive Level Previous Button
    this.aggressiveLevelPreviousButton =
        this.addRenderableWidget(new ImageButton(1, 1, 7, 9, 18, 1, 9, SYMBOLS_TEXTURE, onPress -> {
          NetworkHandler.commandPlayerCompanion(playerCompanionEntity.getStringUUID(),
              PlayerCompanionCommand.AGGRESSION_LEVEL_PREVIOUS);
        }));
    this.aggressiveLevelPreviousButton.visible = false;

    // Aggressive Level Next Button
    this.aggressiveLevelNextButton =
        this.addRenderableWidget(new ImageButton(1, 1, 7, 9, 25, 1, 9, SYMBOLS_TEXTURE, onPress -> {
          NetworkHandler.commandPlayerCompanion(playerCompanionEntity.getStringUUID(),
              PlayerCompanionCommand.AGGRESSION_LEVEL_NEXT);
        }));
    this.aggressiveLevelNextButton.visible = false;

    // Aggressive Level Default Button
    this.aggressionLevelDefaultButton =
        this.addRenderableWidget(new Button(this.leftPosDialog + 10, this.topPosDialog + 68, 132,
            20, new TranslatableComponent("Default Aggression"), onPress -> {
              NetworkHandler.commandPlayerCompanion(playerCompanionEntity.getStringUUID(),
                  PlayerCompanionCommand.AGGRESSION_LEVEL_DEFAULT);
            }));
    this.aggressionLevelDefaultButton.visible = false;
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    super.render(poseStack, x, y, partialTicks);
    this.xMouse = x;
    this.yMouse = y;
    boolean renderTooltip = true;
    if (playerCompanionEntity != null) {
      PlayerCompanionData playerCompanionData =
          PlayerCompanionsClientData.getCompanion(playerCompanionEntity);
      renderEntityStats(playerCompanionEntity, poseStack, this.leftPos, this.topPos);
      renderEntityActions(playerCompanionData, poseStack, this.leftPos + 204, this.topPos + 6);
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
    GuiComponent.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 512,
        256);

    // Entity Overview
    if (playerCompanionEntity != null) {
      CompanionScreenHelper.renderEntity(this.leftPos + 59,
          this.topPos + 70 + playerCompanionEntity.getEntityGuiTop(),
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
      this.saveTextureSkinLocation();
      this.showTextureSettings(false);
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
