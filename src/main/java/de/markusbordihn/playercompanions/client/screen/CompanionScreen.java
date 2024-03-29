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
import de.markusbordihn.playercompanions.skin.SkinType;
import de.markusbordihn.playercompanions.utils.PlayersUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CompanionScreen<T extends CompanionMenu> extends AbstractContainerScreen<T> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final float STATES_SCALE = 0.8f;
  private static final int ADD_SKIN_DELAY = 20;

  private static final ResourceLocation DIALOG_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/dialog.png");
  private static final ResourceLocation SYMBOLS_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/symbols.png");
  // Cache
  protected static int nextTextureSkinLocationChange =
      (int) java.time.Instant.now().getEpochSecond();
  protected final Entity entity;
  protected final PlayerCompanionEntity playerCompanionEntity;
  private ResourceLocation backgroundTexture =
      new ResourceLocation(Constants.MOD_ID, "textures/container/player_companion.png");
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
  private int topPosDialog = this.topPos + 90;

  public CompanionScreen(
      T menu, Inventory inventory, Component component, ResourceLocation backgroundTexture) {
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

  private static void updateNextTextureSkinLocationChange() {
    CompanionScreen.nextTextureSkinLocationChange =
        (int) java.time.Instant.now().getEpochSecond() + ADD_SKIN_DELAY;
  }

  private void renderEntityActions(
      PlayerCompanionData playerCompanionData, GuiGraphics guiGraphics, int x, int y) {
    guiGraphics.pose().pushPose();
    guiGraphics.drawString(
        this.font, Component.literal("Infos and Control"), x + 25, y, Constants.FONT_COLOR_WHITE);
    y += 15;
    guiGraphics.drawString(
        this.font,
        Component.literal("Action: " + playerCompanionData.getEntityActionType()),
        x,
        y,
        Constants.FONT_COLOR_DEFAULT,
        false);
    y += 15;
    if (playerCompanionEntity != null) {
      guiGraphics.drawString(
          this.font,
          Component.literal("Type: " + playerCompanionEntity.getCompanionType()),
          x,
          y,
          Constants.FONT_COLOR_DEFAULT,
          false);
      y += 15;
      guiGraphics.drawString(
          this.font,
          Component.literal("Variant: " + playerCompanionEntity.getVariant()),
          x,
          y,
          Constants.FONT_COLOR_DEFAULT,
          false);
      y += 15;
      guiGraphics.drawString(
          this.font,
          Component.translatable(
              Constants.TEXT_PREFIX + "tamed_companion_level",
              playerCompanionEntity.getExperienceLevel(),
              playerCompanionEntity.getExperience(),
              Experience.getExperienceForNextLevel(playerCompanionEntity.getExperienceLevel())),
          x,
          y,
          Constants.FONT_COLOR_DEFAULT,
          false);
      y += 15;
    }
    guiGraphics.pose().popPose();

    // Actions like order to sit, follow, patrol, attack, ...
    this.actionTypeFollowButton.setX(x);
    this.actionTypeFollowButton.setY(y);
    this.actionTypeFollowButton.visible = true;
    this.actionTypeFollowButton.active =
        !ActionType.FOLLOW.equals(playerCompanionData.getEntityActionType());
    y += 25;

    this.actionTypeSitButton.setX(x);
    this.actionTypeSitButton.setY(y);
    this.actionTypeSitButton.visible = true;
    this.actionTypeSitButton.active =
        !ActionType.SIT.equals(playerCompanionData.getEntityActionType());
    y += 30;

    // Aggression Level
    guiGraphics.pose().pushPose();
    guiGraphics.drawString(
        this.font,
        Component.literal("Aggression Level"),
        x,
        y,
        Constants.FONT_COLOR_DEFAULT,
        false);
    guiGraphics.pose().popPose();
    y += 12;

    AggressionLevel aggressionLevel = playerCompanionData.getEntityAggressionLevel();
    if (playerCompanionEntity != null
        && aggressionLevel != playerCompanionEntity.getFirstAggressionLevel()) {
      this.aggressiveLevelPreviousButton.setX(x);
      this.aggressiveLevelPreviousButton.setY(y - 1);
      this.aggressiveLevelPreviousButton.visible = true;
    } else {
      guiGraphics.pose().pushPose();
      guiGraphics.blit(SYMBOLS_TEXTURE, x, y - 1, 18, 19, 7, 9);
      this.aggressiveLevelNextButton.visible = false;
      guiGraphics.pose().popPose();
      this.aggressiveLevelPreviousButton.visible = false;
    }

    if (playerCompanionEntity != null
        && aggressionLevel != playerCompanionEntity.getLastAggressionLevel()) {
      this.aggressiveLevelNextButton.setX(x + 125);
      this.aggressiveLevelNextButton.setY(y - 1);
      this.aggressiveLevelNextButton.visible = true;
    } else {
      guiGraphics.pose().pushPose();
      guiGraphics.blit(SYMBOLS_TEXTURE, x + 125, y - 1, 25, 19, 7, 9);
      this.aggressiveLevelNextButton.visible = false;
      guiGraphics.pose().popPose();
    }

    guiGraphics.pose().pushPose();
    guiGraphics.fill(x, y - 2, x + 132, y - 1, 0xFF000000);
    guiGraphics.fill(x + 7, y - 1, x + 125, y + 8, 0xFF6F6F6F);
    guiGraphics.fill(x, y + 8, x + 132, y + 9, 0xFF333333);
    guiGraphics.drawString(
        this.font,
        Component.translatable(Constants.AGGRESSION_LEVEL_PREFIX + aggressionLevel.name()),
        x + 9,
        y,
        Constants.FONT_COLOR_WHITE);
    guiGraphics.pose().popPose();
    y += 10;

    aggressionLevelDefaultButton.setX(x);
    aggressionLevelDefaultButton.setY(y);
    aggressionLevelDefaultButton.visible = true;
  }

  private void renderEntityStats(
      PlayerCompanionEntity playerCompanionEntity, GuiGraphics guiGraphics, int x, int y) {
    // Background
    guiGraphics.fill(x + 24, y + 17, x + 50, y + 105, 1325400064);

    // Stats Icons
    int leftPos = x + 23;
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(0, 0, 100);
    guiGraphics.blit(SYMBOLS_TEXTURE, leftPos, y + 19, 1, 1, 16, 16); // Health
    guiGraphics.blit(SYMBOLS_TEXTURE, leftPos, y + 36, 1, 18, 16, 16); // Armor
    guiGraphics.blit(SYMBOLS_TEXTURE, leftPos, y + 53, 1, 35, 16, 16); // Attack Damage
    guiGraphics.blit(SYMBOLS_TEXTURE, leftPos, y + 90, 1, 69, 16, 16); // Experience Level
    guiGraphics.pose().popPose();

    // Stats
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(0, 0, 100);
    guiGraphics.pose().scale(STATES_SCALE, STATES_SCALE, STATES_SCALE);
    leftPos = (int) ((x + 38) / STATES_SCALE);
    guiGraphics.drawString(
        this.font,
        Component.literal(
            (int) playerCompanionEntity.getHealth()
                + " / "
                + (int) playerCompanionEntity.getMaxHealth()),
        leftPos,
        (int) ((y + 23) / STATES_SCALE),
        Constants.FONT_COLOR_WHITE);
    guiGraphics.drawString(
        this.font,
        Component.literal("" + playerCompanionEntity.getArmorValue()),
        leftPos,
        (int) ((y + 41) / STATES_SCALE),
        Constants.FONT_COLOR_WHITE);
    guiGraphics.drawString(
        this.font,
        Component.literal("" + playerCompanionEntity.getAttackDamage()),
        leftPos,
        (int) ((y + 58) / STATES_SCALE),
        Constants.FONT_COLOR_WHITE);
    guiGraphics.drawString(
        this.font,
        Component.literal("" + playerCompanionEntity.getExperienceLevel()),
        leftPos,
        (int) ((y + 95) / STATES_SCALE),
        Constants.FONT_COLOR_WHITE);
    guiGraphics.pose().popPose();
  }

  private void renderTextureSettings(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    // Make sure to render above everything else
    guiGraphics.pose().pushPose();
    guiGraphics.pose().translate(0, 0, 900);

    // Render Dialog
    renderDialogBg(
        guiGraphics,
        leftPosDialog,
        topPosDialog,
        Component.literal("Change Player Companion Skin"));

    // Render Options
    guiGraphics.drawString(
        this.font,
        Component.literal("Use a Player Name / Skin URL"),
        leftPosDialog + 10,
        topPosDialog + 25,
        Constants.FONT_COLOR_DEFAULT,
        false);

    this.textureSkinLocationBox.render(guiGraphics, x, y, partialTicks);
    this.clearTextureSettingsButton.render(guiGraphics, x, y, partialTicks);
    this.saveTextureSettingsButton.render(guiGraphics, x, y, partialTicks);
    this.closeTextureSettingsButton.render(guiGraphics, x, y, partialTicks);

    // Render Status Symbol, if needed.
    if (!this.canTextureSkinLocationChange) {
      guiGraphics.pose().translate(0, 0, 100);
      guiGraphics.blit(
          DIALOG_TEXTURE, this.leftPosDialog + 78, this.topPosDialog + 73, 236, 17, 7, 10);
    }

    guiGraphics.pose().popPose();
  }

  protected void renderDialogBg(GuiGraphics guiGraphics, int x, int y, Component title) {
    guiGraphics.blit(DIALOG_TEXTURE, x, y, 0, 0, 230, 95);
    guiGraphics.drawString(this.font, title, x + 10, y + 10, Constants.FONT_COLOR_DEFAULT, false);
  }

  protected void showTextureSettings(boolean visible) {
    this.showTextureSettings = visible;
    this.clearTextureSettingsButton.visible = visible;
    this.saveTextureSettingsButton.visible = visible;
    this.closeTextureSettingsButton.visible = visible;
    this.textureSkinLocationBox.visible = visible;

    if (visible && playerCompanionEntity != null) {
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
    if (textureSkinLocationValue != null
        && !textureSkinLocationValue.equals(this.formerTextureSkinLocation)
        && (textureSkinLocationValue.isEmpty()
            || PlayersUtils.isValidPlayerName(textureSkinLocationValue)
            || PlayersUtils.isValidUrl(textureSkinLocationValue))) {

      if (PlayersUtils.isValidPlayerName(textureSkinLocationValue)) {
        log.debug("Settings player user texture to {}", textureSkinLocationValue);
        NetworkHandler.skinChange(
            playerCompanionEntity.getUUID(), textureSkinLocationValue, SkinType.PLAYER_SKIN);
      } else if (PlayersUtils.isValidUrl(textureSkinLocationValue)) {
        log.debug("Setting remote user texture to {}", textureSkinLocationValue);
        NetworkHandler.skinChange(
            playerCompanionEntity.getUUID(),
            textureSkinLocationValue,
            SkinType.INSECURE_REMOTE_URL);
      }
      this.formerTextureSkinLocation = textureSkinLocationValue;
      updateNextTextureSkinLocationChange();
    }
  }

  private void validateTextureSkinLocation() {
    String textureSkinLocationValue = this.textureSkinLocationBox.getValue();
    this.canTextureSkinLocationChange =
        java.time.Instant.now().getEpochSecond() >= CompanionScreen.nextTextureSkinLocationChange;

    // Additional check to make sure that the server is not spammed with requests.
    if (this.canTextureSkinLocationChange) {
      this.saveTextureSettingsButton.active =
          textureSkinLocationValue != null
              && !textureSkinLocationValue.equals(this.formerTextureSkinLocation)
              && (textureSkinLocationValue.isEmpty()
                  || PlayersUtils.isValidPlayerName(textureSkinLocationValue)
                  || PlayersUtils.isValidUrl(textureSkinLocationValue));
    } else {
      this.saveTextureSettingsButton.active = textureSkinLocationValue.isEmpty();
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

    // Texture Settings
    this.textureSkinLocationBox =
        new EditBox(
            this.font,
            leftPosDialog + 10,
            topPosDialog + 42,
            190,
            20,
            Component.translatable(Constants.TEXT_PREFIX + "texture_url"));
    this.textureSkinLocationBox.setMaxLength(256);
    this.textureSkinLocationBox.setValue(this.formerTextureSkinLocation);
    this.textureSkinLocationBox.setResponder(consumer -> this.validateTextureSkinLocation());
    this.addRenderableWidget(this.textureSkinLocationBox);
    this.textureSkinLocationBox.visible = false;

    // Texture Settings Buttons
    this.clearTextureSettingsButton =
        this.addRenderableWidget(
            Button.builder(Component.literal("X"), onPress -> this.clearTextureSkinLocation())
                .bounds(this.leftPosDialog + 205, this.topPosDialog + 42, 20, 20)
                .build());
    this.clearTextureSettingsButton.visible = false;

    // Save Button
    this.saveTextureSettingsButton =
        this.addRenderableWidget(
            Button.builder(
                    Component.translatable(Constants.TEXT_PREFIX + "save"),
                    onPress -> {
                      this.saveTextureSkinLocation();
                      this.showTextureSettings(false);
                    })
                .bounds(this.leftPosDialog + 10, this.topPosDialog + 68, 80, 20)
                .build());
    this.saveTextureSettingsButton.active = false;
    this.saveTextureSettingsButton.visible = false;

    // Close Button
    this.closeTextureSettingsButton =
        this.addRenderableWidget(
            Button.builder(
                    Component.translatable(Constants.TEXT_PREFIX + "cancel"),
                    onPress -> this.showTextureSettings(false))
                .bounds(this.leftPosDialog + 145, this.topPosDialog + 68, 80, 20)
                .build());
    this.closeTextureSettingsButton.active = true;
    this.closeTextureSettingsButton.visible = false;

    // Action Type: Follow
    this.actionTypeFollowButton =
        this.addRenderableWidget(
            Button.builder(
                    Component.translatable(Constants.TEXT_PREFIX + "follow"),
                    onPress ->
                        NetworkHandler.commandPlayerCompanion(
                            playerCompanionEntity.getStringUUID(), PlayerCompanionCommand.FOLLOW))
                .bounds(this.leftPosDialog + 10, this.topPosDialog + 68, 132, 20)
                .build());
    this.actionTypeFollowButton.visible = false;

    // Action Type: Sit
    this.actionTypeSitButton =
        this.addRenderableWidget(
            Button.builder(
                    Component.translatable(Constants.TEXT_PREFIX + "sit"),
                    onPress ->
                        NetworkHandler.commandPlayerCompanion(
                            playerCompanionEntity.getStringUUID(), PlayerCompanionCommand.SIT))
                .bounds(this.leftPosDialog + 10, this.topPosDialog + 68, 132, 20)
                .build());
    this.actionTypeSitButton.visible = false;

    // Aggressive Level Previous Button
    this.aggressiveLevelPreviousButton =
        this.addRenderableWidget(
            new ImageButton(
                1,
                1,
                7,
                9,
                18,
                1,
                9,
                SYMBOLS_TEXTURE,
                onPress ->
                    NetworkHandler.commandPlayerCompanion(
                        playerCompanionEntity.getStringUUID(),
                        PlayerCompanionCommand.AGGRESSION_LEVEL_PREVIOUS)));
    this.aggressiveLevelPreviousButton.visible = false;

    // Aggressive Level Next Button
    this.aggressiveLevelNextButton =
        this.addRenderableWidget(
            new ImageButton(
                1,
                1,
                7,
                9,
                25,
                1,
                9,
                SYMBOLS_TEXTURE,
                onPress ->
                    NetworkHandler.commandPlayerCompanion(
                        playerCompanionEntity.getStringUUID(),
                        PlayerCompanionCommand.AGGRESSION_LEVEL_NEXT)));
    this.aggressiveLevelNextButton.visible = false;

    // Aggressive Level Default Button
    this.aggressionLevelDefaultButton =
        this.addRenderableWidget(
            Button.builder(
                    Component.translatable(Constants.TEXT_PREFIX + "default_aggression"),
                    onPress ->
                        NetworkHandler.commandPlayerCompanion(
                            playerCompanionEntity.getStringUUID(),
                            PlayerCompanionCommand.AGGRESSION_LEVEL_DEFAULT))
                .bounds(this.leftPosDialog + 10, this.topPosDialog + 68, 132, 20)
                .build());
    this.aggressionLevelDefaultButton.visible = false;
  }

  @Override
  public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, x, y, partialTicks);
    this.xMouse = x;
    this.yMouse = y;
    boolean renderTooltip = true;
    if (playerCompanionEntity != null) {
      PlayerCompanionData playerCompanionData =
          PlayerCompanionsClientData.getCompanion(playerCompanionEntity);
      renderEntityStats(playerCompanionEntity, guiGraphics, this.leftPos, this.topPos);
      renderEntityActions(playerCompanionData, guiGraphics, this.leftPos + 204, this.topPos + 6);
      if (this.showTextureSettings) {
        renderTextureSettings(guiGraphics, x, y, partialTicks);
        renderTooltip = false;
      }
    }
    if (renderTooltip) {
      this.renderTooltip(guiGraphics, x, y);
    }
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    // Main screen
    guiGraphics.blit(
        this.backgroundTexture, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 512, 256);

    // Entity Overview
    if (playerCompanionEntity != null) {
      CompanionScreenHelper.renderEntity(
          this.leftPos + 59,
          this.topPos + 70 + playerCompanionEntity.getEntityGuiTop(),
          this.leftPos + 58 - this.xMouse,
          this.topPos + 40 - this.yMouse,
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
}
