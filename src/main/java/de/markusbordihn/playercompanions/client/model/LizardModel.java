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

public class LizardModel<T extends TamableAnimal> extends AgeableListModel<T> {

  public final ModelPart head;
  private final ModelPart body;
  private final ModelPart leftFrontLeg;
  private final ModelPart leftHindLeg;
  private final ModelPart rightFrontLeg;
  private final ModelPart rightHindLeg;
  private final ModelPart tail;
  private final ModelPart tongue;

  public LizardModel(ModelPart modelPart) {
    this.body = modelPart.getChild("body");
    this.head = modelPart.getChild("head");
    this.leftFrontLeg = modelPart.getChild("left_front_leg");
    this.leftHindLeg = modelPart.getChild("left_hind_leg");
    this.rightFrontLeg = modelPart.getChild("right_front_leg");
    this.rightHindLeg = modelPart.getChild("right_hind_leg");
    this.tail = this.body.getChild("tail");
    this.tongue = this.head.getChild("tongue");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    // Body
    PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
        .texOffs(0, 0).addBox(-1.5F, 4.0F, -4.0F, 3.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 16.0F, 0.0F));
    body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(10, 12)
        .addBox(-1.0F, -0.25F, -0.75F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 0)
        .addBox(-0.5F, -0.25F, 3.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 4.75F, 3.0F));

    // Head with trill
    PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create()
        .texOffs(0, 9).addBox(-1.5F, 2.0F, -5.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.01F)),
        PartPose.offset(0.0F, 16.0F, -2.0F));
    head.addOrReplaceChild("tongue", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, -3.5F,
        -8.0F, 1.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 2.0F));
    PartDefinition trill = head.addOrReplaceChild("trill", CubeListBuilder.create(),
        PartPose.offset(0.0F, 0.0F, 0.0F));
    trill.addOrReplaceChild("trill_r1",
        CubeListBuilder.create().texOffs(0, 3).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(2.5F, 4.0F, -0.984F, 3.1416F, 0.0F, 0.3927F));
    trill.addOrReplaceChild("trill_r2",
        CubeListBuilder.create().texOffs(0, 3).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(2.6531F, 2.3696F, -0.985F, 0.0F, 0.0F, -0.3927F));
    trill.addOrReplaceChild("trill_r3",
        CubeListBuilder.create().texOffs(0, 3).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-2.6435F, 2.3465F, -0.984F, 0.0F, 3.1416F, 0.3927F));
    trill.addOrReplaceChild("trill_r4",
        CubeListBuilder.create().texOffs(0, 3).addBox(-1.5F, -1.0F, 0.0F, 3.0F, 2.0F, 0.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(-2.5F, 4.0F, -0.985F, 3.1416F, 3.1416F, -0.3927F));

    // Legs
    partDefinition.addOrReplaceChild("right_hind_leg", CubeListBuilder.create().texOffs(0, 9)
        .addBox(1.5F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(-3.0F, 18.0F, 6.0F));
    partDefinition.addOrReplaceChild("left_hind_leg", CubeListBuilder.create().texOffs(0, 9)
        .addBox(-0.5F, 4.0F, -4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(1.0F, 18.0F, 6.0F));
    partDefinition.addOrReplaceChild("right_front_leg", CubeListBuilder.create().texOffs(0, 9)
        .addBox(1.5F, 4.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(-3.0F, 18.0F, -1.0F));
    partDefinition.addOrReplaceChild("left_front_leg", CubeListBuilder.create().texOffs(0, 9)
        .addBox(-0.5F, 4.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(1.0F, 18.0F, -1.0F));

    return LayerDefinition.create(meshDefinition, 32, 32);
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
    if (this.young) {
      this.head.setPos(0.0F, 12.5F, -2.0F);
    } else {
      this.head.setPos(0.0F, 16.0F, -2.0F);
    }
    if (entity.isInSittingPose()) {
      this.body.xRot = ((float) Math.PI / -10F);
      this.body.setPos(0.0F, 17.1F, 1.5F);
      this.tail.xRot = 0;
      this.tongue.visible = true;
    } else {
      this.body.xRot = 0;
      this.body.setPos(0.0F, 16.0F, 0.0F);
      this.tail.xRot = 0;
      this.tongue.visible = false;
    }
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {
    if (!entity.isInSittingPose()) {
      this.head.xRot = headPitch * ((float) Math.PI / 210F);
      this.head.yRot = netHeadYaw * ((float) Math.PI / 210F);
      this.tail.yRot = -netHeadYaw * ((float) Math.PI / 220F);
      this.rightHindLeg.xRot = Mth.cos(limbSwing * 1.333F) * 0.3F * limbSwingAmount;
      this.leftHindLeg.xRot =
          Mth.cos(limbSwing * 1.333F + (float) Math.PI) * 0.3F * limbSwingAmount;
      this.leftFrontLeg.xRot = Mth.cos(limbSwing * 1.333F) * 0.3F * limbSwingAmount;
      this.rightFrontLeg.xRot =
          Mth.cos(limbSwing * 1.333F + (float) Math.PI) * 0.3F * limbSwingAmount;
    }
  }

}
