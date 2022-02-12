/**
 * Copyright 2021 Markus Bordihn
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

package de.markusbordihn.playercompanions.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.client.model.SmallGhastModel;
import de.markusbordihn.playercompanions.entity.guard.SmallGhast;

@OnlyIn(Dist.CLIENT)
public class SmallGhastRenderer extends MobRenderer<SmallGhast, SmallGhastModel<SmallGhast>> {

  private static final ResourceLocation GHAST_LOCATION =
      new ResourceLocation("textures/entity/ghast/ghast.png");
  private static final ResourceLocation GHAST_SHOOTING_LOCATION =
      new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

  public SmallGhastRenderer(EntityRendererProvider.Context context) {
    super(context, new SmallGhastModel<>(context.bakeLayer(ClientRenderer.SMALL_GHAST)), 0.4F);
  }

  public ResourceLocation getTextureLocation(SmallGhast entity) {
    return entity.isCharging() ? GHAST_SHOOTING_LOCATION : GHAST_LOCATION;
  }

  @Override
  public void render(SmallGhast entity, float p_115977_, float p_115978_, PoseStack poseStack,
      MultiBufferSource buffer, int light) {
    //poseStack.translate(0d, 0.75d, 0d);
    super.render(entity, p_115977_, p_115978_, poseStack, buffer, light);
  }

  @Override
  protected void scale(SmallGhast entity, PoseStack poseStack, float unused) {
    poseStack.scale(0.75F, 0.75F, 0.75F);
  }
}
