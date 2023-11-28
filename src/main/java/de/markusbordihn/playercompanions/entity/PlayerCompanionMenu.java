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


package de.markusbordihn.playercompanions.entity;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.CollectorCompanionMenu;
import de.markusbordihn.playercompanions.container.DefaultCompanionMenu;
import de.markusbordihn.playercompanions.container.FollowerCompanionMenu;
import de.markusbordihn.playercompanions.container.GuardCompanionMenu;
import de.markusbordihn.playercompanions.container.HealerCompanionMenu;
import de.markusbordihn.playercompanions.container.SupporterCompanionMenu;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerCompanionMenu {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected PlayerCompanionMenu() {
  }

  public static void openMenu(PlayerCompanionEntity playerCompanionEntity) {
    switch (playerCompanionEntity.getCompanionType()) {
      case COLLECTOR:
        openCollectorMenu(playerCompanionEntity);
        break;
      case FOLLOWER:
        openFollowerMenu(playerCompanionEntity);
        break;
      case GUARD:
        openGuardMenu(playerCompanionEntity);
        break;
      case HEALER:
        openHealerMenu(playerCompanionEntity);
        break;
      case SUPPORTER:
        openSupporterMenu(playerCompanionEntity);
        break;
      default:
        openDefaultMenu(playerCompanionEntity);
    }
  }

  public static void openDefaultMenu(PlayerCompanionEntity playerCompanionEntity) {
    log.debug("Open Default Player Companion Menu for {} ...", playerCompanionEntity);
    LivingEntity owner = playerCompanionEntity.getOwner();
    MinecraftServer minecraftServer = playerCompanionEntity.level().getServer();
    if (owner != null && minecraftServer != null && PlayerCompanionsServerData.available()) {
      ServerPlayer player = minecraftServer.getPlayerList().getPlayer(owner.getUUID());
      if (player instanceof ServerPlayer) {
        UUID playerCompanionUUID = playerCompanionEntity.getUUID();
        Component playerCompanionName = playerCompanionEntity.getCustomName();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return playerCompanionName;
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new DefaultCompanionMenu(windowId, inventory, playerCompanionUUID);
          }
        };
        NetworkHooks.openScreen(player, provider, buffer -> buffer.writeUUID(playerCompanionUUID));
      }
    }
  }

  public static void openCollectorMenu(PlayerCompanionEntity playerCompanionEntity) {
    log.debug("Open Collector Player Companion Menu for {} ...", playerCompanionEntity);
    LivingEntity owner = playerCompanionEntity.getOwner();
    MinecraftServer minecraftServer = playerCompanionEntity.level().getServer();
    if (owner != null && minecraftServer != null && PlayerCompanionsServerData.available()) {
      ServerPlayer player = minecraftServer.getPlayerList().getPlayer(owner.getUUID());
      if (player instanceof ServerPlayer) {
        UUID playerCompanionUUID = playerCompanionEntity.getUUID();
        Component playerCompanionName = playerCompanionEntity.getCustomName();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return playerCompanionName;
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new CollectorCompanionMenu(windowId, inventory, playerCompanionUUID);
          }
        };
        NetworkHooks.openScreen(player, provider, buffer -> buffer.writeUUID(playerCompanionUUID));
      }
    }
  }

  public static void openFollowerMenu(PlayerCompanionEntity playerCompanionEntity) {
    log.debug("Open Follower Player Companion Menu for {} ...", playerCompanionEntity);
    LivingEntity owner = playerCompanionEntity.getOwner();
    MinecraftServer minecraftServer = playerCompanionEntity.level().getServer();
    if (owner != null && minecraftServer != null && PlayerCompanionsServerData.available()) {
      ServerPlayer player = minecraftServer.getPlayerList().getPlayer(owner.getUUID());
      if (player instanceof ServerPlayer) {
        UUID playerCompanionUUID = playerCompanionEntity.getUUID();
        Component playerCompanionName = playerCompanionEntity.getCustomName();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return playerCompanionName;
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new FollowerCompanionMenu(windowId, inventory, playerCompanionUUID);
          }
        };
        NetworkHooks.openScreen(player, provider, buffer -> buffer.writeUUID(playerCompanionUUID));
      }
    }
  }

  public static void openGuardMenu(PlayerCompanionEntity playerCompanionEntity) {
    log.debug("Open Guard Player Companion Menu for {} ...", playerCompanionEntity);
    LivingEntity owner = playerCompanionEntity.getOwner();
    MinecraftServer minecraftServer = playerCompanionEntity.level().getServer();
    if (owner != null && minecraftServer != null && PlayerCompanionsServerData.available()) {
      ServerPlayer player = minecraftServer.getPlayerList().getPlayer(owner.getUUID());
      if (player instanceof ServerPlayer) {
        UUID playerCompanionUUID = playerCompanionEntity.getUUID();
        Component playerCompanionName = playerCompanionEntity.getCustomName();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return playerCompanionName;
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new GuardCompanionMenu(windowId, inventory, playerCompanionUUID);
          }
        };
        NetworkHooks.openScreen(player, provider, buffer -> buffer.writeUUID(playerCompanionUUID));
      }
    }
  }

  public static void openHealerMenu(PlayerCompanionEntity playerCompanionEntity) {
    log.debug("Open Healer Player Companion Menu for {} ...", playerCompanionEntity);
    LivingEntity owner = playerCompanionEntity.getOwner();
    MinecraftServer minecraftServer = playerCompanionEntity.level().getServer();
    if (owner != null && minecraftServer != null && PlayerCompanionsServerData.available()) {
      ServerPlayer player = minecraftServer.getPlayerList().getPlayer(owner.getUUID());
      if (player instanceof ServerPlayer) {
        UUID playerCompanionUUID = playerCompanionEntity.getUUID();
        Component playerCompanionName = playerCompanionEntity.getCustomName();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return playerCompanionName;
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new HealerCompanionMenu(windowId, inventory, playerCompanionUUID);
          }
        };
        NetworkHooks.openScreen(player, provider, buffer -> buffer.writeUUID(playerCompanionUUID));
      }
    }
  }

  public static void openSupporterMenu(PlayerCompanionEntity playerCompanionEntity) {
    log.debug("Open Supporter Player Companion Menu for {} ...", playerCompanionEntity);
    LivingEntity owner = playerCompanionEntity.getOwner();
    MinecraftServer minecraftServer = playerCompanionEntity.level().getServer();
    if (owner != null && minecraftServer != null && PlayerCompanionsServerData.available()) {
      ServerPlayer player = minecraftServer.getPlayerList().getPlayer(owner.getUUID());
      if (player instanceof ServerPlayer) {
        UUID playerCompanionUUID = playerCompanionEntity.getUUID();
        Component playerCompanionName = playerCompanionEntity.getCustomName();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return playerCompanionName;
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new SupporterCompanionMenu(windowId, inventory, playerCompanionUUID);
          }
        };
        NetworkHooks.openScreen(player, provider, buffer -> buffer.writeUUID(playerCompanionUUID));
      }
    }
  }

}
