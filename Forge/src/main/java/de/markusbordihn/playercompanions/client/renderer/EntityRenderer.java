/*
 * Copyright 2026 Markus Bordihn
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

import de.markusbordihn.easynpc.api.model.CustomModelConfig;
import de.markusbordihn.easynpc.api.model.OriginalModelConfig;
import de.markusbordihn.easynpc.client.renderer.entity.raw.ChickenRawRenderer;
import de.markusbordihn.easynpc.client.renderer.entity.raw.PigRawRenderer;
import de.markusbordihn.easynpc.client.renderer.entity.raw.SlimeRawRenderer;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.ModModelLayers;
import de.markusbordihn.playercompanions.entity.CompanionEntityType;
import de.markusbordihn.playercompanions.entity.ModEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class EntityRenderer {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private EntityRenderer() {
  }

  public static void register(EntityRenderersEvent.RegisterRenderers event) {
    log.info("{} Entity Renders ...", Constants.LOG_REGISTER_PREFIX);

    event.registerEntityRenderer(ModEntityType.getEntityType(CompanionEntityType.PIG),
      context -> new PigRawRenderer(context,
        CustomModelConfig.replacementUseVariantTexture(
          ModModelLayers.PIG_COMPANION)));

    event.registerEntityRenderer(ModEntityType.getEntityType(CompanionEntityType.ROOSTER),
      context -> new ChickenRawRenderer(context,
        CustomModelConfig.replacementUseVariantTexture(
          ModModelLayers.ROOSTER_COMPANION)));

    event.registerEntityRenderer(ModEntityType.getEntityType(CompanionEntityType.SMALL_SLIME),
      context -> new SlimeRawRenderer(context,
        OriginalModelConfig.withVariantTexture()));
  }
}
