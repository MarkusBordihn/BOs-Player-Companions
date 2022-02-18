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

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.entity.collector.Snail;

@OnlyIn(Dist.CLIENT)
public class SnailModel extends HierarchicalModel<Snail> {

  private final ModelPart root;
  private final ModelPart body;
  private final ModelPart eyes;
  private final ModelPart house;

  public SnailModel(ModelPart modelPart) {
    this.root = modelPart;
    this.body = root.getChild("body");
    this.eyes = root.getChild("eyes");
    this.house = root.getChild("house");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create()
        .texOffs(32, 15).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 3.0F, 12.0F, new CubeDeformation(0.0F))
        .texOffs(24, 0).addBox(-3.0F, -1.0F, -7.0F, 6.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition eyes = partDefinition.addOrReplaceChild("eyes", CubeListBuilder.create(),
        PartPose.offset(0.0F, 15.75F, -4.5F));

    PartDefinition rightEye = eyes.addOrReplaceChild("right_eye",
        CubeListBuilder.create().texOffs(0, 0)
            .addBox(-2.5F, -2.75F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 4)
            .addBox(-2.0F, -0.75F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 0.0F, 0.0F));

    PartDefinition leftEye = eyes.addOrReplaceChild("left_eye",
        CubeListBuilder.create().texOffs(0, 0)
            .addBox(0.5F, -2.75F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 4)
            .addBox(1.0F, -0.75F, -0.5F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 0.0F, 0.0F));

    PartDefinition house = partDefinition.addOrReplaceChild("house", CubeListBuilder.create(),
        PartPose.offset(0.0F, 16.0F, 3.0F));

    PartDefinition house_r1 = house.addOrReplaceChild("house_r1",
        CubeListBuilder.create().texOffs(0, 6).addBox(-3.0F, -4.5F, -4.25F, 6.0F, 10.0F, 10.0F,
            new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

    return LayerDefinition.create(meshDefinition, 64, 32);
  }

  public void setupAnim(Snail entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {}

  public ModelPart root() {
    return this.root;
  }

}
