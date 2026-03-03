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

package de.markusbordihn.playercompanions.menu;

import de.markusbordihn.playercompanions.commands.CompanionSpawnCooldown;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class CompanionShrineMenu extends AbstractContainerMenu {

  private final List<CompanionShrineEntry> entries;
  private final Player player;
  private final SimpleContainerData remainingSecondsData;

  public CompanionShrineMenu(int windowId, Inventory playerInventory, ServerPlayer serverPlayer) {
    super(ModMenuTypes.COMPANION_SHRINE, windowId);
    this.player = playerInventory.player;
    this.entries = Collections.unmodifiableList(
      CompanionShrineHandler.loadEntriesForPlayer(serverPlayer));
    this.remainingSecondsData = new SimpleContainerData(entries.size());
    addDataSlots(remainingSecondsData);
  }

  public CompanionShrineMenu(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
    super(ModMenuTypes.COMPANION_SHRINE, windowId);
    this.player = playerInventory.player;
    int count = buf.readInt();
    List<CompanionShrineEntry> list = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      list.add(CompanionShrineEntry.decode(buf));
    }
    this.entries = Collections.unmodifiableList(list);
    this.remainingSecondsData = new SimpleContainerData(entries.size());
    addDataSlots(remainingSecondsData);
  }

  @Override
  public void broadcastChanges() {
    if (!player.level().isClientSide && player.level() instanceof ServerLevel serverLevel) {
      long gameTime = serverLevel.getGameTime();
      for (int i = 0; i < entries.size(); i++) {
        remainingSecondsData.set(i,
          (int) (CompanionSpawnCooldown.remainingTicks(entries.get(i).uuid(), gameTime) / 20L));
      }
    }
    super.broadcastChanges();
  }

  public List<CompanionShrineEntry> getEntries() {
    return entries;
  }

  public int getRemainingSeconds(int index) {
    if (index < 0 || index >= remainingSecondsData.getCount()) {
      return 0;
    }
    return remainingSecondsData.get(index);
  }

  public int getPlayerExperienceLevel() {
    return player.experienceLevel;
  }

  @Override
  public boolean stillValid(Player player) {
    return player.isAlive();
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    return ItemStack.EMPTY;
  }
}
