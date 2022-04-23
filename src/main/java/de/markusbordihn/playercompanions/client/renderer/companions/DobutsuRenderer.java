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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.DobutsuModel;
import de.markusbordihn.playercompanions.client.renderer.ClientRenderer;
import de.markusbordihn.playercompanions.client.renderer.layers.HandItemLayer;
import de.markusbordihn.playercompanions.entity.companions.Dobutsu;

@OnlyIn(Dist.CLIENT)
public class DobutsuRenderer extends MobRenderer<Dobutsu, DobutsuModel<Dobutsu>> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public DobutsuRenderer(EntityRendererProvider.Context context) {
    super(context, new DobutsuModel<>(context.bakeLayer(ClientRenderer.DOBUTSU)), 0.3F);
    this.addLayer(new HandItemLayer<>(this));
  }

  @Override
  protected void scale(Dobutsu entity, PoseStack poseStack, float unused) {
    poseStack.scale(0.8F, 0.8F, 0.8F);
  }

  @Override
  public ResourceLocation getTextureLocation(Dobutsu entity) {
    return entity.getTextureLocation();
  }

}
