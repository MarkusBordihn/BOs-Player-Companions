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

package de.markusbordihn.playercompanions.client.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.FollowerCompanionMenu;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

@OnlyIn(Dist.CLIENT)
public class FollowerCompanionScreen extends CompanionScreen<FollowerCompanionMenu> {

  private static final ResourceLocation TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/player_companion_follower.png");

  public FollowerCompanionScreen(FollowerCompanionMenu menu, Inventory inventory,
      Component component) {
    super(menu, inventory, component, TEXTURE);
  }

  @Override
  public void init() {
    super.init();

    if (entity instanceof PlayerCompanionEntity playerCompanionEntity
        && playerCompanionEntity.enableCustomTextureSkin()) {
      this.addRenderableWidget(new Button(this.leftPos + 5, this.topPos + 107, 108, 20,
          new TranslatableComponent("Texture Settings"), event -> {
            this.showTextureSettings(true);
            this.setFocused(null);
          }));
    }
  }

}
