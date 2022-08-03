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

package de.markusbordihn.playercompanions.client.model;

import java.util.List;

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

@OnlyIn(Dist.CLIENT)
public class RoosterModel<T extends TamableAnimal> extends AgeableListModel<T>
    implements PlayerCompanionModel {

  private final ModelPart body;
  private final ModelPart head;
  private final ModelPart leftLeg;
  private final ModelPart leftWing;
  private final ModelPart rightHand;
  private final ModelPart rightLeg;
  private final ModelPart rightWing;
  private final ModelPart tail;

  public RoosterModel(ModelPart modelPart) {
    this.body = modelPart.getChild("body");
    this.head = modelPart.getChild("head");
    this.leftLeg = modelPart.getChild("left_leg");
    this.leftWing = modelPart.getChild("left_wing");
    this.rightHand = modelPart.getChild("right_hand");
    this.rightLeg = modelPart.getChild("right_leg");
    this.rightWing = modelPart.getChild("right_wing");
    this.tail = this.body.getChild("tail");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
        .texOffs(24, 0).addBox(-3.0F, -3.0F, -6.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 16.0F, 0.0F));

    body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(10, 14)
        .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(24, 7)
        .addBox(-2.0F, 4.0F, 0.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)).texOffs(0, 27)
        .addBox(-3.0F, -2.0F, -5.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(18, 24)
        .addBox(-3.0F, -3.0F, -4.0F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
        .addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

    PartDefinition tail =
        body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

    tail.addOrReplaceChild("tail_r1",
        CubeListBuilder.create().texOffs(18, 0)
            .addBox(-1.0F, -4.5F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(11, 29)
            .addBox(-1.0F, -1.5F, -1.0F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, -12.5F, 6.5F, -0.3927F, 0.0F, 0.0F));

    tail.addOrReplaceChild("tail_r2",
        CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.75F, 1.075F, 2.0F, 3.0F, 1.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, -15.0F, 8.5F, -0.7854F, 0.0F, 0.0F));

    tail.addOrReplaceChild("tail_r3",
        CubeListBuilder.create().texOffs(10, 18).addBox(-1.0F, -1.0F, -1.05F, 2.0F, 3.0F, 1.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, -15.1924F, 6.5328F, 0.3927F, 0.0F, 0.0F));

    PartDefinition head = partDefinition.addOrReplaceChild("head",
        CubeListBuilder.create().texOffs(18, 14).addBox(-2.002F, -6.0F, -2.002F, 4.0F, 6.0F, 4.0F,
            new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 14.0F, -4.0F));

    head.addOrReplaceChild("feather_r1",
        CubeListBuilder.create().texOffs(30, 14)
            .addBox(-0.5F, -1.5F, 0.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(21, 30)
            .addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, -7.5F, -0.5F, -0.3927F, 0.0F, 0.0F));

    head.addOrReplaceChild("comb", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -2.0F,
        -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

    head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -4.0F,
        -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

    partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(29, 27)
        .addBox(-1.0F, 2.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
        PartPose.offset(-2.0F, 19.0F, 1.0F));

    partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(29, 27)
        .addBox(-1.0F, 2.0F, -3.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
        PartPose.offset(1.0F, 19.0F, 1.0F));

    PartDefinition rightWing = partDefinition.addOrReplaceChild("right_wing",
        CubeListBuilder.create(), PartPose.offset(-3.0F, 13.0F, 0.0F));

    rightWing.addOrReplaceChild("wing0_r1",
        CubeListBuilder.create().texOffs(0, 14).addBox(-0.5F, -2.5F, -4.0F, 1.0F, 5.0F, 8.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-0.5F, 2.5F, 1.0F, 3.1416F, 3.1416F, 0.0F));

    partDefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 14)
        .addBox(0.0F, 0.0F, -3.0F, 1.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
        PartPose.offset(3.0F, 13.0F, 0.0F));

    partDefinition.addOrReplaceChild("right_hand", CubeListBuilder.create(),
        PartPose.offsetAndRotation(-0.2F, 0.4F, 0.3F, -120.0F, 180.0F, 0.0F));

    return LayerDefinition.create(meshDefinition, 64, 64);
  }

  protected Iterable<ModelPart> headParts() {
    return List.of(this.head);
  }

  protected Iterable<ModelPart> bodyParts() {
    return List.of(this.body, this.rightLeg, this.leftLeg, this.rightWing, this.leftWing);
  }

  @Override
  public ModelPart rightHand() {
    return this.rightHand;
  }

  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
    if (entity.isInSittingPose()) {
      this.body.setPos(0.0F, 20.0F, 0.0F);
      this.head.setPos(0.0F, 18.0F, -4.0F);
      this.leftWing.setPos(3.0F, 17.0F, 0.0F);
      this.rightWing.setPos(-3.0F, 17.0F, 0.0F);
      this.tail.setPos(0.0F, 5.0F, -4.0F);
      this.tail.xRot = -0.45F;
      this.rightLeg.visible = false;
      this.leftLeg.visible = false;
    } else {
      this.body.setPos(0.0F, 16.0F, 0.0F);
      this.head.setPos(0.0F, 14.0F, -4.0F);
      this.leftWing.setPos(3.0F, 13.0F, 0.0F);
      this.rightWing.setPos(-3.0F, 13.0F, 0.0F);
      this.tail.setPos(0.0F, 8.0F, 0.0F);
      this.tail.xRot = 0.0F;
      this.rightLeg.visible = true;
      this.leftLeg.visible = true;
    }
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {

    // Don't animate death entities
    if (entity.isDeadOrDying()) {
      return;
    }

    if (!entity.isInSittingPose()) {
      this.head.xRot = headPitch * ((float) Math.PI / 180F);
      this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
      this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
      this.rightWing.zRot = ageInTicks;
      this.leftWing.zRot = -ageInTicks;
    }
  }

}
