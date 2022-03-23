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

public class DobutsuModel<T extends TamableAnimal> extends AgeableListModel<T>
    implements PlayerCompanionModel {

  private final ModelPart head;
  private final ModelPart leftHand;
  private final ModelPart leftLeg;
  private final ModelPart rightHand;
  private final ModelPart rightLeg;

  public DobutsuModel(ModelPart modelPart) {
    this.head = modelPart.getChild("head");
    this.leftLeg = modelPart.getChild("left_leg");
    this.leftHand = modelPart.getChild("left_hand");
    this.rightHand = modelPart.getChild("right_hand");
    this.rightLeg = modelPart.getChild("right_leg");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    // Head and Body
    partDefinition.addOrReplaceChild("head",
        CubeListBuilder.create().texOffs(0, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(32, 0)
            .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
        PartPose.offset(0.0F, 15.0F, 0.0F));

    // Legs
    partDefinition.addOrReplaceChild("left_leg",
        CubeListBuilder.create().texOffs(16, 48)
            .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 48)
            .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
        PartPose.offset(1.9F, 8.0F, 0.0F));
    partDefinition.addOrReplaceChild("right_leg",
        CubeListBuilder.create().texOffs(0, 16)
            .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 32)
            .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)),
        PartPose.offset(-1.9F, 8.0F, 0.0F));

    // Hands
    partDefinition.addOrReplaceChild("right_hand", CubeListBuilder.create(),
        PartPose.offsetAndRotation(-4.5F, 16.0F, 0.0F, -120.0F, 180.0F, 0.0F));
    partDefinition.addOrReplaceChild("left_hand", CubeListBuilder.create(),
        PartPose.offsetAndRotation(4.5F, 16.0F, 0.0F, -120.0F, 180.0F, 0.0F));

    return LayerDefinition.create(meshDefinition, 64, 64);
  }

  @Override
  protected Iterable<ModelPart> headParts() {
    return List.of(this.head);
  }

  @Override
  protected Iterable<ModelPart> bodyParts() {
    return List.of(this.rightLeg, this.leftLeg);
  }

  @Override
  public ModelPart rightHand() {
    return this.rightHand;
  }

  @Override
  public ModelPart leftHand() {
    return this.leftHand;
  }

  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
    if (entity.isInSittingPose()) {
      if (this.young) {
        this.head.setPos(0.0F, 16.1F, 0.0F);
      } else {
        this.head.setPos(0.0F, 19.1F, 0.0F);
      }
      this.leftLeg.setPos(1.9F, 21.8F, 6.0F);
      this.leftLeg.xRot = -1.58F;
      this.leftLeg.yRot = -0.1F;
      this.rightLeg.setPos(-1.9F, 21.8F, 6.0F);
      this.rightLeg.xRot = -1.58F;
      this.rightLeg.yRot = 0.1F;
    } else {
      if (this.young) {
        this.head.setPos(0.0F, 14.0F, 0.0F);
        this.rightLeg.setPos(-1.9F, 8.0F, 4.0F);
        this.leftLeg.setPos(1.9F, 8.0F, 4.0F);
      } else {
        this.head.setPos(0.0F, 15.0F, 0.0F);
        this.rightLeg.setPos(-1.9F, 8.0F, 0.0F);
        this.leftLeg.setPos(1.9F, 8.0F, 0.0F);
      }
      this.leftLeg.yRot = 0.0F;
      this.leftLeg.xRot = 0.0F;
      this.rightLeg.xRot = 0.0F;
      this.rightLeg.yRot = 0.0F;
    }
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {
    if (!entity.isInSittingPose()) {
      float limbSwingAmountFraction = 1.4F * limbSwingAmount * 0.30F;
      this.head.xRot = headPitch * ((float) Math.PI / 225F);
      this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
      this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmountFraction;
      this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmountFraction;
    }
  }

}
