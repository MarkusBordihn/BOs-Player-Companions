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

import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.ProgressionDataCapable;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.CompanionRole;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.menu.CompanionMenu;
import de.markusbordihn.playercompanions.network.CompanionNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;

public class CompanionScreen<T extends CompanionMenu> extends AbstractContainerScreen<T> {

  static final ResourceLocation TEXTURE_INVENTORY =
    new ResourceLocation("minecraft", "textures/gui/container/inventory.png");
  static final ResourceLocation TEXTURE_DEMO_BG =
    new ResourceLocation("minecraft", "textures/gui/demo_background.png");

  static final int SCREEN_WIDTH = 350;
  static final int SCREEN_HEIGHT = 230;

  private static final int PREVIEW_CENTER_X = 59;
  private static final int PREVIEW_CENTER_Y = 70;
  private static final int PREVIEW_SCALE = 40;
  private static final int INFO_PANEL_X = 204;
  private static final int TEXTURE_PANEL_WIDTH = 248;
  private static final int TEXTURE_PANEL_HEIGHT = 166;
  private static final int PANEL_SEPARATOR_WIDTH = 5;
  private static final int LEFT_PANEL_WIDTH = INFO_PANEL_X - PANEL_SEPARATOR_WIDTH * 2; // 194
  private static final int RIGHT_PANEL_X_OFFSET = INFO_PANEL_X - PANEL_SEPARATOR_WIDTH; // 199

  protected Mob companionEntity;
  private float mousePositionX;
  private float mousePositionY;
  private Button followButton;
  private Button sitButton;
  private Button wanderButton;
  private Button aggressionButton;
  private Button collectorToggleButton;

  public CompanionScreen(T menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
  }

  private static void blitPanel(GuiGraphics guiGraphics, int x, int y,
    int panelWidth, int panelHeight, int leftSectionWidth, int topSectionHeight) {
    int rightSectionWidth = panelWidth - leftSectionWidth;
    int rightSectionU = TEXTURE_PANEL_WIDTH - rightSectionWidth;
    int bottomSectionHeight = panelHeight - topSectionHeight;
    int bottomSectionV = TEXTURE_PANEL_HEIGHT - bottomSectionHeight;

    guiGraphics.blit(TEXTURE_DEMO_BG, x, y,
      leftSectionWidth, topSectionHeight, 0, 0, leftSectionWidth, topSectionHeight, 256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, x + leftSectionWidth, y,
      rightSectionWidth, topSectionHeight, rightSectionU, 0, rightSectionWidth, topSectionHeight,
      256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, x, y + topSectionHeight,
      leftSectionWidth, bottomSectionHeight, 0, bottomSectionV, leftSectionWidth,
      bottomSectionHeight, 256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, x + leftSectionWidth, y + topSectionHeight,
      rightSectionWidth, bottomSectionHeight, rightSectionU, bottomSectionV, rightSectionWidth,
      bottomSectionHeight, 256, 256);
  }

  static void renderWindowBg(GuiGraphics guiGraphics, int originX, int originY) {
    int topSectionHeight = TEXTURE_PANEL_HEIGHT / 2; // 83

    blitPanel(guiGraphics, originX, originY, LEFT_PANEL_WIDTH, SCREEN_HEIGHT, 169,
      topSectionHeight);
    guiGraphics.fill(
      originX + LEFT_PANEL_WIDTH, originY + PANEL_SEPARATOR_WIDTH,
      originX + RIGHT_PANEL_X_OFFSET, originY + SCREEN_HEIGHT - PANEL_SEPARATOR_WIDTH,
      0xFF555555);
    blitPanel(guiGraphics, originX + RIGHT_PANEL_X_OFFSET, originY,
      SCREEN_WIDTH - RIGHT_PANEL_X_OFFSET, SCREEN_HEIGHT, 132, topSectionHeight);
  }

  static void renderCompanionSlots(GuiGraphics guiGraphics, int originX, int originY) {
    for (int i = 0; i < 4; i++) {
      guiGraphics.blit(TEXTURE_INVENTORY, originX + 5, originY + 16 + i * 18, 18, 18, 7, 7, 18, 18,
        256, 256);
      guiGraphics.blit(TEXTURE_INVENTORY, originX + 95, originY + 16 + i * 18, 18, 18, 7, 7, 18, 18,
        256, 256);
    }
    guiGraphics.blit(TEXTURE_INVENTORY, originX + 5, originY + 88, 18, 18, 7, 7, 18, 18, 256, 256);
    guiGraphics.blit(TEXTURE_INVENTORY, originX + 95, originY + 88, 18, 18, 7, 7, 18, 18, 256, 256);
  }

