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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.TamableAnimal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.Constants;

@OnlyIn(Dist.CLIENT)
public class FireflyModel<T extends TamableAnimal> extends AgeableListModel<T> {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private final ModelPart head;
  private final ModelPart body;
  private final ModelPart rightWing;
  private final ModelPart leftWing;
  private final ModelPart frontLeg;
  private final ModelPart backLeg;

  public FireflyModel(ModelPart modelPart) {
    super(false, 24.0F, 0.0F);
    this.head = modelPart.getChild("head");
    this.body = modelPart.getChild("body");
    this.rightWing = modelPart.getChild("right_wing");
    this.leftWing = modelPart.getChild("left_wing");
    this.frontLeg = modelPart.getChild("front_leg");
    this.backLeg = modelPart.getChild("back_leg");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    PartDefinition head =
        partDefinition
            .addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 14).addBox(-2.0F, -2.0019F, -3.9135F, 4.0F,
                    4.0F, 4.0F, new CubeDeformation(0.1F)),
                PartPose.offset(0.0F, 20.0019F, -3.0865F));

    head.addOrReplaceChild("antenna_right",
        CubeListBuilder.create().texOffs(0, 2).mirror()
            .addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
        PartPose.offsetAndRotation(1.975F, -2.9991F, -2.4182F, -0.3927F, 0.0F, 0.0F));

    head.addOrReplaceChild("antenna_left",
        CubeListBuilder.create().texOffs(0, 2).addBox(0.0F, -1.5F, -1.0F, 0.0F, 3.0F, 2.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-1.975F, -2.9991F, -2.4182F, -0.3927F, 0.0F, 0.0F));

    partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F,
        -1.0F, -3.0F, 4.0F, 4.0F, 10.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.5F, 19.0F, 0.0F));

    partDefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(12, 0)
        .addBox(-8.5F, 2.998F, 0.5F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)),
        PartPose.offset(-1.0F, 15.0F, -3.0F));

    partDefinition.addOrReplaceChild("left_wing",
        CubeListBuilder.create().texOffs(12, 0).mirror()
            .addBox(-1.5F, 2.998F, 0.5F, 9.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false),
        PartPose.offset(2.0F, 15.0F, -3.0F));

    partDefinition.addOrReplaceChild("front_leg", CubeListBuilder.create().texOffs(0, 2)
        .addBox(-4.0F, 0.0F, 0.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offset(2.0F, 22.0F, -2.0F));

    partDefinition.addOrReplaceChild("back_leg", CubeListBuilder.create().texOffs(0, 0)
        .addBox(-4.0F, 0.0F, 1.0F, 4.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)),
        PartPose.offset(2.0F, 22.0F, 2.0F));

    return LayerDefinition.create(meshDefinition, 64, 64);
  }

  @Override
  protected Iterable<ModelPart> headParts() {
    return List.of(this.head);
  }

  @Override
  protected Iterable<ModelPart> bodyParts() {
    return List.of(this.body, this.frontLeg, this.backLeg, this.leftWing, this.rightWing);
  }

  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
    if (entity.isInSittingPose()
        || (entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7D)) {
      this.body.xRot = 0.0F;
      this.head.setPos(0.0F, 20.0019F, -3.0865F);
      this.frontLeg.setPos(2.0F, 22.0F, -2.0F);
      this.backLeg.setPos(2.0F, 22.0F, 2.0F);
    } else {
      this.body.xRot = -0.8F;
      this.head.setPos(0.0F, 16.0019F, -2.0865F);
      this.frontLeg.setPos(2.0F, 19.5F, -3.5F);
      this.backLeg.setPos(2.0F, 23.0F, -1.0F);
    }
  }

  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {

    // Don't animate death entities
    if (entity.isDeadOrDying()) {
      return;
    }

    if ((entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7D)
        || entity.isInSittingPose()) {
      this.rightWing.setPos(-1.0F, 15.001F, -3.0F);
      this.rightWing.xRot = 0.0F;
      this.rightWing.yRot = 0.8F;
      this.rightWing.zRot = 0.0F;
      this.leftWing.setPos(2.0F, 15.0F, -3.0F);
      this.leftWing.xRot = 0.0F;
      this.leftWing.yRot = -this.rightWing.yRot;
      this.leftWing.zRot = 0.0F;
    } else {
      float f = ageInTicks * 120.32113F * ((float) Math.PI / 180F);
      this.rightWing.setPos(-1.0F, 14.0F, 1F);
      this.rightWing.xRot = -0.8F;
      this.rightWing.yRot = 0.3F;
      this.rightWing.zRot = Mth.cos(f) * (float) Math.PI * 0.15F;
      this.leftWing.setPos(2.0F, 14.0F, 1.0F);
      this.leftWing.xRot = this.rightWing.xRot;
      this.leftWing.yRot = -this.rightWing.yRot;
      this.leftWing.zRot = -this.rightWing.zRot;
    }
    this.head.xRot = headPitch * ((float) Math.PI / 225F);
    this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
  }

}
