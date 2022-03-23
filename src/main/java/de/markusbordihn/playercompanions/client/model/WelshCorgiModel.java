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

public class WelshCorgiModel<T extends TamableAnimal> extends AgeableListModel<T> {

  public final ModelPart head;
  private final ModelPart body;
  private final ModelPart leftFrontLeg;
  private final ModelPart leftHindLeg;
  private final ModelPart rightFrontLeg;
  private final ModelPart rightHindLeg;
  private final ModelPart tail;
  private final ModelPart tongue;

  public WelshCorgiModel(ModelPart modelPart) {
    super(true, 8.0F, 3.35F);
    this.head = modelPart.getChild("head");
    this.body = modelPart.getChild("body");
    this.rightHindLeg = modelPart.getChild("right_hind_leg");
    this.leftHindLeg = modelPart.getChild("left_hind_leg");
    this.rightFrontLeg = modelPart.getChild("right_front_leg");
    this.leftFrontLeg = modelPart.getChild("left_front_leg");
    this.tail = this.body.getChild("tail");
    this.tongue = this.head.getChild("snout").getChild("tongue");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    // Body
    PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create(),
        PartPose.offset(0.0F, 16.0F, 0.0F));
    body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(0, 12)
        .addBox(-3.0F, -3.0F, -5.0F, 6.0F, 9.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(17, 21)
        .addBox(-3.0F, -4.5F, -3.0F, 6.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(21, 7)
        .addBox(-3.5F, -4.0F, -4.0F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 1.5708F, 0.0F, 0.0F));
    body.addOrReplaceChild("body_r2",
        CubeListBuilder.create().texOffs(0, 31).addBox(-3.5F, -2.825F, -1.25F, 7.0F, 6.0F, 4.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 2.5F, 4.5F, 0.3927F, 0.0F, 0.0F));
    PartDefinition tail =
        body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 7.5F));
    tail.addOrReplaceChild("tail_r1",
        CubeListBuilder.create().texOffs(21, 0).addBox(-1.0F, -2.75F, -0.5F, 2.0F, 3.0F, 1.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

    // Head with snout and ears
    PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
        .texOffs(0, 0).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 11.0F, -1.0F));
    PartDefinition rightEar = head.addOrReplaceChild("right_ear", CubeListBuilder.create(),
        PartPose.offset(0.0F, 0.0F, 0.0F));
    rightEar.addOrReplaceChild("right_ear_r1", CubeListBuilder.create().texOffs(22, 17)
        .addBox(-1.5F, -5.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(22, 31)
        .addBox(-2.0F, -4.0F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-2.5F, -2.0F, -2.5F, 0.0F, 0.0F, -0.3927F));
    PartDefinition leftEar = head.addOrReplaceChild("left_ear", CubeListBuilder.create(),
        PartPose.offset(0.0F, 0.0F, 0.0F));
    leftEar.addOrReplaceChild("left_ear_r1", CubeListBuilder.create().texOffs(22, 17)
        .addBox(-0.5F, -5.0F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(22, 31)
        .addBox(-1.0F, -4.0F, -0.5F, 3.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(2.5F, -2.0F, -2.5F, 0.0F, 0.0F, 0.3927F));
    PartDefinition snout = head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(26, 0)
        .addBox(-1.0F, 1.9998F, -8.275F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
        .addBox(-0.5F, 1.825F, -8.45F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, -0.5F, 0.0F));
    snout.addOrReplaceChild("snout_r1",
        CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -0.625F, 1.1F, 2.0F, 2.0F, 3.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 3.0F, -8.25F, 0.3927F, 0.0F, 0.0F));
    snout.addOrReplaceChild("snout_r2",
        CubeListBuilder.create().texOffs(26, 0).addBox(-0.975F, -1.0001F, 0.125F, 2.0F, 2.0F, 4.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 3.0F, -8.0F, 0.0F, 0.3927F, 0.0F));
    snout.addOrReplaceChild("snout_r3",
        CubeListBuilder.create().texOffs(26, 0).addBox(-1.025F, -1.0F, 0.125F, 2.0F, 2.0F, 4.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 3.0F, -8.0F, 0.0F, -0.3927F, 0.0F));
    snout.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(0, 2).addBox(-0.5F, 3.25F,
        -8.3F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

    // Legs
    partDefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(30, 31)
        .addBox(-3.005F, -4.0F, 5.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 24.0F, -1.0F));
    partDefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(30, 31)
        .addBox(1.005F, -4.0F, 5.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 24.0F, -1.0F));
    partDefinition
        .addOrReplaceChild(
            "right_front_leg", CubeListBuilder.create().texOffs(30, 31).addBox(-3.005F, -4.0F,
                -3.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 24.0F, -1.0F));
    partDefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(30, 31)
        .addBox(1.005F, -4.0F, -3.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 24.0F, -1.0F));

    return LayerDefinition.create(meshDefinition, 64, 64);
  }

  @Override
  protected Iterable<ModelPart> headParts() {
    return java.util.List.of(this.head);
  }

  @Override
  protected Iterable<ModelPart> bodyParts() {
    return java.util.List.of(this.body, this.leftFrontLeg, this.leftHindLeg, this.rightFrontLeg,
        this.rightHindLeg);
  }

  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
    if (entity.isInSittingPose()) {
      this.body.xRot = ((float) Math.PI / -5F);
      this.tail.xRot = ((float) Math.PI / 4F);
      if (this.young) {
        this.head.setPos(0.0F, 13.0F, -4.5F);
      } else {
        this.head.setPos(0.0F, 10.0F, -1.5F);
      }
      this.head.xRot = 0.0F;
      this.head.yRot = 0.0F;
      this.rightHindLeg.xRot = 80.0F;
      this.rightHindLeg.setPos(0.0F, 16.5F, 0.5F);
      this.leftHindLeg.xRot = 80.0F;
      this.leftHindLeg.setPos(0.0F, 16.5F, 0.5F);
      this.rightFrontLeg.xRot = -0.2617994F;
      this.rightFrontLeg.setPos(0.0F, 24.0F, -2.0F);
      this.leftFrontLeg.xRot = -0.2617994F;
      this.leftFrontLeg.setPos(0.0F, 24.0F, -2.0F);
      this.tongue.visible = true;
    } else {
      this.body.setPos(0.0F, 16.0F, 0.0F);
      this.body.zRot = 0.0F;
      this.body.xRot = 0;
      this.rightHindLeg.setPos(0.0F, 24.0F, -1.0F);
      this.leftHindLeg.setPos(0.0F, 24.0F, -1.0F);
      this.rightFrontLeg.setPos(0.0F, 24.0F, -1.0F);
      this.leftFrontLeg.setPos(0.0F, 24.0F, -1.0F);
      this.head.setPos(0.0F, 11.0F, -1.0F);
      if (this.young) {
        this.head.setPos(0.0F, 14.0F, -4.0F);
      } else {
        this.head.setPos(0.0F, 11.0F, -1.0F);
      }
      this.tail.setPos(0.0F, 1.5F, 7.5F);
      this.tail.xRot = 0.0F;
      this.rightHindLeg.visible = true;
      this.leftHindLeg.visible = true;
      this.rightFrontLeg.visible = true;
      this.leftFrontLeg.visible = true;
      this.tongue.visible = false;
    }
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {
    if (!entity.isInSittingPose()) {
      this.head.xRot = headPitch * ((float) Math.PI / 220F);
      this.head.yRot = netHeadYaw * ((float) Math.PI / 180F) * 0.90F;
      this.rightHindLeg.xRot = Mth.cos(limbSwing * 1.6662F) * 0.3F * limbSwingAmount;
      this.leftHindLeg.xRot =
          Mth.cos(limbSwing * 1.6662F + (float) Math.PI) * 0.3F * limbSwingAmount;
      this.leftFrontLeg.xRot = Mth.cos(limbSwing * 1.6662F) * 0.3F * limbSwingAmount;
      this.rightFrontLeg.xRot =
          Mth.cos(limbSwing * 1.6662F + (float) Math.PI) * 0.3F * limbSwingAmount;
    }
  }

}
