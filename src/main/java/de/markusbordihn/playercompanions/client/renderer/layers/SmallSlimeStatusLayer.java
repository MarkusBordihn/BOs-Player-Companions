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

package de.markusbordihn.playercompanions.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import de.markusbordihn.playercompanions.client.model.SmallSlimeModel;
import de.markusbordihn.playercompanions.entity.follower.SmallSlime;

public class SmallSlimeStatusLayer extends RenderLayer<SmallSlime, SmallSlimeModel<SmallSlime>>{
  private static final ResourceLocation WOLF_COLLAR_LOCATION =
      new ResourceLocation("textures/entity/wolf/wolf_collar.png");

  public SmallSlimeStatusLayer(RenderLayerParent<SmallSlime, SmallSlimeModel<SmallSlime>> renderer) {
      super(renderer);
   }

  public void render(PoseStack poseStack, MultiBufferSource buffer, int p_117722_,
      SmallSlime entity, float p_117724_, float p_117725_, float p_117726_, float p_117727_,
      float p_117728_, float p_117729_) {
    if (entity.isTame() && !entity.isInvisible()) {
      float[] afloat = entity.getColor().getTextureDiffuseColors();
      renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, poseStack, buffer,
          p_117722_, entity, afloat[0], afloat[1], afloat[2]);
    }
  }
}