  static void renderPlayerSlots(GuiGraphics guiGraphics, int originX, int originY) {
    guiGraphics.blit(TEXTURE_INVENTORY, originX + 5, originY + 150, 162, 54, 7, 83, 162, 54, 256,
      256);
    guiGraphics.blit(TEXTURE_INVENTORY, originX + 5, originY + 208, 162, 18, 7, 141, 162, 18, 256,
      256);
  }

  @Override
  public void init() {
    super.init();
    this.imageWidth = SCREEN_WIDTH;
    this.imageHeight = SCREEN_HEIGHT;
    this.titleLabelX = 6;
    this.titleLabelY = 6;
    this.topPos = (this.height - SCREEN_HEIGHT) / 2;
    this.leftPos = (this.width - SCREEN_WIDTH) / 2;
    this.inventoryLabelX = 6;
    this.inventoryLabelY = SCREEN_HEIGHT - 91;

    int buttonX = this.leftPos + INFO_PANEL_X;
    int buttonY = this.topPos + 100;
    followButton = this.addRenderableWidget(
      Button.builder(Component.translatable("playercompanions.command.follow"),
          btn -> sendCommand(CompanionCommand.FOLLOW))
        .bounds(buttonX, buttonY, 132, 20).build());
    sitButton = this.addRenderableWidget(
      Button.builder(Component.translatable("playercompanions.command.sit"),
          btn -> sendCommand(CompanionCommand.SIT))
        .bounds(buttonX, buttonY + 25, 132, 20).build());
    wanderButton = this.addRenderableWidget(
      Button.builder(Component.translatable("playercompanions.command.wander"),
          btn -> sendCommand(CompanionCommand.WANDER))
        .bounds(buttonX, buttonY + 50, 132, 20).build());

    companionEntity = resolveCompanionEntity();
    if (companionEntity instanceof PlayerCompanion companion) {
      CompanionRole role = companion.getCompanionRole();
      if (role == CompanionRole.GUARD) {
        aggressionButton = this.addRenderableWidget(
          Button.builder(aggressionLabel(companion.getAggressionLevel()),
              btn -> cycleAggression())
            .bounds(buttonX, this.topPos + 175, 132, 20).build());
      } else if (role == CompanionRole.COLLECTOR) {
        collectorToggleButton = this.addRenderableWidget(
          Button.builder(collectorLabel(companion.isCollectorActive()),
              btn -> toggleCollector())
            .bounds(buttonX, this.topPos + 175, 132, 20).build());
      }
    }
  }

