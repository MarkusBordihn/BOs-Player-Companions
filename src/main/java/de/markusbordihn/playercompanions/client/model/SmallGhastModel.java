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

import java.util.Random;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.entity.companions.SmallGhast;

@OnlyIn(Dist.CLIENT)
public class SmallGhastModel extends HierarchicalModel<SmallGhast> {

  private final ModelPart root;
  private final ModelPart[] tentacles = new ModelPart[9];

  public SmallGhastModel(ModelPart modelPart) {
    this.root = modelPart;

    for (int i = 0; i < this.tentacles.length; ++i) {
      this.tentacles[i] = modelPart.getChild("tentacle" + i);
    }
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();
    partDefinition.addOrReplaceChild("body",
        CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -30.0F, -8.0F, 16.0F, 16.0F, 16.0F),
        PartPose.offset(0.0F, 17.6F, 0.0F));
    Random random = new Random(1660L);

    for (int i = 0; i < 9; ++i) {
      float f = (((i % 3) - (i / 3F % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
      float f1 = ((i / 3F) / 2.0F * 2.0F - 1.3F) * 5.0F;
      int j = random.nextInt(7) + 8;
      partDefinition.addOrReplaceChild("tentacle" + i,
          CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, j, 2.0F),
          PartPose.offset(f, 2.6F, f1));
    }

    return LayerDefinition.create(meshDefinition, 64, 32);
  }

  public void setupAnim(
      SmallGhast entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {
    if (!entity.isInSittingPose()) {
      for (int i = 0; i < this.tentacles.length; ++i) {
        this.tentacles[i].xRot = 0.2F * Mth.sin(ageInTicks * 0.3F + i) + 0.4F;
      }
    }
  }

  public ModelPart root() {
    return this.root;
  }

}
