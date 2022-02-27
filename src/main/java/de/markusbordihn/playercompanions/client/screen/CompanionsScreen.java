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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.CompanionsMenu;

@OnlyIn(Dist.CLIENT)
public class CompanionsScreen extends AbstractContainerScreen<CompanionsMenu> {

  private static final ResourceLocation TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/container/player_companions.png");

  private final Entity entity;

  public CompanionsScreen(CompanionsMenu menu, Inventory inventory, Component component) {
    super(menu, inventory, component);
    this.entity = menu.getPlayerCompanionEntity();
  }

  @Override
  public void init() {
    super.init();
    this.imageHeight = 210;
    this.titleLabelX = 7;
    this.titleLabelY = 7;
    this.topPos = (this.height - this.imageHeight) / 2;
    this.inventoryLabelY = this.imageHeight - 92;
  }

  @Override
  public void render(PoseStack poseStack, int x, int y, float partialTicks) {
    this.renderBackground(poseStack);
    super.render(poseStack, x, y, partialTicks);
    if (entity instanceof LivingEntity livingEntity) {
      InventoryScreen.renderEntityInInventory(
          this.leftPos + 50,
          this.topPos + 87, 30, 0F, 0F,
          livingEntity);
    }
  }

  @Override
  protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, TEXTURE);

    // Cut main screen
    this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
  }

}
