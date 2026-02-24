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
  private final ModelPart tail;
  private final ModelPart head;
  private final ModelPart right_leg;
  private final ModelPart left_leg;
  private final ModelPart right_wing;
  private final ModelPart left_wing;
  private final ModelPart right_hand;
  private final ModelPart left_hand;
  private final ModelPart beak;
  private final ModelPart red_thing;

  public test(ModelPart root) {
    this.body = root.getChild("body");
    this.tail = this.body.getChild("tail");
    this.head = root.getChild("head");
    this.right_leg = root.getChild("right_leg");
    this.left_leg = root.getChild("left_leg");
    this.right_wing = root.getChild("right_wing");
    this.left_wing = root.getChild("left_wing");
    this.right_hand = root.getChild("right_hand");
    this.left_hand = root.getChild("left_hand");
    this.beak = root.getChild("beak");
    this.red_thing = root.getChild("red_thing");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition body = partdefinition.addOrReplaceChild("body",
      CubeListBuilder.create().texOffs(24, 0)
        .addBox(-3.0F, -3.0F, -6.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 16.0F, 0.0F));

    PartDefinition body_r1 = body.addOrReplaceChild("body_r1",
      CubeListBuilder.create().texOffs(10, 14)
        .addBox(-2.0F, 4.0F, -2.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(24, 7).addBox(-2.0F, 4.0F, 0.0F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(0, 27).addBox(-3.0F, -2.0F, -5.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(18, 24).addBox(-3.0F, -3.0F, -4.0F, 6.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(0, 0).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

    PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create(),
      PartPose.offset(0.0F, 8.0F, 0.0F));

    PartDefinition tail_r1 = tail.addOrReplaceChild("tail_r1",
      CubeListBuilder.create().texOffs(18, 0)
        .addBox(-1.0F, -4.5F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(11, 29).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(0.0F, -12.5F, 6.5F, -0.3927F, 0.0F, 0.0F));

    PartDefinition tail_r2 = tail.addOrReplaceChild("tail_r2",
      CubeListBuilder.create().texOffs(0, 0)
        .addBox(-1.0F, -0.75F, 0.075F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(0.0F, -15.0F, 8.5F, -0.7854F, 0.0F, 0.0F));

    PartDefinition tail_r3 = tail.addOrReplaceChild("tail_r3",
      CubeListBuilder.create().texOffs(10, 18)
        .addBox(-1.0F, -1.0F, -1.05F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(0.0F, -15.1924F, 6.5328F, 0.3927F, 0.0F, 0.0F));

    PartDefinition head = partdefinition.addOrReplaceChild("head",
      CubeListBuilder.create().texOffs(18, 14)
        .addBox(-2.002F, -6.0F, -2.002F, 4.004F, 6.0F, 4.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 14.0F, -4.0F));

    PartDefinition feather_r1 = head.addOrReplaceChild("feather_r1",
      CubeListBuilder.create().texOffs(30, 14)
        .addBox(-0.5F, -1.5F, 0.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(21, 30).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(0.0F, -7.5F, -0.5F, -0.3927F, 0.0F, 0.0F));

    PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg",
      CubeListBuilder.create().texOffs(29, 27)
        .addBox(-3.0F, -3.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg",
      CubeListBuilder.create().texOffs(29, 27)
        .addBox(0.0F, -3.0F, -2.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing",
      CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition right_wing_r1 = right_wing.addOrReplaceChild("right_wing_r1",
      CubeListBuilder.create().texOffs(0, 14)
        .addBox(-0.5F, -2.5F, -4.0F, 1.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
      PartPose.offsetAndRotation(-3.5F, -8.5F, 1.0F, 3.1416F, 3.1416F, 0.0F));

    PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing",
      CubeListBuilder.create().texOffs(0, 14)
        .addBox(3.0F, -11.0F, -3.0F, 1.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition right_hand = partdefinition.addOrReplaceChild("right_hand",
      CubeListBuilder.create(), PartPose.offset(-4.0F, 15.0F, 0.0F));

    PartDefinition left_hand = partdefinition.addOrReplaceChild("left_hand",
      CubeListBuilder.create(), PartPose.offset(4.0F, 15.0F, 0.0F));

    PartDefinition beak = partdefinition.addOrReplaceChild("beak",
      CubeListBuilder.create().texOffs(0, 14)
        .addBox(-1.0F, -14.0F, -8.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
      PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition red_thing = partdefinition.addOrReplaceChild("red_thing",
      CubeListBuilder.create().texOffs(0, 18)
        .addBox(-1.0F, -12.0F, -7.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
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
    right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    right_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    left_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    right_hand.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    left_hand.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
    beak.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    red_thing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue,
      alpha);
  }
}
