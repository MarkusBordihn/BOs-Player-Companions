/**
 * Copyright 2021 Markus Bordihn
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.markusbordihn.playercompanions.client.renderer.companions;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.SmallSlimeModel;
import de.markusbordihn.playercompanions.client.renderer.ClientRenderer;
import de.markusbordihn.playercompanions.client.renderer.layers.SmallSlimeOuterLayer;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.companions.SmallSlime;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmallSlimeRenderer extends MobRenderer<SmallSlime, SmallSlimeModel<SmallSlime>> {

  // Variant Textures
  protected static final Map<PlayerCompanionVariant, ResourceLocation> TEXTURE_BY_VARIANT =
      Util.make(
          new EnumMap<>(PlayerCompanionVariant.class),
          hashMap -> {
            hashMap.put(
                PlayerCompanionVariant.DEFAULT,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_green.png"));
            hashMap.put(
                PlayerCompanionVariant.BLACK,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_black.png"));
            hashMap.put(
                PlayerCompanionVariant.BLUE,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_blue.png"));
            hashMap.put(
                PlayerCompanionVariant.BROWN,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_brown.png"));
            hashMap.put(
                PlayerCompanionVariant.CYAN,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_cyan.png"));
            hashMap.put(
                PlayerCompanionVariant.GRAY,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_gray.png"));
            hashMap.put(
                PlayerCompanionVariant.GREEN,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_green.png"));
            hashMap.put(
                PlayerCompanionVariant.LIGHT_BLUE,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_light_blue.png"));
            hashMap.put(
                PlayerCompanionVariant.LIGHT_GRAY,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_light_gray.png"));
            hashMap.put(
                PlayerCompanionVariant.LIME,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_lime.png"));
            hashMap.put(
                PlayerCompanionVariant.MAGENTA,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_magenta.png"));
            hashMap.put(
                PlayerCompanionVariant.ORANGE,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_orange.png"));
            hashMap.put(
                PlayerCompanionVariant.PINK,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_pink.png"));
            hashMap.put(
                PlayerCompanionVariant.PURPLE,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_purple.png"));
            hashMap.put(
                PlayerCompanionVariant.RED,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_red.png"));
            hashMap.put(
                PlayerCompanionVariant.WHITE,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_white.png"));
            hashMap.put(
                PlayerCompanionVariant.YELLOW,
                new ResourceLocation(
                    Constants.MOD_ID, "textures/entity/small_slime/small_slime_yellow.png"));
          });
  protected static final ResourceLocation DEFAULT_TEXTURE =
      TEXTURE_BY_VARIANT.get(PlayerCompanionVariant.DEFAULT);

  public SmallSlimeRenderer(EntityRendererProvider.Context context) {
    super(context, new SmallSlimeModel<>(context.bakeLayer(ClientRenderer.SMALL_SLIME)), 0.4F);
    this.addLayer(new SmallSlimeOuterLayer<>(this, context.getModelSet()));
  }

  @Override
  public ResourceLocation getTextureLocation(SmallSlime entity) {
    return TEXTURE_BY_VARIANT.getOrDefault(entity.getVariant(), DEFAULT_TEXTURE);
  }
}
