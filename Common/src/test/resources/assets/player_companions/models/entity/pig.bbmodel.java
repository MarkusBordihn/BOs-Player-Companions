// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class test<T extends Entity> extends EntityModel<T> {

  // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
    new ResourceLocation("modid", "test"), "main");
  private final ModelPart body;
  private final ModelPart bags;
  private final ModelPart head;
  private final ModelPart right_hind_leg;
  private final ModelPart left_hind_leg;
  private final ModelPart right_front_leg;
  private final ModelPart left_front_leg;

  public test(ModelPart root) {
    this.body = root.getChild("body");
    this.bags = this.body.getChild("bags");
    this.head = root.getChild("head");
    this.right_hind_leg = root.getChild("right_hind_leg");
    this.left_hind_leg = root.getChild("left_hind_leg");
    this.right_front_leg = root.getChild("right_front_leg");
    this.left_front_leg = root.getChild("left_front_leg");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition body_r1 = body.addOrReplaceChild("body_r1",
      CubeListBuilder.create().texOffs(26, 8)
        .addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(0.0F, -13.0F, 2.0F, 1.5708F, 0.0F, 0.0F));

    PartDefinition bags = body.addOrReplaceChild("bags", CubeListBuilder.create().texOffs(6, 20)
        .addBox(5.0F, -14.0F, -3.0F, 2.0F, 9.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(26, 32).addBox(-6.0F, -14.5F, -1.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(26, 32).addBox(-6.0F, -14.5F, -1.0F, 12.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 0.0F, 0.0F));

    PartDefinition right_bag_r1 = bags.addOrReplaceChild("right_bag_r1",
      CubeListBuilder.create().texOffs(6, 20)
        .addBox(-1.0F, -4.5F, -4.0F, 2.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(-6.0F, -9.5F, 1.0F, 0.0F, 3.1416F, 0.0F));

    PartDefinition head = partdefinition.addOrReplaceChild("head",
      CubeListBuilder.create().texOffs(0, 0)
        .addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 12.0F, -6.0F));

    PartDefinition eyebrown_left_r1 = head.addOrReplaceChild("eyebrown_left_r1",
      CubeListBuilder.create().texOffs(0, 0)
        .addBox(-1.025F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(2.975F, -1.975F, -8.0F, 0.0F, 0.0F, -0.0873F));

    PartDefinition eyebrown_right_r1 = head.addOrReplaceChild("eyebrown_right_r1",
      CubeListBuilder.create().texOffs(0, 0)
        .addBox(-1.9F, 0.025F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(-2.0F, -2.5F, -8.0F, 0.0F, 0.0F, 0.0873F));

    PartDefinition right_hind_leg = partdefinition.addOrReplaceChild("right_hind_leg",
      CubeListBuilder.create().texOffs(0, 17)
        .addBox(-5.0F, -6.0F, 4.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition left_hind_leg = partdefinition.addOrReplaceChild("left_hind_leg",
      CubeListBuilder.create().texOffs(0, 17).mirror()
        .addBox(2.0F, -6.0F, 4.0F, 3.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition right_front_leg = partdefinition.addOrReplaceChild("right_front_leg",
      CubeListBuilder.create().texOffs(0, 16)
        .addBox(-4.998F, -7.0F, -7.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition left_front_leg = partdefinition.addOrReplaceChild("left_front_leg",
      CubeListBuilder.create().texOffs(0, 16).mirror()
        .addBox(1.998F, -7.0F, -7.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    return LayerDefinition.create(meshdefinition, 64, 64);
  }

  @Override
  public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
    float netHeadYaw, float headPitch) {

  }

  @Override
  public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight,
    int packedOverlay, float red, float green, float blue, float alpha) {
    body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    right_hind_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    left_hind_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    right_front_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    left_front_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
  }
}
