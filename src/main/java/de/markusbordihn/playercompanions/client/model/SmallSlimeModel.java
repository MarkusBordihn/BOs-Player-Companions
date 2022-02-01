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

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmallSlimeModel<T extends Entity> extends HierarchicalModel<T> {

  private final ModelPart root;
  private static final PartPose partPoseOffset = PartPose.offset(0.1F, 19.9F, -1.6F);

  public SmallSlimeModel(ModelPart modelPart) {
    this.root = modelPart;
  }

  public static LayerDefinition createOuterBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();
    partDefinition.addOrReplaceChild("shell",
        CubeListBuilder.create().texOffs(0, 0).addBox(-4.1F, -3.9F, -2.4F, 8.0F, 8.0F, 8.0F),
        partPoseOffset);
    return LayerDefinition.create(meshDefinition, 32, 32);
  }

  public static LayerDefinition createInnerBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();
    partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-3.1F,
        -2.9F, -1.4F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), partPoseOffset);
    partDefinition.addOrReplaceChild("right_eye",
        CubeListBuilder.create().texOffs(24, 20).addBox(1.25F, -1.9F, -1.9F, 2.0F, 2.0F, 2.0F),
        partPoseOffset);
    partDefinition.addOrReplaceChild("left_eye",
        CubeListBuilder.create().texOffs(24, 16).addBox(-3.25F, -1.9F, -1.9F, 2.0F, 2.0F, 2.0F),
        partPoseOffset);
    partDefinition.addOrReplaceChild("mouth",
        CubeListBuilder.create().texOffs(24, 24).addBox(0.0F, 1.0F, -1.9F, 1.0F, 1.0F, 1.0F),
        partPoseOffset);
    return LayerDefinition.create(meshDefinition, 32, 32);
  }

  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {
      }

  public ModelPart root() {
    return this.root;
  }

}
