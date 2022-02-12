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

package de.markusbordihn.playercompanions.client.gui;

import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PlayerCompanionGui extends GuiComponent {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static GuiPosition guiPosition = COMMON.guiPosition.get();
  private static int guiOffsetX = COMMON.guiOffsetX.get();
  private static int guiOffsetY = COMMON.guiOffsetY.get();

  private static final ResourceLocation WIDGETS_TEXTURE =
      new ResourceLocation("minecraft", "textures/gui/widgets.png");
  private static final int TEXT_COLOR_WHITE = ChatFormatting.WHITE.getColor();

  private final PositionManager positionManager = new PositionManager();
  private final Minecraft minecraft;
  private final Font fontRender;
  private final ItemRenderer itemRenderer;
  private final EntityRenderDispatcher entityRenderDispatcher;

  public PlayerCompanionGui(Minecraft minecraft) {
    this.minecraft = minecraft;
    this.fontRender = minecraft.font;
    this.itemRenderer = minecraft.getItemRenderer();
    this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
    positionManager.setInstance(minecraft);
    positionManager.setWidth(81);
    positionManager.setHeight(22);
  }

  @SubscribeEvent()
  public void renderOverlay(RenderGameOverlayEvent.Pre event) {

    if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
      return;
    }

    positionManager.updateWindow();
    positionManager.setPosition(positionManager.getHotbarRight());
    positionManager.setWidth(81);
    positionManager.setHeight(21);
    int x = positionManager.getPositionX();
    int y = positionManager.getPositionY();
    int slots = 4;

    PoseStack poseStack = event.getMatrixStack();

    poseStack.pushPose();
    fontRender.draw(poseStack, "Co", x + slots * 20 + 5, y + 5, TEXT_COLOR_WHITE);

    // Render Background
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();
    this.blit(poseStack, x, y - 1, 0, 0, slots * 20 + 1, 22);

    // Render Items
    Player player = this.getCameraPlayer();
    if (player != null) {
      // this.renderSlot(x + 3, y + 2, 0, player, player.getInventory().items.get(0), 10);
    }

    RenderSystem.disableBlend();
    poseStack.popPose();

    // Render Entity
    poseStack.pushPose();
    // this.renderCustomEntity(player, poseStack, );

    Entity entity = null;
    Set<PlayerCompanionData> companions = PlayerCompanionsClientData.getCompanions();

    if (companions != null && !companions.isEmpty()) {
      PlayerCompanionData playerCompanion = companions.iterator().next();
      entity = this.minecraft.level.getEntity(playerCompanion.getEntityId());
    }
    if (entity != null && entity instanceof LivingEntity livingEntity) {
      InventoryScreen.renderEntityInInventory(x + 11, y + 18, 8, 0F, 0F, livingEntity);
    } else {
      InventoryScreen.renderEntityInInventory(x + 11, y + 18, 8, 0F, 0F, player);
    }
    // log.info("{}", PlayerCompanionsData.get().getCompanions(minecraft.player.getUUID()));
    poseStack.popPose();
  }

  @SubscribeEvent
  public static void handleModConfigReloadEvent(ModConfigEvent.Reloading event) {
    ModConfig config = event.getConfig();
    if (config.getSpec() != CommonConfig.commonSpec) {
      return;
    }
    guiPosition = COMMON.guiPosition.get();
    guiOffsetX = COMMON.guiOffsetX.get();
    guiOffsetY = COMMON.guiOffsetY.get();
  }

  private void renderSlot(int x, int y, float p_168680_, Player player, ItemStack itemStack,
      int p_168683_) {
    if (!itemStack.isEmpty()) {
      PoseStack poseStack = RenderSystem.getModelViewStack();
      float f = (float) itemStack.getPopTime() - p_168680_;
      if (f > 0.0F) {
        float f1 = 1.0F + f / 5.0F;
        poseStack.pushPose();
        poseStack.translate((double) (x + 8), (double) (y + 12), 0.0D);
        poseStack.scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
        poseStack.translate((double) (-(x + 8)), (double) (-(y + 12)), 0.0D);
        RenderSystem.applyModelViewMatrix();
      }

      this.itemRenderer.renderAndDecorateItem(player, itemStack, x, y, p_168683_);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      if (f > 0.0F) {
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
      }

      this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, itemStack, x, y);
    }
  }

  public void renderCustomEntity(Entity entity, PoseStack poseStack, MultiBufferSource buffer,
      int combinedLight) {
    if (entity != null) {
      this.entityRenderDispatcher.render(entity, 0, 0, 0, 1F, 0, poseStack, buffer, combinedLight);
    }
  }

  private Player getCameraPlayer() {
    return !(this.minecraft.getCameraEntity() instanceof Player) ? null
        : (Player) this.minecraft.getCameraEntity();
  }

}
