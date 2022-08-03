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

import com.google.common.collect.ImmutableList;

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
public class RaptorModel<T extends TamableAnimal> extends AgeableListModel<T> {

  private final ModelPart body;
  private final ModelPart bodyNeck;
  private final ModelPart head;
  private final ModelPart headBottom;
  private final ModelPart headTop;
  private final ModelPart leftArm;
  private final ModelPart leftLeg;
  private final ModelPart leftLegClaw;
  private final ModelPart rightArm;
  private final ModelPart rightLeg;
  private final ModelPart rightLegClaw;
  private final ModelPart tail;
  private final ModelPart tailEnd;
  private final ModelPart tailMiddle;

  public RaptorModel(ModelPart root) {
    this.body = root.getChild("body");
    this.bodyNeck = this.body.getChild("neck");
    this.head = root.getChild("head");
    this.headBottom = this.head.getChild("head_bottom");
    this.headTop = this.head.getChild("head_top");
    this.leftArm = root.getChild("left_arm");
    this.leftLeg = root.getChild("left_leg");
    this.leftLegClaw = this.leftLeg.getChild("left_claw");
    this.rightArm = root.getChild("right_arm");
    this.rightLeg = root.getChild("right_leg");
    this.rightLegClaw = this.rightLeg.getChild("right_claw");
    this.tail = this.body.getChild("tail");
    this.tailMiddle = this.tail.getChild("tail_middle");
    this.tailEnd = this.tailMiddle.getChild("tail_end");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    PartDefinition head = partDefinition.addOrReplaceChild("head", CubeListBuilder.create(),
        PartPose.offset(0.0F, 2.75F, -14.4F));

    head.addOrReplaceChild("head_top", CubeListBuilder.create().texOffs(38, 0)
        .addBox(-1.5F, -1.5F, -8.425F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.2F)).texOffs(0, 35)
        .addBox(-2.0F, -1.5F, -4.075F, 4.0F, 3.0F, 5.0F, new CubeDeformation(0.2F)).texOffs(0, 27)
        .addBox(-1.5F, 1.5F, -8.425F, 3.0F, 1.0F, 7.0F, new CubeDeformation(-0.21F)),
        PartPose.offset(0.0F, 0.275F, -1.025F));

