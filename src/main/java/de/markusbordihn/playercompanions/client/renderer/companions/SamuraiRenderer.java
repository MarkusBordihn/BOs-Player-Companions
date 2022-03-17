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

package de.markusbordihn.playercompanions.client.renderer.companions;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.client.model.SamuraiModel;
import de.markusbordihn.playercompanions.client.renderer.ClientRenderer;
import de.markusbordihn.playercompanions.entity.companions.Samurai;

@OnlyIn(Dist.CLIENT)
public class SamuraiRenderer extends HumanoidMobRenderer<Samurai, SamuraiModel<Samurai>> {

  public SamuraiRenderer(EntityRendererProvider.Context context) {
    super(context, new SamuraiModel<>(context.bakeLayer(ClientRenderer.SAMURAI)), 0.3F);
  }

  @Override
  protected int getBlockLightLevel(Samurai entity, BlockPos blockPos) {
    return 15;
  }

  @Override
  protected void scale(Samurai entity, PoseStack poseStack, float unused) {
    poseStack.scale(0.7F, 0.7F, 0.7F);
  }

  @Override
  public ResourceLocation getTextureLocation(Samurai entity) {
    return entity.getResourceLocation();
  }

}
