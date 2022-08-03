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


package de.markusbordihn.playercompanions.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.TamableAnimal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SamuraiModel<T extends TamableAnimal> extends HumanoidModel<T> {

  public SamuraiModel(ModelPart modelPart) {
    super(modelPart);
    this.hat.visible = false;
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    // Body
    partDefinition.addOrReplaceChild("body",
        CubeListBuilder.create().texOffs(26, 33)
            .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 17)
            .addBox(-4.0F, 0.0F, -2.5F, 8.0F, 18.0F, 5.0F, new CubeDeformation(0.5F)),
        PartPose.offset(0.0F, 0.0F, 0.0F));

    // Head with Overlay
    partDefinition.addOrReplaceChild("head",
        CubeListBuilder.create().texOffs(26, 17)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
            .addBox(-4.5F, -8.5F, -4.5F, 9.0F, 8.0F, 9.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 0.0F, 0.0F));
    partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(),
        PartPose.offset(-6.0F, 11.0F, 0.0F));

    // Arms
    partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(34, 53)
        .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(45, 44)
        .addBox(-1.0F, -2.5F, -2.5F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)),
        PartPose.offset(5.0F, 2.0F, 0.0F));
    partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(52, 0)
        .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(16, 49)
        .addBox(-3.0F, -2.5F, -2.5F, 4.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)),
        PartPose.offset(-5.0F, 2.0F, 0.0F));

    // Legs
    partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 40)
        .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
        PartPose.offset(-1.9F, 12.0F, 0.0F));
    partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(36, 0)
        .addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
        PartPose.offset(2.1F, 12.0F, 0.0F));

    // Hands
    partDefinition.addOrReplaceChild("leftHand", CubeListBuilder.create(),
        PartPose.offset(6.0F, 11.0F, 0.0F));
    partDefinition.addOrReplaceChild("rightHand", CubeListBuilder.create(),
        PartPose.offset(-6.0F, 11.0F, 0.0F));

    return LayerDefinition.create(meshDefinition, 128, 128);
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {

    // Don't animate death entities
    if (entity.isDeadOrDying()) {
      return;
    }

    if (entity.isInSittingPose()) {
      this.head.setPos(0.0F, 8.0F, 0.0F);
      this.body.setPos(0.0F, 8.0F, 0.0F);
      this.leftArm.setPos(5.0F, 10.0F, 0.0F);
      this.rightArm.setPos(-5.0F, 10.0F, 0.0F);
      this.leftLeg.setPos(-1.9F, 22.0F, 0.0F);
      this.leftLeg.xRot = -1.6F;
      this.leftLeg.yRot = 0.1F;
      this.rightLeg.setPos(2.1F, 22.0F, 0.0F);
      this.rightLeg.xRot = -1.6F;
      this.rightLeg.yRot = -0.1F;
    } else {
      super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
  }

}
