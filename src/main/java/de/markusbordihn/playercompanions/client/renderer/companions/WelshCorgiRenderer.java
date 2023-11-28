/*
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

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.WelshCorgiModel;
import de.markusbordihn.playercompanions.client.renderer.ClientRenderer;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.companions.WelshCorgi;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WelshCorgiRenderer extends MobRenderer<WelshCorgi, WelshCorgiModel<WelshCorgi>> {

  // Variant Textures
  protected static final Map<PlayerCompanionVariant, ResourceLocation> TEXTURE_BY_VARIANT =
      Util.make(
          new EnumMap<>(PlayerCompanionVariant.class),
          hashMap -> {
            hashMap.put(
                PlayerCompanionVariant.DEFAULT,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/welsh_corgi/welsh_corgi_default.png"));
            hashMap.put(
                PlayerCompanionVariant.MIXED,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/welsh_corgi/welsh_corgi_mixed.png"));
            hashMap.put(
                PlayerCompanionVariant.BLACK,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/welsh_corgi/welsh_corgi_black.png"));
          });
  protected static final ResourceLocation DEFAULT_TEXTURE =
      TEXTURE_BY_VARIANT.get(PlayerCompanionVariant.DEFAULT);

  public WelshCorgiRenderer(EntityRendererProvider.Context context) {
    super(context, new WelshCorgiModel<>(context.bakeLayer(ClientRenderer.WELSH_CORGI)), 0.5F);
  }

  @Override
  public ResourceLocation getTextureLocation(WelshCorgi entity) {
    return TEXTURE_BY_VARIANT.getOrDefault(entity.getVariant(), DEFAULT_TEXTURE);
  }
}
