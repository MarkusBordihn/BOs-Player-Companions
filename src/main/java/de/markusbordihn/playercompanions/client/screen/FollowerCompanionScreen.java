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

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.FollowerCompanionMenu;

@OnlyIn(Dist.CLIENT)
public class FollowerCompanionScreen extends CompanionScreen<FollowerCompanionMenu> {

  private static final ResourceLocation TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/player_companion_follower.png");

  private EditBox skinEdit;

  public FollowerCompanionScreen(FollowerCompanionMenu menu, Inventory inventory,
      Component component) {
    super(menu, inventory, component, TEXTURE);
  }

  @Override
  public void init() {
    super.init();
    this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    this.skinEdit = new EditBox(this.font, this.width / 2 - 100, 116, 200, 20,
        new TranslatableComponent("addServer.enterIp"));
    this.skinEdit.setMaxLength(128);
    this.skinEdit.setFocus(true);
    this.skinEdit.setValue(this.minecraft.options.lastMpIp);
    this.skinEdit.setResponder(consumer -> {
      this.updateSelectButtonStatus();
    });
    this.addWidget(this.skinEdit);
    this.setInitialFocus(this.skinEdit);
  }

  private void updateSelectButtonStatus() {
    log.info("Text Input {}", this.skinEdit.getValue());
  }

  @Override
  public boolean keyPressed(int keyCode, int p_95965_, int p_95966_) {
    if (this.getFocused() != this.skinEdit || keyCode == 256 || keyCode == 257 || keyCode == 335) {
      return super.keyPressed(keyCode, p_95965_, p_95966_);
    } else {
      return true;
    }
  }

  @Override
  public void removed() {
    this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    super.removed();
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    super.render(poseStack, x, y, partialTicks);
    this.skinEdit.render(poseStack, x, y, partialTicks);
  }

}