    head.addOrReplaceChild("head_bottom", CubeListBuilder.create().texOffs(46, 7)
        .addBox(-1.0F, -0.501F, -7.7625F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.3F))
        .texOffs(0, 43).addBox(-1.5F, -0.4F, -3.0625F, 3.0F, 1.0F, 4.0F, new CubeDeformation(0.4F))
        .texOffs(35, 11).addBox(-1.0F, -1.6F, -7.6375F, 2.0F, 1.0F, 7.0F,
            new CubeDeformation(-0.2F)),
        PartPose.offset(0.0F, 2.75F, -1.5875F));

    PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
        .texOffs(20, 15).addBox(-2.5F, -15.0F, -9.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.2F))
        .texOffs(0, 0).addBox(-2.5F, -15.0F, -5.0F, 5.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(0, 15).addBox(-2.0F, -14.75F, 2.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create(),
        PartPose.offset(0.0F, -15.025F, -9.35F));

    neck.addOrReplaceChild("neck_r1",
        CubeListBuilder.create().texOffs(18, 0).addBox(-2.0F, -2.225F, -0.5F, 4.0F, 4.0F, 4.0F,
            new CubeDeformation(-0.201F)),
        PartPose.offsetAndRotation(0.0F, 5.025F, -0.15F, 0.7854F, 0.0F, 0.0F));

    neck.addOrReplaceChild("neck_r2",
        CubeListBuilder.create().texOffs(34, 33).addBox(-2.0F, -3.975F, -3.3F, 4.0F, 7.0F, 4.0F,
            new CubeDeformation(-0.2F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

    neck.addOrReplaceChild("neck_r3",
        CubeListBuilder.create().texOffs(36, 23).addBox(-2.0F, -4.925F, -2.225F, 4.0F, 6.0F, 4.0F,
            new CubeDeformation(-0.201F)),
        PartPose.offsetAndRotation(0.0F, -3.1749F, -2.7222F, 0.7854F, 0.0F, 0.0F));

    PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, 27)
        .addBox(-1.5F, -1.5F, 0.0F, 3.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, -13.0F, 7.5F));

    PartDefinition tailMiddle = tail.addOrReplaceChild("tail_middle", CubeListBuilder.create()
        .texOffs(26, 0).addBox(-1.0F, -1.25F, 0.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 0.0F, 5.0F));

    tailMiddle.addOrReplaceChild("tail_end", CubeListBuilder.create().texOffs(11, 36).addBox(-0.5F,
        -1.0F, 0.0F, 1.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 0.0F, 7.0F));

    PartDefinition leftArm = partDefinition.addOrReplaceChild("left_arm",
        CubeListBuilder.create().texOffs(11, 45)
            .addBox(-1.0F, -1.6667F, -1.9333F, 2.0F, 5.0F, 3.0F, new CubeDeformation(-0.3F))
            .texOffs(13, 27)
            .addBox(-1.0F, 1.3333F, -5.3333F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F)),
        PartPose.offsetAndRotation(2.75F, 15.4167F, -6.0667F, 0.3927F, 0.0F, 0.0F));

    leftArm.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(26, 11)
        .addBox(1.75F, -7.25F, -11.8F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)).texOffs(0, 15)
        .addBox(1.75F, -7.25F, -12.2F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)),
        PartPose.offset(-2.75F, 8.5833F, 6.0667F));

    PartDefinition rightArm = partDefinition.addOrReplaceChild("right_arm",
        CubeListBuilder.create().texOffs(13, 27).mirror()
            .addBox(-1.0F, 1.3333F, -5.3333F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F))
            .mirror(false).texOffs(11, 45).mirror()
            .addBox(-1.0F, -1.6667F, -1.9333F, 2.0F, 5.0F, 3.0F, new CubeDeformation(-0.3F))
            .mirror(false),
        PartPose.offsetAndRotation(-2.75F, 15.4167F, -6.0667F, 0.3927F, 0.0F, 0.0F));

    rightArm.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(26, 11).mirror()
        .addBox(-3.75F, -7.25F, -11.8F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false)
        .texOffs(0, 15).mirror()
        .addBox(-3.75F, -7.25F, -12.2F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.3F)).mirror(false),
        PartPose.offset(2.75F, 8.5833F, 6.0667F));

    PartDefinition rightLeg = partDefinition.addOrReplaceChild("right_leg",
        CubeListBuilder.create().texOffs(27, 41).mirror()
            .addBox(-1.0F, -1.4932F, -1.7312F, 2.0F, 8.0F, 3.0F, new CubeDeformation(-0.1F))
            .mirror(false).texOffs(14, 15).mirror()
            .addBox(-1.0F, 10.2068F, -2.2562F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .mirror(false),
        PartPose.offset(-2.5F, 12.7932F, 0.2562F));

    rightLeg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(0, 0).mirror()
        .addBox(-1.0F, -2.5F, -0.5F, 2.0F, 5.0F, 2.0F, new CubeDeformation(-0.3F)).mirror(false),
        PartPose.offsetAndRotation(0.0F, 8.7068F, -0.0062F, -0.3927F, 0.0F, 0.0F));

    rightLeg.addOrReplaceChild("right_leg_r2", CubeListBuilder.create().texOffs(37, 44).mirror()
        .addBox(-1.0F, -2.0F, -1.625F, 2.0F, 3.0F, 4.0F, new CubeDeformation(-0.2F)).mirror(false),
        PartPose.offsetAndRotation(0.0F, 6.2068F, -0.0062F, -0.3927F, 0.0F, 0.0F));

    PartDefinition rightClaw = rightLeg.addOrReplaceChild("right_claw",
        CubeListBuilder.create().texOffs(18, 0).mirror()
            .addBox(-0.5F, -1.3295F, -0.4692F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)).mirror(
                false),
        PartPose.offsetAndRotation(1.0F, 10.0134F, -0.9849F, 0.3927F, 0.0F, 0.0F));

    rightClaw.addOrReplaceChild("right_claw_top_r1",
        CubeListBuilder.create().texOffs(0, 27).mirror()
            .addBox(-0.5F, -0.312F, -0.8371F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.101F))
            .mirror(false),
        PartPose.offsetAndRotation(0.0F, -1.8306F, -0.4692F, -0.7854F, 0.0F, 0.0F));

    PartDefinition leftLeg = partDefinition.addOrReplaceChild("left_leg",
        CubeListBuilder.create().texOffs(27, 41)
            .addBox(-1.0F, -1.4932F, -1.7312F, 2.0F, 8.0F, 3.0F, new CubeDeformation(-0.1F))
            .texOffs(14, 15).addBox(-1.0F, 10.2068F, -2.2562F, 2.0F, 1.0F, 3.0F,
                new CubeDeformation(0.0F)),
        PartPose.offset(2.5F, 12.7932F, 0.2562F));

    leftLeg.addOrReplaceChild("left_leg_r1",
        CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.5F, -0.5F, 2.0F, 5.0F, 2.0F,
            new CubeDeformation(-0.3F)),
        PartPose.offsetAndRotation(0.0F, 8.7068F, -0.0062F, -0.3927F, 0.0F, 0.0F));

    leftLeg.addOrReplaceChild("left_leg_r2",
        CubeListBuilder.create().texOffs(37, 44).addBox(-1.0F, -2.0F, -1.625F, 2.0F, 3.0F, 4.0F,
            new CubeDeformation(-0.2F)),
        PartPose.offsetAndRotation(0.0F, 6.2068F, -0.0062F, -0.3927F, 0.0F, 0.0F));

    PartDefinition leftClaw = leftLeg.addOrReplaceChild("left_claw",
        CubeListBuilder.create().texOffs(18, 0).addBox(-0.5F, -1.3295F, -0.4692F, 1.0F, 2.0F, 1.0F,
            new CubeDeformation(-0.1F)),
        PartPose.offsetAndRotation(-1.0F, 10.0134F, -0.9849F, 0.3927F, 0.0F, 0.0F));

    leftClaw.addOrReplaceChild("left_claw_top_r1",
        CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, -0.312F, -0.8371F, 1.0F, 1.0F, 2.0F,
            new CubeDeformation(-0.101F)),
        PartPose.offsetAndRotation(0.0F, -1.8306F, -0.4692F, -0.7854F, 0.0F, 0.0F));

    return LayerDefinition.create(meshDefinition, 64, 64);
  }

  @Override
  protected Iterable<ModelPart> headParts() {
    return ImmutableList.of(this.head);
  }

  @Override
  protected Iterable<ModelPart> bodyParts() {
    return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg);
  }

  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
    if (entity.isInSittingPose()) {

      // Position y: -8

      // Body
      this.body.setPos(0.0F, 32.0F, 0.0F);
      this.bodyNeck.xRot = 0.4f;

      // Head
      if (this.young) {
        this.head.setPos(0.0F, 18.25F, -10.0F);
      } else {
        this.head.setPos(0.0F, 12.75F, -15.4F);
      }
      this.head.xRot = -0.4f;
      this.headBottom.xRot = 0f;

      // Arms
      this.rightArm.setPos(-2.75F, 23.4167F, -6.0667F);
      this.rightArm.xRot = -1.28F;
      this.leftArm.setPos(2.75F, 23.4167F, -6.0667F);
      this.leftArm.xRot = -1.28F;

      // Legs
      this.rightLeg.setPos(-2.5F, 20.7932F, 0.2562F);
      this.rightLeg.xRot = 1.58F;
      this.leftLeg.setPos(2.5F, 20.7932F, 0.2562F);
      this.leftLeg.xRot = 1.58F;

    } else {

      // Body
      this.body.setPos(0.0F, 24.0F, 0.0F);
      this.bodyNeck.xRot = 0f;

      // Head
      if (this.young) {
        this.head.setPos(0.0F, 8.25F, -9.0F);
      } else {
        this.head.setPos(0.0F, 2.75F, -14.4F);
      }
      this.head.xRot = 0f;

      // Arms
      if (limbSwingAmount > 0.01) {
        this.rightArm.xRot =
            Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount * 0.5F;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount * 0.5F;
      } else {
        this.rightArm.setPos(-2.75F, 15.4167F, -6.0667F);
        this.rightArm.setRotation(0.1927F, 0.0F, 0.0F);
        this.leftArm.setPos(2.75F, 15.4167F, -6.0667F);
        this.leftArm.setRotation(-0.3927F, 0.0F, 0.0F);
      }

      // Legs
      this.rightLeg.setPos(-2.5F, 12.7932F, 0.2562F);
      this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;

      this.leftLeg.setPos(2.5F, 12.7932F, 0.2562F);
      this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
    }
  }

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {

    // Don't animate death entities
    if (entity.isDeadOrDying()) {
      return;
    }

    // Limiting movements in sitting pose
    if (!entity.isInSittingPose()) {
      // Claws
      this.rightLegClaw.xRot = headPitch * ((float) Math.PI / 180F) * -7.5f;
      this.leftLegClaw.xRot = netHeadYaw * ((float) Math.PI / 180F) * -0.5f;
    }

    // Head
    this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
    this.head.xRot = headPitch * ((float) Math.PI / 180F) * -1;
    float headBottomRotation = netHeadYaw * ((float) Math.PI / 180F);
    if (headBottomRotation >= 0) {
      this.headBottom.xRot = headBottomRotation * (entity.getTarget() == null ? 0.75f : 0.25f);
    }
    this.headTop.xRot = headPitch * ((float) Math.PI / 360F) * 0.5f;

    // Tail
    float tailSwing = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount * 0.75f;
    this.tail.xRot = tailSwing * 0.5f;
    this.tail.yRot = tailSwing;
    this.tailMiddle.yRot = tailSwing;
    this.tailEnd.yRot = tailSwing;
    if (this.tail.xRot == 0) {
      float tailSwingAlternative = netHeadYaw * ((float) Math.PI / 180F) * 0.5f;
      this.tail.yRot = tailSwingAlternative;
      this.tailMiddle.yRot = tailSwingAlternative;
      this.tailEnd.yRot = tailSwingAlternative;
    }
  }

}
