/*
 * Copyright 2026 Markus Bordihn
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

package de.markusbordihn.playercompanions.entity.companion;

import de.markusbordihn.easynpc.api.npc.base.ChickenBase;
import de.markusbordihn.easynpc.api.skin.VariantTexture;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.CompanionRelationship;
import de.markusbordihn.playercompanions.entity.CompanionRelationshipData;
import de.markusbordihn.playercompanions.entity.CompanionRole;
import de.markusbordihn.playercompanions.entity.taming.TamingHintHandler;
import de.markusbordihn.playercompanions.network.CompanionEntityDataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class RoosterCompanion extends ChickenBase implements PlayerCompanion {

  private static final EntityDataAccessor<CompanionRelationshipData> DATA_RELATIONSHIP =
    SynchedEntityData.defineId(RoosterCompanion.class,
      CompanionEntityDataSerializers.RELATIONSHIP_DATA);
  private final TamingHintHandler tamingHintHandler = new TamingHintHandler();
  private CompanionRelationship relationship;
  private CompanionCommand companionCommand = CompanionCommand.FOLLOW;

  public RoosterCompanion(EntityType<? extends Chicken> entityType, Level level) {
    this(entityType, level, Variant.DEFAULT);
  }

  public RoosterCompanion(EntityType<? extends Chicken> entityType, Level level,
    Enum<?> variantType) {
    super(entityType, level, variantType);
    this.relationship = new CompanionRelationship(this, DATA_RELATIONSHIP);
    this.initCompanionAttributes();
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_RELATIONSHIP, CompanionRelationshipData.EMPTY);
  }

  @Override
  public RandomSource getRandom() {
    return random;
  }

  @Override
  public Enum<?>[] getSkinVariantTypes() {
    return Variant.values();
  }

  @Override
  public Enum<?> getDefaultSkinVariantType() {
    return Variant.DEFAULT;
  }

  @Override
  public Enum<?> getSkinVariantType(String name) {
    if (name == null || name.isEmpty()) {
      return Variant.DEFAULT;
    }
    try {
      return Variant.valueOf(name);
    } catch (IllegalArgumentException e) {
      return Variant.DEFAULT;
    }
  }

  @Override
  public CompanionRelationship getRelationship() {
    return this.relationship;
  }

  @Override
  public String getCompanionTypeName() {
    return "rooster";
  }

  @Override
  public TamingHintHandler getTamingHintHandler() {
    return this.tamingHintHandler;
  }

  @Override
  public CompanionRole getCompanionRole() {
    return CompanionRole.GUARD;
  }

  @Override
  public CompanionCommand getCompanionCommand() {
    return this.companionCommand;
  }

  @Override
  public void setCompanionCommand(CompanionCommand command) {
    this.companionCommand = command;
  }

  @Override
  public SpawnGroupData finalizeSpawn(
    ServerLevelAccessor level,
    DifficultyInstance difficulty,
    MobSpawnType spawnType,
    SpawnGroupData spawnGroupData,
    CompoundTag compoundTag) {
    spawnGroupData =
      super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData, compoundTag);

    if (random.nextInt(2) == 0) {
      Enum<?>[] variants = Variant.values();
      if (variants.length > 0) {
        setSkinVariantType(variants[random.nextInt(variants.length)]);
      }
    }

    return spawnGroupData;
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    InteractionResult tamingResult = handleCompanionInteraction(player, hand);
    if (tamingResult.consumesAction()) {
      return tamingResult;
    }
    return super.mobInteract(player, hand);
  }

  @Override
  public boolean isInvulnerableTo(DamageSource damageSource) {
    return handleDamage(damageSource, super.isInvulnerableTo(damageSource));
  }

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    saveCompanionData(tag);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    loadCompanionData(tag);
  }

  @Override
  public void tick() {
    super.tick();
    tickCompanion();
  }

  public enum Variant implements VariantTexture {
    DEFAULT(Constants.MOD_ID, "textures/entity/rooster/rooster_default.png"),
    MIXED(Constants.MOD_ID, "textures/entity/rooster/rooster_mixed.png");

    private final ResourceLocation textureLocation;

    Variant(String namespace, String path) {
      this.textureLocation = new ResourceLocation(namespace, path);
    }

    @Override
    public ResourceLocation getTextureLocation() {
      return this.textureLocation;
    }
  }
}
