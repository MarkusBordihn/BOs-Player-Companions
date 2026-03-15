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

import de.markusbordihn.easynpc.api.npc.base.PigBase;
import de.markusbordihn.easynpc.api.skin.VariantTexture;
import de.markusbordihn.easynpc.data.progression.ProgressionData;
import de.markusbordihn.easynpc.entity.easynpc.data.ProgressionDataCapable;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.CompanionBehaviorHandler;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.CompanionRelationship;
import de.markusbordihn.playercompanions.entity.CompanionRelationshipData;
import de.markusbordihn.playercompanions.entity.CompanionRole;
import de.markusbordihn.playercompanions.entity.behavior.CollectorBehavior;
import de.markusbordihn.playercompanions.entity.taming.TamingHintHandler;
import de.markusbordihn.playercompanions.network.CompanionEntityDataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class PigCompanion extends PigBase implements PlayerCompanion {

  private static final int INVENTORY_SIZE = 16;
  private static final String TAG_INVENTORY = "Inventory";
  private static final String TAG_COLLECTOR_ACTIVE = "CollectorActive";
  private static final EntityDataAccessor<CompanionRelationshipData> DATA_RELATIONSHIP =
    SynchedEntityData.defineId(PigCompanion.class,
      CompanionEntityDataSerializers.RELATIONSHIP_DATA);
  private static final EntityDataAccessor<CompanionCommand> DATA_COMMAND =
    SynchedEntityData.defineId(PigCompanion.class,
      CompanionEntityDataSerializers.COMPANION_COMMAND);
  private static final EntityDataAccessor<Boolean> DATA_COLLECTOR_ACTIVE =
    SynchedEntityData.defineId(PigCompanion.class, EntityDataSerializers.BOOLEAN);
  private final TamingHintHandler tamingHintHandler = new TamingHintHandler();
  private final SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE);
  private CompanionRelationship relationship;

  public PigCompanion(EntityType<? extends Pig> entityType, Level level) {
    this(entityType, level, Variant.DEFAULT);
  }

  public PigCompanion(EntityType<? extends Pig> entityType, Level level, Enum<?> variantType) {
    super(entityType, level, variantType);
    this.relationship = new CompanionRelationship(this, DATA_RELATIONSHIP);
    this.initCompanionAttributes();
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_RELATIONSHIP, CompanionRelationshipData.EMPTY);
    this.entityData.define(DATA_COMMAND, CompanionCommand.FOLLOW);
    this.entityData.define(DATA_COLLECTOR_ACTIVE, true);
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
    return PlayerCompanion.super.getSkinVariantType(name);
  }

  @Override
  public CompanionRelationship getRelationship() {
    return this.relationship;
  }

  @Override
  public String getCompanionTypeName() {
    return "pig";
  }

  @Override
  public TamingHintHandler getTamingHintHandler() {
    return this.tamingHintHandler;
  }

  public SimpleContainer getInventory() {
    return this.inventory;
  }

  @Override
  public boolean isCollectorActive() {
    return this.entityData.get(DATA_COLLECTOR_ACTIVE);
  }

  @Override
  public void setCollectorActive(boolean active) {
    this.entityData.set(DATA_COLLECTOR_ACTIVE, active);
  }

  @Override
  public CompanionRole getCompanionRole() {
    return CompanionRole.COLLECTOR;
  }

  @Override
  public SoundEvent getFeedingSound() {
    return SoundEvents.PIG_AMBIENT;
  }

  @Override
  public SoundEvent getPetSound() {
    return SoundEvents.PIG_AMBIENT;
  }

  @Override
  public int getEntityGuiScaling() {
    return 35;
  }

  @Override
  public int getEntityGuiTop() {
    return 13;
  }

  @Override
  public CompanionCommand getCompanionCommand() {
    return this.entityData.get(DATA_COMMAND);
  }

  @Override
  public void setCompanionCommand(CompanionCommand command) {
    this.entityData.set(DATA_COMMAND, command);
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

    // 10% chance to spawn as baby
    if (random.nextInt(10) == 0) {
      setBaby(true);
    }

    if (!isOwned()) {
      CompanionBehaviorHandler.initializeWildBehavior(this);
    }

    return spawnGroupData;
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    InteractionResult result = handleMobInteract(player, hand);
    return result != InteractionResult.PASS ? result : super.mobInteract(player, hand);
  }

  @Override
  public void die(DamageSource damageSource) {
    handleCompanionDeath(damageSource);
    super.die(damageSource);
  }

  @Override
  public boolean isInvulnerableTo(DamageSource damageSource) {
    return handleCompanionDamage(damageSource, super.isInvulnerableTo(damageSource));
  }

  @Override
  public void addAdditionalSaveData(CompoundTag tag) {
    super.addAdditionalSaveData(tag);
    saveCompanionData(tag);
    tag.put(TAG_INVENTORY, inventory.createTag());
    tag.putBoolean(TAG_COLLECTOR_ACTIVE, isCollectorActive());
  }

  @Override
  public void readAdditionalSaveData(CompoundTag tag) {
    super.readAdditionalSaveData(tag);
    loadCompanionData(tag);
    if (tag.contains(TAG_INVENTORY)) {
      inventory.fromTag(tag.getList(TAG_INVENTORY, 10));
    }
    if (tag.contains(TAG_COLLECTOR_ACTIVE)) {
      this.entityData.set(DATA_COLLECTOR_ACTIVE, tag.getBoolean(TAG_COLLECTOR_ACTIVE));
    }
  }

  @Override
  protected void dropEquipment() {
    super.dropEquipment();
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      ItemStack stack = inventory.getItem(i);
      if (!stack.isEmpty()) {
        spawnAtLocation(stack);
        inventory.setItem(i, ItemStack.EMPTY);
      }
    }
  }

  @Override
  public void tick() {
    super.tick();
    tickCompanion();
    if (isCollectorActive() && CollectorBehavior.tick(this, inventory)) {
      ((ProgressionDataCapable<?>) this).addExperience(TamingConfig.COLLECTOR_PICKUP_XP_AMOUNT);
    }
  }

  @Override
  public void onProgressLevelUp(ProgressionData oldData, ProgressionData newData) {
    super.onProgressLevelUp(oldData, newData);
    notifyOwnerLevelUp(newData.experienceLevel());
  }

  public enum Variant implements VariantTexture {
    DEFAULT(Constants.MOD_ID, "textures/entity/pig/pig_default.png"),
    SPOTTED(Constants.MOD_ID, "textures/entity/pig/pig_spotted.png");

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
