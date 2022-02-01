package de.markusbordihn.playercompanions.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.markusbordihn.playercompanions.client.model.SmallSlimeModel;
import de.markusbordihn.playercompanions.entity.follower.SmallSlime;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class SmallSlimeStatusLayer extends RenderLayer<SmallSlime, SmallSlimeModel<SmallSlime>>{
  private static final ResourceLocation WOLF_COLLAR_LOCATION =
      new ResourceLocation("textures/entity/wolf/wolf_collar.png");

  public SmallSlimeStatusLayer(RenderLayerParent<SmallSlime, SmallSlimeModel<SmallSlime>> p_117707_) {
      super(p_117707_);
   }

  public void render(PoseStack p_117720_, MultiBufferSource p_117721_, int p_117722_,
      SmallSlime p_117723_, float p_117724_, float p_117725_, float p_117726_, float p_117727_,
      float p_117728_, float p_117729_) {
    if (p_117723_.isTame() && !p_117723_.isInvisible()) {
      float[] afloat = p_117723_.getColor().getTextureDiffuseColors();
      renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, p_117720_, p_117721_,
          p_117722_, p_117723_, afloat[0], afloat[1], afloat[2]);
    }
  }
}