  private Mob resolveCompanionEntity() {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.level == null || minecraft.player == null) {
      return null;
    }
    return minecraft.level.getEntitiesOfClass(Mob.class,
        minecraft.player.getBoundingBox().inflate(64),
        e -> e instanceof PlayerCompanion && e.getUUID().equals(menu.getCompanionUUID()))
      .stream().findFirst().orElse(null);
  }

  private void sendCommand(CompanionCommand command) {
    CompanionNetworkHandler.sendCompanionCommand(menu.getCompanionUUID(), command);
  }

  private void cycleAggression() {
    if (!(companionEntity instanceof PlayerCompanion companion)) {
      return;
    }
    AggressionLevel next = companion.getAggressionLevel().next(AggressionLevel.GUARD_LEVELS);
    CompanionNetworkHandler.sendCompanionAggression(menu.getCompanionUUID(), next);
    if (aggressionButton != null) {
      aggressionButton.setMessage(aggressionLabel(next));
    }
  }

  private void toggleCollector() {
    if (!(companionEntity instanceof PlayerCompanion companion)) {
      return;
    }
    boolean newActive = !companion.isCollectorActive();
    CompanionNetworkHandler.sendCollectorActive(menu.getCompanionUUID(), newActive);
    if (collectorToggleButton != null) {
      collectorToggleButton.setMessage(collectorLabel(newActive));
    }
  }

  private Component aggressionLabel(AggressionLevel level) {
    return Component.translatable("playercompanions.aggression." + level.name().toLowerCase());
  }

  private Component collectorLabel(boolean active) {
    return Component.translatable(
      active ? "playercompanions.collector.active" : "playercompanions.collector.inactive");
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(guiGraphics);
    this.mousePositionX = mouseX;
    this.mousePositionY = mouseY;
    companionEntity = resolveCompanionEntity();
    super.render(guiGraphics, mouseX, mouseY, partialTick);

    renderInfoPanel(guiGraphics);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    renderWindowBg(guiGraphics, this.leftPos, this.topPos);
    renderCompanionSlots(guiGraphics, this.leftPos, this.topPos);
    renderPlayerSlots(guiGraphics, this.leftPos, this.topPos);

    int previewX = this.leftPos + 24;
    int previewY = this.topPos + 16;
    guiGraphics.fill(previewX, previewY, previewX + 70, previewY + 90, 0xFF000000);

    if (companionEntity != null) {
      int scale = PREVIEW_SCALE;
      int yOffset = getPreviewYOffset();
      if (companionEntity instanceof PlayerCompanion pc) {
        scale = pc.getEntityGuiScaling();
        yOffset = pc.getEntityGuiTop();
      }
      CompanionScreenHelper.renderEntity(
        this.leftPos + PREVIEW_CENTER_X,
        this.topPos + PREVIEW_CENTER_Y + yOffset,
        scale,
        this.leftPos + PREVIEW_CENTER_X - this.mousePositionX,
        this.topPos + 40 - this.mousePositionY,
        companionEntity);
    }
  }

  protected int getPreviewYOffset() {
    return 0;
  }

  private void renderInfoPanel(GuiGraphics guiGraphics) {
    int panelX = this.leftPos + INFO_PANEL_X;
    int panelY = this.topPos + 6;

    guiGraphics.drawString(this.font,
      Component.translatable("playercompanions.screen.info_title"), panelX + 25, panelY,
      0xFFFFFF, false);
    panelY += 15;

    if (companionEntity instanceof PlayerCompanion companion) {
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.role",
          companion.getCompanionRole().name()), panelX, panelY, 0x666666, false);
      panelY += 12;
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.type",
          companion.getCompanionTypeName()), panelX, panelY, 0x666666, false);
      panelY += 12;
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.command",
          companion.getCompanionCommand().name()), panelX, panelY, 0x666666, false);
      panelY += 12;
    }

    if (companionEntity != null) {
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.health",
          (int) companionEntity.getHealth(), (int) companionEntity.getMaxHealth()),
        panelX, panelY, 0x666666, false);
      panelY += 12;
    }

    if (companionEntity instanceof EasyNPC<?> easyNPC
      && easyNPC instanceof ProgressionDataCapable<?> progression) {
      int experienceLevel = progression.getExperienceLevel();
      int experiencePoints = progression.getExperience();
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.level", experienceLevel), panelX, panelY,
        0xFFD700, false);
      panelY += 12;
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.xp", experiencePoints), panelX, panelY,
        0x55FF55, false);
      panelY += 12;

      int barWidth = 132;
      int barHeight = 5;
      float levelProgress = (float) experienceLevel / TamingConfig.MAX_LEVEL;
      int fillWidth = Math.min((int) (barWidth * levelProgress), barWidth);
      guiGraphics.fill(panelX, panelY, panelX + barWidth, panelY + barHeight, 0xFF555555);
      if (fillWidth > 0) {
        guiGraphics.fill(panelX, panelY, panelX + fillWidth, panelY + barHeight, 0xFF55FF55);
      }
    }

    if (companionEntity instanceof PlayerCompanion companion) {
      boolean isSitting = companion.getCompanionCommand() == CompanionCommand.SIT;
      boolean isWandering = companion.getCompanionCommand() == CompanionCommand.WANDER;
      followButton.active = isSitting || isWandering;
      sitButton.active = !isSitting;
      wanderButton.active = !isWandering;
    }

    if (companionEntity instanceof PlayerCompanion companion
      && companion.getCompanionRole() == CompanionRole.GUARD
      && aggressionButton != null) {
      aggressionButton.setMessage(aggressionLabel(companion.getAggressionLevel()));
    }

    if (companionEntity instanceof PlayerCompanion companion
      && companion.getCompanionRole() == CompanionRole.COLLECTOR
      && collectorToggleButton != null) {
      collectorToggleButton.setMessage(collectorLabel(companion.isCollectorActive()));
    }
  }
}
