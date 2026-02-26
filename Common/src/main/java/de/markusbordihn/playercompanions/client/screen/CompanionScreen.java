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

import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.CompanionRole;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.menu.CompanionMenu;
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

  static final int W = 350;
  static final int H = 230;

  private static final int PREVIEW_CENTER_X = 59;
  private static final int PREVIEW_CENTER_Y = 70;
  private static final int PREVIEW_SCALE = 40;
  private static final int INFO_X = 204;

  private float xMouse;
  private float yMouse;

  protected Mob companionEntity;
  private Button followButton;
  private Button sitButton;

  public CompanionScreen(T menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.imageWidth = W;
    this.imageHeight = H;
  }

  @Override
  public void init() {
    super.init();
    this.imageWidth = W;
    this.imageHeight = H;
    this.titleLabelX = 6;
    this.titleLabelY = 6;
    this.topPos = (this.height - H) / 2;
    this.leftPos = (this.width - W) / 2;
    this.inventoryLabelX = 6;
    this.inventoryLabelY = H - 91;
    companionEntity = resolveCompanionEntity();

    int btnX = this.leftPos + INFO_X;
    int btnY = this.topPos + 100;
    followButton = this.addRenderableWidget(
        Button.builder(Component.translatable("playercompanions.command.follow"),
            btn -> sendCommand(CompanionCommand.FOLLOW))
            .bounds(btnX, btnY, 132, 20).build());
    sitButton = this.addRenderableWidget(
        Button.builder(Component.translatable("playercompanions.command.sit"),
            btn -> sendCommand(CompanionCommand.SIT))
            .bounds(btnX, btnY + 25, 132, 20).build());
  }

  private Mob resolveCompanionEntity() {
    Minecraft mc = Minecraft.getInstance();
    if (mc.level == null) return null;
    return mc.level.getEntitiesOfClass(Mob.class,
        mc.player.getBoundingBox().inflate(64),
        e -> e instanceof PlayerCompanion && e.getUUID().equals(menu.getCompanionUUID()))
        .stream().findFirst().orElse(null);
  }

  private void sendCommand(CompanionCommand command) {
    // TODO: send network packet to server
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    this.renderBackground(guiGraphics);
    this.xMouse = mouseX;
    this.yMouse = mouseY;
    companionEntity = resolveCompanionEntity();

    super.render(guiGraphics, mouseX, mouseY, partialTick);

    renderInfoPanel(guiGraphics);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    renderWindowBg(guiGraphics, this.leftPos, this.topPos, W, H);

    guiGraphics.fill(
        this.leftPos + INFO_X, this.topPos + 1,
        this.leftPos + W - 1, this.topPos + H - 1,
        0xFFC6C6C6);

    renderCompanionSlots(guiGraphics, this.leftPos, this.topPos);
    renderPlayerSlots(guiGraphics, this.leftPos, this.topPos);

    int previewX = this.leftPos + 26;
    int previewY = this.topPos + 17;
    guiGraphics.fill(previewX, previewY, previewX + 68, previewY + 68, 0xFF000000);

    if (companionEntity != null) {
      CompanionScreenHelper.renderEntity(
          this.leftPos + PREVIEW_CENTER_X,
          this.topPos + PREVIEW_CENTER_Y + getPreviewYOffset(companionEntity),
          PREVIEW_SCALE,
          this.leftPos + PREVIEW_CENTER_X - this.xMouse,
          this.topPos + 40 - this.yMouse,
          companionEntity);
    }
  }

  protected int getPreviewYOffset(Mob entity) {
    return 0;
  }

  private void renderInfoPanel(GuiGraphics guiGraphics) {
    int x = this.leftPos + INFO_X;
    int y = this.topPos + 6;

    guiGraphics.drawString(this.font,
        Component.translatable("playercompanions.screen.info_title"), x + 25, y,
        0xFFFFFF, false);
    y += 15;

    if (companionEntity instanceof PlayerCompanion companion) {
      guiGraphics.drawString(this.font,
          Component.literal("Role: " + companion.getCompanionRole().name()), x, y,
          0xAAAAAA, false);
      y += 12;
      guiGraphics.drawString(this.font,
          Component.literal("Type: " + companion.getCompanionTypeName()), x, y,
          0xAAAAAA, false);
      y += 12;
      guiGraphics.drawString(this.font,
          Component.literal("Cmd: " + companion.getCompanionCommand().name()), x, y,
          0xAAAAAA, false);
      y += 12;
    }

    if (companionEntity != null) {
      int hp = (int) companionEntity.getHealth();
      int maxHp = (int) companionEntity.getMaxHealth();
      guiGraphics.drawString(this.font,
          Component.literal("HP: " + hp + " / " + maxHp), x, y, 0xAAAAAA, false);
    }

    if (companionEntity instanceof PlayerCompanion companion) {
      boolean isSitting = companion.getCompanionCommand() == CompanionCommand.SIT;
      followButton.active = isSitting;
      sitButton.active = !isSitting;
    }

    if (companionEntity instanceof PlayerCompanion companion
        && companion.getCompanionRole() == CompanionRole.GUARD) {
      int y2 = this.topPos + 155;
      guiGraphics.drawString(this.font,
          Component.translatable("playercompanions.screen.aggression"), x, y2, 0xAAAAAA, false);
    }
  }

  static void renderWindowBg(GuiGraphics guiGraphics, int x, int y, int w, int h) {
    int splitX = 169;
    int splitY = 83;
    int rightW = w - splitX;
    int bottomH = h - splitY;
    guiGraphics.blit(TEXTURE_DEMO_BG, x,          y,          splitX, splitY,  0,   0,   splitX, splitY,  256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, x + splitX, y,          rightW, splitY,  132, 0,   rightW, splitY,  256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, x,          y + splitY, splitX, bottomH, 0,   5,   splitX, bottomH, 256, 256);
    guiGraphics.blit(TEXTURE_DEMO_BG, x + splitX, y + splitY, rightW, bottomH, 132, 5,   rightW, bottomH, 256, 256);
  }

  static void renderCompanionSlots(GuiGraphics guiGraphics, int x, int y) {
    for (int i = 0; i < 4; i++) {
      guiGraphics.blit(TEXTURE_INVENTORY, x + 5,  y + 16 + i * 18, 18, 18, 7, 7, 18, 18, 256, 256);
      guiGraphics.blit(TEXTURE_INVENTORY, x + 95, y + 16 + i * 18, 18, 18, 7, 7, 18, 18, 256, 256);
    }
    guiGraphics.blit(TEXTURE_INVENTORY, x + 5,  y + 88, 18, 18, 7, 7, 18, 18, 256, 256);
    guiGraphics.blit(TEXTURE_INVENTORY, x + 95, y + 88, 18, 18, 7, 7, 18, 18, 256, 256);
  }

  static void renderPlayerSlots(GuiGraphics guiGraphics, int x, int y) {
    guiGraphics.blit(TEXTURE_INVENTORY, x + 5, y + 150, 162, 54, 7, 83, 162, 54, 256, 256);
    guiGraphics.blit(TEXTURE_INVENTORY, x + 5, y + 208, 162, 18, 7, 141, 162, 18, 256, 256);
  }
}
