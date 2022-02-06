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

package de.markusbordihn.playercompanions.network.message;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import de.markusbordihn.playercompanions.container.CompanionsMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class MessageOpenCompanionsMenu {

  public MessageOpenCompanionsMenu() {}

  public static void handle(MessageOpenCompanionsMenu message,
      Supplier<NetworkEvent.Context> supplier) {
    NetworkEvent.Context context = supplier.get();
    context.enqueueWork(() -> {
      ServerPlayer player = context.getSender();
      if (player != null) {
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return new TranslatableComponent("container.player_companions.companions_menu");
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new CompanionsMenu(windowId, inventory);
          }
        };
        NetworkHooks.openGui(player, provider);
      }
    });
    context.setPacketHandled(true);
  }
}
