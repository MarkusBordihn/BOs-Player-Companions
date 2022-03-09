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
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.TamableAnimal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.client.model.SmallSlimeModel;
import de.markusbordihn.playercompanions.client.renderer.ClientRenderer;

@OnlyIn(Dist.CLIENT)
public class SmallSlimeOuterLayer<T extends TamableAnimal>
    extends RenderLayer<T, SmallSlimeModel<T>> {

  private final EntityModel<T> model;

  public SmallSlimeOuterLayer(RenderLayerParent<T, SmallSlimeModel<T>> layer,
      EntityModelSet entityModelSet) {
    super(layer);
    this.model = new SmallSlimeModel<>(entityModelSet.bakeLayer(ClientRenderer.SMALL_SLIME_OUTER));
  }

  public void render(PoseStack poseStack, MultiBufferSource buffer, int p_117472_, T entity,
      float p_117474_, float p_117475_, float p_117476_, float p_117477_, float p_117478_,
      float p_117479_) {
    Minecraft minecraft = Minecraft.getInstance();
    boolean flag = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
    if (!entity.isInvisible() || flag) {
      VertexConsumer vertexConsumer;
      if (flag) {
        vertexConsumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(entity)));
      } else {
        vertexConsumer =
            buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
      }
      this.getParentModel().copyPropertiesTo(this.model);
      this.model.prepareMobModel(entity, p_117474_, p_117475_, p_117476_);
      this.model.setupAnim(entity, p_117474_, p_117475_, p_117477_, p_117478_, p_117479_);
      this.model.renderToBuffer(poseStack, vertexConsumer, p_117472_,
          LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
  }
}
