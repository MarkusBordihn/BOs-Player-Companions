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

import de.markusbordihn.easynpc.data.npc.SavedNPCEntityEntry;
import de.markusbordihn.easynpc.data.saveddata.NPCEntityData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionShrineHandler {

  public static final int MAX_ENTRIES = 10;
  public static final String SHRINE_TITLE_KEY = "playercompanions.shrine.title";

  private static final Logger log = LogManager.getLogger(CompanionShrineHandler.class);
  private static CompanionShrineOpener shrineOpener;

  private CompanionShrineHandler() {
  }

  public static void setOpener(CompanionShrineOpener opener) {
    log.info("Registering companion shrine opener: {}", opener.getClass().getSimpleName());
    shrineOpener = opener;
  }

  public static void openMenu(ServerPlayer player) {
    if (shrineOpener == null) {
      log.error("No CompanionShrineOpener registered — cannot open shrine menu");
      return;
    }
    shrineOpener.openShrineMenu(player, new MenuProvider() {
      @Override
      public Component getDisplayName() {
        return Component.translatable(SHRINE_TITLE_KEY);
      }

      @Override
      public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player p) {
        return new CompanionShrineMenu(windowId, inv, (ServerPlayer) p);
      }
    });
  }

  public static void writeShrineData(ServerPlayer player, FriendlyByteBuf buf) {
    List<CompanionShrineEntry> entries = loadEntriesForPlayer(player);
    buf.writeInt(entries.size());
    entries.forEach(e -> e.encode(buf));
  }

  public static List<CompanionShrineEntry> loadEntriesForPlayer(ServerPlayer player) {
    List<CompanionShrineEntry> result = new ArrayList<>();
    for (SavedNPCEntityEntry entry :
      NPCEntityData.get(player.getServer()).getEntriesByOwner(player.getUUID())) {
      if (!entry.metadata().hasRemovalReason()) {
        continue;
      }
      result.add(toEntry(entry));
      if (result.size() >= MAX_ENTRIES) {
        break;
      }
    }
    return result;
  }

  private static CompanionShrineEntry toEntry(SavedNPCEntityEntry entry) {
    UUID uuid = entry.entityUUID();
    String displayName = uuid.toString();
    if (entry.npcData() != null && entry.npcData().contains("CustomName")) {
      try {
        String raw = entry.npcData().getString("CustomName");
        if (!raw.isEmpty()) {
          Component parsed = Component.Serializer.fromJson(raw);
          displayName = parsed != null ? parsed.getString() : raw;
        }
      } catch (Exception ignored) {
      }
    }
    String entityType =
      entry.metadata().hasEntityType() ? entry.metadata().entityType() : "unknown";
    if (entityType.contains(":")) {
      entityType = entityType.substring(entityType.indexOf(':') + 1);
    }
    return new CompanionShrineEntry(uuid, displayName, entityType);
  }
}
