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

  static void renderWindowBg(GuiGraphics guiGraphics, int originX, int originY) {
    int leftSplitWidth = 169;
    int topSplitHeight = 83;
    int rightSectionWidth = SCREEN_WIDTH - leftSplitWidth;
    int bottomSectionHeight = SCREEN_HEIGHT - topSplitHeight;
    guiGraphics.blit(TEXTURE_DEMO_BG, originX, originY, leftSplitWidth, topSplitHeight, 0, 0,
      leftSplitWidth, topSplitHeight, 256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, originX + leftSplitWidth, originY, rightSectionWidth,
      topSplitHeight, 132, 0, rightSectionWidth, topSplitHeight, 256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, originX, originY + topSplitHeight, leftSplitWidth,
      bottomSectionHeight, 0, 5, leftSplitWidth, bottomSectionHeight, 256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, originX + leftSplitWidth, originY + topSplitHeight,
      rightSectionWidth, bottomSectionHeight, 132, 5, rightSectionWidth, bottomSectionHeight, 256,
      256);
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

    guiGraphics.fill(
      this.leftPos + INFO_PANEL_X, this.topPos + 1,
      this.leftPos + SCREEN_WIDTH - 1, this.topPos + SCREEN_HEIGHT - 1,
      0xFFC6C6C6);

    renderCompanionSlots(guiGraphics, this.leftPos, this.topPos);
    renderPlayerSlots(guiGraphics, this.leftPos, this.topPos);

    int previewX = this.leftPos + 26;
    int previewY = this.topPos + 17;
    guiGraphics.fill(previewX, previewY, previewX + 68, previewY + 68, 0xFF000000);

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
          companion.getCompanionRole().name()), panelX, panelY, 0xAAAAAA, false);
      panelY += 12;
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.type",
          companion.getCompanionTypeName()), panelX, panelY, 0xAAAAAA, false);
      panelY += 12;
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.command",
          companion.getCompanionCommand().name()), panelX, panelY, 0xAAAAAA, false);
      panelY += 12;
    }

    if (companionEntity != null) {
      guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.health",
          (int) companionEntity.getHealth(), (int) companionEntity.getMaxHealth()),
        panelX, panelY, 0xAAAAAA, false);
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

      // XP level progress bar
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
