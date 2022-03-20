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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.DobutsuModel;
import de.markusbordihn.playercompanions.client.model.FairyModel;
import de.markusbordihn.playercompanions.client.model.PigModel;
import de.markusbordihn.playercompanions.client.model.RoosterModel;
import de.markusbordihn.playercompanions.client.model.SamuraiModel;
import de.markusbordihn.playercompanions.client.model.SmallGhastModel;
import de.markusbordihn.playercompanions.client.model.SmallSlimeModel;
import de.markusbordihn.playercompanions.client.model.SnailModel;
import de.markusbordihn.playercompanions.client.model.WelshCorgiModel;
import de.markusbordihn.playercompanions.client.renderer.companions.DobutsuRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.FairyRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.PigRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.RoosterRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.SamuraiRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.SmallGhastRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.SmallSlimeRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.SnailRenderer;
import de.markusbordihn.playercompanions.client.renderer.companions.WelshCorgiRenderer;
import de.markusbordihn.playercompanions.entity.ModEntityType;

@OnlyIn(Dist.CLIENT)
public class ClientRenderer {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Layer Definitions
  public static final ModelLayerLocation DOBUTSU =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "dobutsu"), "main");
  public static final ModelLayerLocation FAIRY =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "fairy"), "main");
  public static final ModelLayerLocation PIG =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "pig"), "main");
  public static final ModelLayerLocation ROOSTER =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "rooster"), "main");
  public static final ModelLayerLocation SAMURAI =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "samurai"), "main");
  public static final ModelLayerLocation SMALL_GHAST =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "small_ghast"), "main");
  public static final ModelLayerLocation SMALL_SLIME =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "small_slime"), "main");
  public static final ModelLayerLocation SMALL_SLIME_OUTER = new ModelLayerLocation(
      new ResourceLocation(Constants.MOD_ID, "small_slime"), "outer");
  public static final ModelLayerLocation SNAIL =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "snail"), "main");
  public static final ModelLayerLocation WELSH_CORGI =
      new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, "welsh_corgi"), "main");

  protected ClientRenderer() {}

  public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    log.info("{} Entity Renders ...", Constants.LOG_REGISTER_PREFIX);

    event.registerEntityRenderer(ModEntityType.DOBUTSU.get(), DobutsuRenderer::new);
    event.registerEntityRenderer(ModEntityType.FAIRY.get(), FairyRenderer::new);
    event.registerEntityRenderer(ModEntityType.PIG.get(), PigRenderer::new);
    event.registerEntityRenderer(ModEntityType.ROOSTER.get(), RoosterRenderer::new);
    event.registerEntityRenderer(ModEntityType.SAMURAI.get(), SamuraiRenderer::new);
    event.registerEntityRenderer(ModEntityType.SMALL_GHAST.get(), SmallGhastRenderer::new);
    event.registerEntityRenderer(ModEntityType.SMALL_SLIME.get(), SmallSlimeRenderer::new);
    event.registerEntityRenderer(ModEntityType.SNAIL.get(), SnailRenderer::new);
    event.registerEntityRenderer(ModEntityType.WELSH_CORGI.get(),
        WelshCorgiRenderer::new);
  }

  public static void registerEntityLayerDefinitions(
      EntityRenderersEvent.RegisterLayerDefinitions event) {
    log.info("{} Entity Layer Definitions ...", Constants.LOG_REGISTER_PREFIX);

    event.registerLayerDefinition(DOBUTSU, DobutsuModel::createBodyLayer);
    event.registerLayerDefinition(FAIRY, FairyModel::createBodyLayer);
    event.registerLayerDefinition(PIG, PigModel::createBodyLayer);
    event.registerLayerDefinition(ROOSTER, RoosterModel::createBodyLayer);
    event.registerLayerDefinition(SAMURAI, SamuraiModel::createBodyLayer);
    event.registerLayerDefinition(SMALL_GHAST, SmallGhastModel::createBodyLayer);
    event.registerLayerDefinition(SMALL_SLIME, SmallSlimeModel::createInnerBodyLayer);
    event.registerLayerDefinition(SMALL_SLIME_OUTER, SmallSlimeModel::createOuterBodyLayer);
    event.registerLayerDefinition(SNAIL, SnailModel::createBodyLayer);
    event.registerLayerDefinition(WELSH_CORGI, WelshCorgiModel::createBodyLayer);
  }
}
