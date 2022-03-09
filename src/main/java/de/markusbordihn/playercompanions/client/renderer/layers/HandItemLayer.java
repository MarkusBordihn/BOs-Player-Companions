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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.PlayerCompanionModel;

public class HandItemLayer<T extends LivingEntity, M extends EntityModel<T>>
    extends RenderLayer<T, M> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  ModelPart rightHand;

  public HandItemLayer(RenderLayerParent<T, M> parent) {
    super(parent);
  }

  @Override
  public void render(PoseStack poseStack, MultiBufferSource buffer, int lightLevel, T entity,
      float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_,
      float p_117358_) {
    ItemStack itemStackMainHand = entity.getMainHandItem();

    if (this.getParentModel() instanceof PlayerCompanionModel playerCompanionModel) {
      rightHand = playerCompanionModel.rightHand();
    }

    if (rightHand != null) {
      this.renderRightHandItem(entity, itemStackMainHand,
          ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack,
          buffer, lightLevel);
    }
  }

  protected void renderRightHandItem(LivingEntity livingEntity, ItemStack itemStack,
      ItemTransforms.TransformType transformType, HumanoidArm p_117188_, PoseStack poseStack,
      MultiBufferSource buffer, int lightLevel) {
    if (!itemStack.isEmpty()) {
      poseStack.pushPose();
      if (rightHand != null) {
        poseStack.translate(rightHand.x, rightHand.y, rightHand.z);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(rightHand.xRot));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rightHand.yRot));
      } else {
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      }
      boolean flag = p_117188_ == HumanoidArm.LEFT;
      poseStack.translate((flag ? -1 : 1) / 16.0F, 0.125D, -0.625D);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(livingEntity, itemStack,
          transformType, flag, poseStack, buffer, lightLevel);
      poseStack.popPose();
    }
  }

}
