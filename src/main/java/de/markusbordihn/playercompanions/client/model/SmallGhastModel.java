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
import java.util.Random;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.TamableAnimal;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmallGhastModel<T extends TamableAnimal> extends AgeableListModel<T> {

  private final ModelPart body;
  private final ModelPart[] tentacles = new ModelPart[9];

  public SmallGhastModel(ModelPart modelPart) {
    this.body = modelPart.getChild("body");
    for (int i = 0; i < this.tentacles.length; ++i) {
      this.tentacles[i] = modelPart.getChild("tentacle" + i);
    }
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition partDefinition = meshDefinition.getRoot();

    // Body
    partDefinition.addOrReplaceChild("body",
        CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -30.0F, -8.0F, 16.0F, 16.0F, 16.0F),
        PartPose.offset(0.0F, 17.6F, 0.0F));
    Random random = new Random(1660L);

    // Adding tentacles.
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

  @Override
  public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {

    // Don't animate death entities
    if (entity.isDeadOrDying()) {
      return;
    }

    if (!entity.isInSittingPose()) {
      for (int i = 0; i < this.tentacles.length; ++i) {
        this.tentacles[i].xRot = 0.2F * Mth.sin(ageInTicks * 0.3F + i) + 0.4F;
      }
    }
  }

  @Override
  public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
    if (entity.isInSittingPose()) {
      this.body.setPos(0.0F, 34.0F, 0.0F);
      for (int i = 0; i < this.tentacles.length; ++i) {
        float f = (((i % 3) - (i / 3F % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
        float f1 = ((i / 3F) / 2.0F * 2.0F - 1.3F) * 5.0F;
        this.tentacles[i].setPos(f, 20.6F, f1 - 0.5F);
        this.tentacles[i].xRot = 1.40F;
      }
    } else {
      this.body.setPos(0.0F, 20.0F, 0.0F);
      for (int i = 0; i < this.tentacles.length; ++i) {
        float f = (((i % 3) - (i / 3F % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
        float f1 = ((i / 3F) / 2.0F * 2.0F - 1.3F) * 5.0F;
        this.tentacles[i].setPos(f, 2.6F, f1);
      }
    }
  }

  @Override
  protected Iterable<ModelPart> headParts() {
    return List.of();
  }

  @Override
  protected Iterable<ModelPart> bodyParts() {
    return List.of(this.body, this.tentacles[0], this.tentacles[1], this.tentacles[2],
        this.tentacles[3], this.tentacles[4], this.tentacles[5], this.tentacles[6],
        this.tentacles[7], this.tentacles[8]);
  }
}
