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

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.RoosterModel;
import de.markusbordihn.playercompanions.client.renderer.ClientRenderer;
import de.markusbordihn.playercompanions.client.renderer.layers.HandItemLayer;
import de.markusbordihn.playercompanions.entity.companions.Rooster;

@OnlyIn(Dist.CLIENT)
public class RoosterRenderer extends MobRenderer<Rooster, RoosterModel<Rooster>> {

  private static final ResourceLocation TEXTURE_LOCATION =
      new ResourceLocation(Constants.MOD_ID, "textures/entity/rooster/rooster.png");

  public RoosterRenderer(EntityRendererProvider.Context context) {
    super(context, new RoosterModel<>(context.bakeLayer(ClientRenderer.ROOSTER)), 0.35F);
    this.addLayer(new HandItemLayer<>(this));
  }

  public ResourceLocation getTextureLocation(Rooster entity) {
    return TEXTURE_LOCATION;
  }

  @Override
  protected float getBob(Rooster entity, float delay) {
    float f = Mth.lerp(delay, entity.getOFlap(), entity.getFlap());
    float f1 = Mth.lerp(delay, entity.getOFlapSpeed(), entity.getFlapSpeed());
    return (Mth.sin(f) + 1.0F) * f1;
  }

}
