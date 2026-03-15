/*
 * Copyright 2026 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.playercompanions.client.screen;

import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.menu.CompanionShrineEntry;
import de.markusbordihn.playercompanions.menu.CompanionShrineMenu;
import de.markusbordihn.playercompanions.network.CompanionNetworkHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CompanionShrineScreen extends AbstractContainerScreen<CompanionShrineMenu> {

  private static final int IMAGE_WIDTH = 320;
  private static final int IMAGE_HEIGHT = 220;
  private static final int ENTRIES_PER_PAGE = 5;
  private static final int ROW_HEIGHT = 34;
  private static final int ENTRIES_START_Y = 26;

  private static final int BG_OUTER = 0xFF1A1A2E;
  private static final int BG_PANEL = 0xFF16213E;
  private static final int BG_ROW_EVEN = 0xFF0F3460;
  private static final int BG_ROW_ODD = 0xFF0D2A50;
  private static final int COLOR_TITLE = 0xFFE2B96F;
  private static final int COLOR_NAME = 0xFFFFFFFF;
  private static final int COLOR_TYPE = 0xFFAAAAAA;
  private static final int COLOR_READY = 0xFF55FF55;
  private static final int COLOR_TIMER = 0xFFFFAA00;
  private static final int COLOR_EMPTY = 0xFF88AACC;

  private static final int BUTTON_UPDATE_INTERVAL_TICKS = 20;
  private final List<EntryButtons> pageEntryButtons = new ArrayList<>();
  private Button prevPageButton;
  private Button nextPageButton;
  private int currentPage = 0;
  private long lastButtonUpdateTick = -1;

  public CompanionShrineScreen(
    CompanionShrineMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
  }

  @Override
  public void init() {
    super.init();
    this.imageWidth = IMAGE_WIDTH;
    this.imageHeight = IMAGE_HEIGHT;
    this.topPos = (this.height - IMAGE_HEIGHT) / 2;
    this.leftPos = (this.width - IMAGE_WIDTH) / 2;
    this.titleLabelX = IMAGE_WIDTH / 2;
    this.titleLabelY = 6;
    this.inventoryLabelY = IMAGE_HEIGHT + 10;

    buildPageButtons();
  }

  private void buildPageButtons() {
    pageEntryButtons.forEach(eb -> {
      if (eb.speedUpButton() != null) {
        removeWidget(eb.speedUpButton());
      }
      removeWidget(eb.respawnButton());
    });
    pageEntryButtons.clear();
    if (prevPageButton != null) {
      removeWidget(prevPageButton);
      prevPageButton = null;
    }
    if (nextPageButton != null) {
      removeWidget(nextPageButton);
      nextPageButton = null;
    }

    List<CompanionShrineEntry> entries = menu.getEntries();
    int totalPages = Math.max(1, (entries.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE);
    currentPage = Math.min(currentPage, totalPages - 1);

    int pageStart = currentPage * ENTRIES_PER_PAGE;
    int pageEnd = Math.min(pageStart + ENTRIES_PER_PAGE, entries.size());

    for (int i = pageStart; i < pageEnd; i++) {
      CompanionShrineEntry entry = entries.get(i);
      int buttonY = topPos + ENTRIES_START_Y + (i - pageStart) * ROW_HEIGHT + (ROW_HEIGHT - 18) / 2;
      int remaining = menu.getRemainingSeconds(i);

      Button speedUpButton = addRenderableWidget(
        Button.builder(
            Component.translatable("playercompanions.shrine.xp_reduce"),
            btn -> CompanionNetworkHandler.sendShrineXpReduce(entry.uuid()))
          .bounds(leftPos + 190, buttonY, 60, 18)
          .tooltip(
            Tooltip.create(Component.translatable("playercompanions.shrine.xp_reduce.tooltip")))
          .build());
      speedUpButton.visible = remaining > 0;
      speedUpButton.active = remaining > 0 && menu.getPlayerExperienceLevel() >= 1;

      Button respawnButton = addRenderableWidget(
        Button.builder(
            Component.translatable("playercompanions.shrine.respawn"),
            btn -> CompanionNetworkHandler.sendShrineRespawn(entry.uuid()))
          .bounds(leftPos + 254, buttonY, 60, 18)
          .build());
      respawnButton.active = remaining == 0;

      pageEntryButtons.add(new EntryButtons(speedUpButton, respawnButton));
    }

    if (totalPages > 1) {
      int navY = topPos + ENTRIES_START_Y + ENTRIES_PER_PAGE * ROW_HEIGHT + 4;
      prevPageButton = addRenderableWidget(
        Button.builder(
            Component.translatable("playercompanions.shrine.page.previous"),
            btn -> {
              currentPage = Math.max(0, currentPage - 1);
              buildPageButtons();
            })
          .bounds(leftPos + 5, navY, 50, 16)
          .build());
      nextPageButton = addRenderableWidget(
        Button.builder(
            Component.translatable("playercompanions.shrine.page.next"),
            btn -> {
              currentPage = Math.min(totalPages - 1, currentPage + 1);
              buildPageButtons();
            })
          .bounds(leftPos + 265, navY, 50, 16)
          .build());
    }

    updateButtonStates();
  }

  private void updateButtonStates() {
    List<CompanionShrineEntry> entries = menu.getEntries();
    int pageStart = currentPage * ENTRIES_PER_PAGE;
    int playerLevel = menu.getPlayerExperienceLevel();

    for (int j = 0; j < pageEntryButtons.size(); j++) {
      int remaining = menu.getRemainingSeconds(pageStart + j);
      EntryButtons eb = pageEntryButtons.get(j);
      eb.speedUpButton().visible = remaining > 0;
      eb.speedUpButton().active = remaining > 0 && playerLevel >= 1;
      eb.respawnButton().active = remaining == 0;
    }

    int totalPages = Math.max(1, (entries.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE);
    if (prevPageButton != null) {
      prevPageButton.active = currentPage > 0;
    }
    if (nextPageButton != null) {
      nextPageButton.active = currentPage < totalPages - 1;
    }
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    int x = leftPos;
    int y = topPos;

    guiGraphics.fill(x, y, x + IMAGE_WIDTH, y + IMAGE_HEIGHT, BG_OUTER);
    guiGraphics.fill(x + 2, y + 2, x + IMAGE_WIDTH - 2, y + IMAGE_HEIGHT - 2, BG_PANEL);
    guiGraphics.fill(x + 4, y + 22, x + IMAGE_WIDTH - 4, y + 23, COLOR_TITLE);

    List<CompanionShrineEntry> entries = menu.getEntries();
    if (entries.isEmpty()) {
      return;
    }
    int pageStart = currentPage * ENTRIES_PER_PAGE;
    int pageEnd = Math.min(pageStart + ENTRIES_PER_PAGE, entries.size());
    for (int i = pageStart; i < pageEnd; i++) {
      int rowIndex = i - pageStart;
      int rowY = y + ENTRIES_START_Y + rowIndex * ROW_HEIGHT;
      guiGraphics.fill(x + 4, rowY + 1, x + IMAGE_WIDTH - 4, rowY + ROW_HEIGHT - 1,
        rowIndex % 2 == 0 ? BG_ROW_EVEN : BG_ROW_ODD);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawCenteredString(
      font,
      Component.translatable("playercompanions.shrine.title"),
      IMAGE_WIDTH / 2,
      7,
      COLOR_TITLE);

    List<CompanionShrineEntry> entries = menu.getEntries();

    if (entries.isEmpty()) {
      guiGraphics.drawCenteredString(
        font,
        Component.translatable("playercompanions.shrine.empty"),
        IMAGE_WIDTH / 2,
        IMAGE_HEIGHT / 2 - 4,
        COLOR_EMPTY);
      renderCompanionCount(guiGraphics);
      return;
    }

    int pageStart = currentPage * ENTRIES_PER_PAGE;
    int pageEnd = Math.min(pageStart + ENTRIES_PER_PAGE, entries.size());

    for (int i = pageStart; i < pageEnd; i++) {
      CompanionShrineEntry entry = entries.get(i);
      int rowY = ENTRIES_START_Y + (i - pageStart) * ROW_HEIGHT;
      int remaining = menu.getRemainingSeconds(i);

      guiGraphics.drawString(font, entry.displayName(), 8, rowY + 4, COLOR_NAME, true);
      guiGraphics.drawString(font, "[" + entry.readableType() + "]", 8, rowY + 16, COLOR_TYPE);

      if (remaining <= 0) {
        guiGraphics.drawString(font, Component.translatable("playercompanions.shrine.ready"), 140,
          rowY + 13, COLOR_READY);
      } else {
        guiGraphics.drawString(font,
          String.format("%d:%02d", remaining / 60, remaining % 60),
          140, rowY + 13, COLOR_TIMER);
      }
    }

    int totalPages = Math.max(1, (entries.size() + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE);
    if (totalPages > 1) {
      guiGraphics.drawCenteredString(
        font, Component.translatable("playercompanions.shrine.page.indicator", currentPage + 1,
          totalPages),
        IMAGE_WIDTH / 2, ENTRIES_START_Y + ENTRIES_PER_PAGE * ROW_HEIGHT + 4,
        COLOR_TYPE);
    }

    renderCompanionCount(guiGraphics);
  }

  private void renderCompanionCount(GuiGraphics guiGraphics) {
    int companionLimit = TamingConfig.COMPANION_LIMIT_PER_PLAYER;
    int totalCount = menu.getTotalCompanionCount();
    Component countText = companionLimit > 0
      ? Component.translatable("playercompanions.shrine.companion_count_limited", totalCount,
      companionLimit)
      : Component.translatable("playercompanions.shrine.companion_count", totalCount);
    guiGraphics.drawCenteredString(
      font, countText,
      IMAGE_WIDTH / 2, ENTRIES_START_Y + ENTRIES_PER_PAGE * ROW_HEIGHT + 13,
      COLOR_TYPE);
  }

  @Override
  public void render(
    @NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    if (this.minecraft != null && this.minecraft.level != null) {
      long currentTick = this.minecraft.level.getGameTime();
      if (currentTick - lastButtonUpdateTick >= BUTTON_UPDATE_INTERVAL_TICKS) {
        updateButtonStates();
        lastButtonUpdateTick = currentTick;
      }
    }
    renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTick);
  }

  private record EntryButtons(Button speedUpButton, Button respawnButton) {

  }
}
