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

import de.markusbordihn.easynpc.api.npc.base.slime.SlimeSmallBase;
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
import de.markusbordihn.playercompanions.entity.taming.TamingHintHandler;
import de.markusbordihn.playercompanions.network.CompanionEntityDataSerializers;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

/**
 * A small slime companion that follows its owner, warns about nearby threats, and gains XP over
 * time.
 */
public class SmallSlimeCompanion extends SlimeSmallBase implements PlayerCompanion {

  private static final EntityDataAccessor<CompanionRelationshipData> DATA_RELATIONSHIP =
    SynchedEntityData.defineId(SmallSlimeCompanion.class,
      CompanionEntityDataSerializers.RELATIONSHIP_DATA);
  private static final EntityDataAccessor<CompanionCommand> DATA_COMMAND =
    SynchedEntityData.defineId(SmallSlimeCompanion.class,
      CompanionEntityDataSerializers.COMPANION_COMMAND);
  private static final int OWNER_PROXIMITY_XP_INTERVAL = TamingConfig.FOLLOWER_PROXIMITY_XP_INTERVAL;
  private static final int FOLLOWER_WARNING_INTERVAL = TamingConfig.FOLLOWER_WARNING_INTERVAL;
  private final TamingHintHandler tamingHintHandler = new TamingHintHandler();
  private final CompanionRelationship relationship;
  private final Set<UUID> warnedThreats = new HashSet<>();
  private int ownerProximityTicker;
  private int followerWarningTicker;

  public SmallSlimeCompanion(EntityType<? extends Slime> entityType, Level level) {
    this(entityType, level, Variant.GREEN);
  }

  public SmallSlimeCompanion(
    EntityType<? extends Slime> entityType, Level level, Enum<?> variantType) {
    super(entityType, level, variantType);
    this.relationship = new CompanionRelationship(this, DATA_RELATIONSHIP);
    this.initCompanionAttributes();
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_RELATIONSHIP, CompanionRelationshipData.EMPTY);
    this.entityData.define(DATA_COMMAND, CompanionCommand.FOLLOW);
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
    return Variant.GREEN;
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
    return "small_slime";
  }

  @Override
  public TamingHintHandler getTamingHintHandler() {
    return this.tamingHintHandler;
  }

  @Override
  public CompanionRole getCompanionRole() {
    return CompanionRole.FOLLOWER;
  }

  @Override
  public SoundEvent getFeedingSound() {
    return SoundEvents.SLIME_SQUISH_SMALL;
  }

  @Override
  public SoundEvent getPetSound() {
    return SoundEvents.SLIME_SQUISH;
  }

  @Override
  public int getEntityGuiScaling() {
    return 60;
  }

  @Override
  public int getEntityGuiTop() {
    return 8;
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
    this.setPos(this.getX(), this.getY() + 0.01D, this.getZ());
    if (random.nextInt(2) == 0) {
      Variant[] variants = Variant.values();
      setSkinVariantType(variants[random.nextInt(variants.length)]);
    }

    if (!isOwned()) {
      CompanionBehaviorHandler.initializeWildBehavior(this);
    }

    return spawnGroupData;
  }

  @Override
  protected boolean spawnCustomParticles() {
    return true;
  }

  @Override
  public boolean shouldRenderAtSqrDistance(double distance) {
    return distance < 64.0 * 64.0;
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
    if (!level().isClientSide && isOwned()) {
      if (++ownerProximityTicker >= OWNER_PROXIMITY_XP_INTERVAL) {
        ownerProximityTicker = 0;
        grantOwnerProximityXp();
      }
      if (++followerWarningTicker >= FOLLOWER_WARNING_INTERVAL) {
        followerWarningTicker = 0;
        checkForNearbyThreats();
      }
    }
  }

  @Override
  public void onProgressLevelUp(ProgressionData oldData, ProgressionData newData) {
    super.onProgressLevelUp(oldData, newData);
    notifyOwnerLevelUp(newData.experienceLevel());
  }

  /**
   * Awards 1 XP to this companion when its owner is within 8 blocks, checked every 5 minutes.
   */
  private void grantOwnerProximityXp() {
    getOnlineOwner().ifPresent(owner -> {
      if (this.distanceTo(owner) <= TamingConfig.FOLLOWER_PROXIMITY_XP_RANGE) {
        ((ProgressionDataCapable<?>) this).addExperience(TamingConfig.FOLLOWER_PROXIMITY_XP_AMOUNT);
      }
    });
  }

  /**
   * Scans for nearby monsters and sends a chat warning to the owner for each new threat found.
   */
  private void checkForNearbyThreats() {
    getOnlineOwner().ifPresent(owner -> {
      warnedThreats.removeIf(uuid -> {
        var entity = ((ServerLevel) level()).getEntity(uuid);
        return entity == null || !entity.isAlive();
      });
      AABB searchBox = this.getBoundingBox().inflate(TamingConfig.FOLLOWER_THREAT_RADIUS);
      List<Monster> threats = level().getEntitiesOfClass(Monster.class, searchBox,
        monster -> monster.isAlive() && !warnedThreats.contains(monster.getUUID()));
      for (Monster monster : threats) {
        warnedThreats.add(monster.getUUID());
        owner.sendSystemMessage(
          Component.translatable(
              "playercompanions.follower.threat_warning",
              this.getDisplayName(),
              monster.getType().getDescription())
            .withStyle(ChatFormatting.YELLOW));
      }
    });
  }

  public enum Variant implements VariantTexture {
    BLACK(Constants.MOD_ID, "textures/entity/small_slime/small_slime_black.png"),
    BLUE(Constants.MOD_ID, "textures/entity/small_slime/small_slime_blue.png"),
    BROWN(Constants.MOD_ID, "textures/entity/small_slime/small_slime_brown.png"),
    CYAN(Constants.MOD_ID, "textures/entity/small_slime/small_slime_cyan.png"),
    GRAY(Constants.MOD_ID, "textures/entity/small_slime/small_slime_gray.png"),
    GREEN(Constants.MOD_ID, "textures/entity/small_slime/small_slime_green.png"),
    LIGHT_BLUE(Constants.MOD_ID, "textures/entity/small_slime/small_slime_light_blue.png"),
    LIGHT_GRAY(Constants.MOD_ID, "textures/entity/small_slime/small_slime_light_gray.png"),
    LIME(Constants.MOD_ID, "textures/entity/small_slime/small_slime_lime.png"),
    MAGENTA(Constants.MOD_ID, "textures/entity/small_slime/small_slime_magenta.png"),
    ORANGE(Constants.MOD_ID, "textures/entity/small_slime/small_slime_orange.png"),
    PINK(Constants.MOD_ID, "textures/entity/small_slime/small_slime_pink.png"),
    PURPLE(Constants.MOD_ID, "textures/entity/small_slime/small_slime_purple.png"),
    RED(Constants.MOD_ID, "textures/entity/small_slime/small_slime_red.png"),
    WHITE(Constants.MOD_ID, "textures/entity/small_slime/small_slime_white.png"),
    YELLOW(Constants.MOD_ID, "textures/entity/small_slime/small_slime_yellow.png");

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
