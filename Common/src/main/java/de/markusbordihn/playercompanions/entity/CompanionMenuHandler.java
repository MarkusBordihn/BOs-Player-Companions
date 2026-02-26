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

package de.markusbordihn.playercompanions.entity;

import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.menu.CompanionCollectorMenu;
import de.markusbordihn.playercompanions.menu.CompanionDefaultMenu;
import de.markusbordihn.playercompanions.menu.CompanionFollowerMenu;
import de.markusbordihn.playercompanions.menu.CompanionGuardMenu;
import de.markusbordihn.playercompanions.menu.IMenuOpener;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionMenuHandler {

  private static final Logger log = LogManager.getLogger(CompanionMenuHandler.class);
  private static IMenuOpener menuOpener;

  private CompanionMenuHandler() {}

  public static void setMenuOpener(IMenuOpener opener) {
    menuOpener = opener;
  }

  public static void openMenu(PlayerCompanion companion, ServerPlayer player) {
    if (menuOpener == null) {
      log.error("No IMenuOpener registered — cannot open companion menu");
      return;
    }
    UUID companionUUID = companion.asEntity().getUUID();
    Component displayName = companion.asEntity().getCustomName() != null
        ? companion.asEntity().getCustomName()
        : companion.asEntity().getName();

    MenuProvider menuProvider = buildMenuProvider(companion, companionUUID, displayName);
    menuOpener.openMenu(player, menuProvider, companionUUID);
  }

  private static MenuProvider buildMenuProvider(PlayerCompanion companion, UUID companionUUID,
      Component displayName) {
    return new MenuProvider() {
      @Override
      public Component getDisplayName() {
        return displayName;
      }

      @Override
      public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
          net.minecraft.world.entity.player.Player player) {
        return switch (companion.getCompanionRole()) {
          case COLLECTOR -> new CompanionCollectorMenu(windowId, inventory, companionUUID);
          case GUARD -> new CompanionGuardMenu(windowId, inventory, companionUUID);
          case FOLLOWER -> new CompanionFollowerMenu(windowId, inventory, companionUUID);
          default -> new CompanionDefaultMenu(windowId, inventory, companionUUID);
        };
      }
    };
  }
}
