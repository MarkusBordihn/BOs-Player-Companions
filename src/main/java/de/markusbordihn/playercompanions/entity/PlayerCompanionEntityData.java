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

package de.markusbordihn.playercompanions.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.textures.ModTextureManager;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsDataSync;
import de.markusbordihn.playercompanions.utils.PlayersUtils;

@EventBusSubscriber
public class PlayerCompanionEntityData extends TamableAnimal
    implements PlayerCompanionsDataSync, PlayerCompanionExperience, PlayerCompanionAttributes {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Config values
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static int maxHealth = COMMON.maxHealth.get();
  private static int maxAttackDamage = COMMON.maxAttackDamage.get();

  // Synced Entity Data
  private static final EntityDataAccessor<Boolean> DATA_ACTIVE =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE_LEVEL =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_RESPAWN_TIMER =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<String> DATA_CUSTOM_TEXTURE_SKIN =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.STRING);
  private static final EntityDataAccessor<String> DATA_VARIANT =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.STRING);
  protected static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
      SynchedEntityData.defineId(PlayerCompanionEntityData.class, EntityDataSerializers.INT);

  // Stored Entity Data Tags
  private static final String DATA_ACTIVE_TAG = "Active";
  private static final String DATA_CUSTOM_TEXTURE_SKIN_TAG = "CustomTextureSkin";
  private static final String DATA_EXPERIENCE_LEVEL_TAG = "CompanionExperienceLevel";
  private static final String DATA_EXPERIENCE_TAG = "CompanionExperience";
  private static final String DATA_RESPAWN_TIMER_TAG = "CompanionRespawnTicker";
  private static final String DATA_VARIANT_TAG = "Variant";

  // Attribute Names
  private static final String ATTRIBUTE_MAX_HEALTH = "Player Companions Max Health";
  private static final String ATTRIBUTE_ATTACK_DAMAGE = "Player Companions Attack Damage";

  // Default values
  private int explosionPower = 0;
  private static final int JUMP_MOVE_DELAY = 10;
  protected static final int RIDE_COOLDOWN = 600;

  // Variants
  private Map<PlayerCompanionVariant, ResourceLocation> textureByVariant;
  private Map<PlayerCompanionVariant, Item> companionItemByVariant;

  // Cache
  private ResourceLocation textureCache = null;
  private ResourceLocation textureCustomCache = null;
  private PlayerCompanionVariant textureCustomCacheVariant = PlayerCompanionVariant.NONE;
  private String lastCustomTextureSkin = null;

  // Temporary stats
  private ActionType actionType = ActionType.UNKNOWN;
  private AggressionLevel aggressionLevel = AggressionLevel.UNKNOWN;
  private BlockPos orderedToPosition = null;
  private String dimensionName = "";
  private boolean isDataSyncNeeded = false;
  private boolean sitOnShoulder = false;
  private boolean shouldAttack = false;
  private boolean shouldGlowInTheDark = false;
  private boolean enableCustomTextureSkin = false;
  protected UUID persistentAngerTarget;
  protected int rideCooldownCounter;

  // Internal references
  private PlayerCompanionEntity playerCompanionEntity;

  // Additional ticker
  private static final int DATA_SYNC_TICK = 10;
  private int dataSyncTicker = 0;

  protected static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

  protected PlayerCompanionEntityData(EntityType<? extends TamableAnimal> entityType, Level level,
      Map<PlayerCompanionVariant, ResourceLocation> textureByVariant,
      Map<PlayerCompanionVariant, Item> companionItemByVariant) {
    super(entityType, level);

    // Dimension Name reference.
    this.dimensionName = level.dimension().location().toString();

    this.textureByVariant = textureByVariant;
    this.companionItemByVariant = companionItemByVariant;
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    maxHealth = COMMON.maxHealth.get();
    if (maxHealth > 0) {
      log.info("The max base health for player companions is set to {}.", maxHealth);
    } else {
      log.warn("The max base health for player companions will not be adjusted!");
    }

    maxAttackDamage = COMMON.maxAttackDamage.get();
    if (maxAttackDamage > 0) {
      log.info("The max base attack damage for player companions is set to {}.", maxAttackDamage);
    } else {
      log.warn("The max base attack damage for player companions will not be adjusted!");
    }
  }

  public void enableCustomTextureSkin(boolean enable) {
    this.enableCustomTextureSkin = enable;
  }

  public boolean enableCustomTextureSkin() {
    return this.enableCustomTextureSkin;
  }

  public void setSyncReference(PlayerCompanionEntity playerCompanionEntity) {
    // Internal reference for handling syncing and shared tasks.
    this.playerCompanionEntity = playerCompanionEntity;
  }

  public PlayerCompanionEntity getSyncReference() {
    return this.playerCompanionEntity;
  }

  public boolean isTamable() {
    return !isTame();
  }

  public Player getNearestPlayer(TargetingConditions targetingConditions) {
    return this.level.getNearestPlayer(targetingConditions, this);
  }

  public Item getCompanionItem() {
    return companionItemByVariant.getOrDefault(this.getVariant(),
        companionItemByVariant.get(PlayerCompanionVariant.DEFAULT));
  }

  public boolean isCharging() {
    return this.entityData.get(DATA_IS_CHARGING);
  }

  public void setCharging(boolean charging) {
    this.entityData.set(DATA_IS_CHARGING, charging);
  }

  public boolean isActive() {
    return this.entityData.get(DATA_ACTIVE);
  }

  public void setActive(boolean active) {
    this.entityData.set(DATA_ACTIVE, active);
  }

  public void setDataSyncNeeded() {
    if (!this.level.isClientSide && hasOwner()) {
      this.isDataSyncNeeded = true;
    }
  }

  public void setDataSyncNeeded(boolean dirty) {
    if (!this.level.isClientSide) {
      this.isDataSyncNeeded = dirty;
    }
  }

  public boolean getDataSyncNeeded() {
    return this.isDataSyncNeeded;
  }

  public String getDimensionName() {
    return this.dimensionName;
  }

  public int getExplosionPower() {
    return this.explosionPower;
  }

  public void setExplosionPower(int explosionPower) {
    this.explosionPower = explosionPower;
  }

  public PlayerCompanionVariant getVariant() {
    return PlayerCompanionVariant.getOrDefault(this.entityData.get(DATA_VARIANT));
  }

  public PlayerCompanionVariant getRandomVariant() {
    if (textureByVariant.size() > 1 && this.random.nextInt(2) == 0) {
      List<PlayerCompanionVariant> variants = new ArrayList<>(textureByVariant.keySet());
      return variants.get(this.random.nextInt(variants.size()));
    }
    return PlayerCompanionVariant.DEFAULT;
  }

  public void setVariant(PlayerCompanionVariant variant) {
    this.entityData.set(DATA_VARIANT, variant.name());
  }

  public void setGlowInTheDark(boolean glowInTheDark) {
    this.shouldGlowInTheDark = glowInTheDark;
  }

  public boolean shouldGlowInTheDark() {
    return this.shouldGlowInTheDark;
  }

  public void setCustomTextureCache(ResourceLocation resourceLocation) {
    this.textureCustomCache = resourceLocation;
  }

  public ResourceLocation getCustomTextureCache() {
    return this.textureCustomCache;
  }

  public boolean hasCustomTextureCache() {
    return this.textureCustomCache != null;
  }

  public void setTextureCache(ResourceLocation resourceLocation) {
    this.textureCache = resourceLocation;
  }

  public ResourceLocation getTextureCache() {
    return this.textureCache;
  }

  public boolean hasTextureCache() {
    return this.textureCache != null;
  }

  public boolean hasCustomTextureSkin() {
    return getCustomTextureSkin() != null && !getCustomTextureSkin().isEmpty();
  }

  public String getCustomTextureSkin() {
    return this.entityData.get(DATA_CUSTOM_TEXTURE_SKIN);
  }

  public boolean hasChangedCustomTextureSkin() {
    return (this.lastCustomTextureSkin == null && hasCustomTextureSkin())
        || (this.lastCustomTextureSkin != null
            && !this.lastCustomTextureSkin.equals(getCustomTextureSkin()));
  }

  public void setCustomTextureSkin(String textureSkinLocation) {
    if (!textureSkinLocation.equals(getCustomTextureSkin())) {
      this.entityData.set(DATA_CUSTOM_TEXTURE_SKIN, textureSkinLocation);
    }
    this.lastCustomTextureSkin = textureSkinLocation;
  }

  public void processCustomTextureSkin(String data) {
    String textureSkinLocation = "";
    if (PlayersUtils.isValidPlayerName(data)) {
      log.debug("Getting user texture for {}", data);
      textureSkinLocation = PlayersUtils.getUserTexture(level.getServer(), data);
    } else if (PlayersUtils.isValidUrl(data)) {
      log.debug("Setting remote user texture for {}", data);
      textureSkinLocation = data;
    }
    if (textureSkinLocation != null) {
      setCustomTextureSkin(textureSkinLocation);
    } else {
      log.error("Unable to process custom texture skin for with {}!", data);
    }
  }

  public ResourceLocation getTextureLocation() {
    if (level.isClientSide && !this.hasTextureCache()) {
      if (this.hasCustomTextureSkin()) {
        this.setTextureCache(ModTextureManager.addTexture(this.getCustomTextureSkin()));
      }
      if (!this.hasTextureCache()) {
        this.setTextureCache(textureByVariant.getOrDefault(this.getVariant(),
            textureByVariant.get(PlayerCompanionVariant.DEFAULT)));
      }
      if (!this.hasTextureCache()) {
        log.error("Unable to get any resource location for {}, looks like a bug!", this);
      }
    }
    return this.getTextureCache();
  }

  public ResourceLocation getTextureLocation(PlayerCompanionVariant variant) {
    if (level.isClientSide && !this.hasCustomTextureSkin()
        && textureCustomCacheVariant != variant) {
      this.setCustomTextureCache(textureByVariant.getOrDefault(variant,
          textureByVariant.get(PlayerCompanionVariant.DEFAULT)));
      textureCustomCacheVariant = variant;
    }
    return this.getCustomTextureCache();
  }

  public int getExperience() {
    return this.entityData.get(DATA_EXPERIENCE);
  }

  public int setExperience(int experience) {
    if (experience >= 1) {
      this.entityData.set(DATA_EXPERIENCE, experience);
      if (isMaxExperienceLevel()) {
        return -1;
      } else if (getExperienceForNextLevel() <= getExperience()) {
        return increaseExperienceLevel(1);
      } else if (getExperienceForLevel() >= getExperience()) {
        decreaseExperienceLevel(1);
      }
    }
    return -1;
  }

  public int getExperienceLevel() {
    return this.entityData.get(DATA_EXPERIENCE_LEVEL);
  }

  public int setExperienceLevel(int level) {
    if (level >= getMinExperienceLevel() && level != getExperienceLevel()) {
      this.entityData.set(DATA_EXPERIENCE_LEVEL, level);
      this.syncData();
    }
    return getExperienceLevel();
  }

  public void onLevelUp(int level) {
    log.debug("Level up for {} to {} ...", this, level);
    adjustMaxHealthPerLevel(level);
    adjustAttackDamagePerLevel(level);
    this.syncData();
  }

  public void onLevelDown(int level) {
    log.debug("Level down for {} to {} ...", this, level);
    adjustMaxHealthPerLevel(level);
    adjustAttackDamagePerLevel(level);
    this.syncData();
  }

  public void onExperienceChange(int experience) {
    this.setDataSyncNeeded();
  }

  public int getRespawnTimer() {
    return this.entityData.get(DATA_RESPAWN_TIMER);
  }

  public void setRespawnTimer(int timer) {
    if (timer > java.time.Instant.now().getEpochSecond()) {
      this.setActive(false);
    }
    this.entityData.set(DATA_RESPAWN_TIMER, timer);
  }

  public void stopRespawnTimer() {
    this.setActive(true);
    this.setRespawnTimer(0);
  }

  public String getRandomName() {
    return PlayerCompanionNames.getRandomCompanionName();
  }

  public int getJumpDelay() {
    return this.random.nextInt(20) + 10;
  }

  public int getWaitDelay() {
    return this.random.nextInt(200) + 10;
  }

  public int getJumpMoveDelay() {
    return JUMP_MOVE_DELAY;
  }

  public float getSoundPitch() {
    return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.4F;
  }

  public boolean hasRideCooldown() {
    return this.rideCooldownCounter > RIDE_COOLDOWN;
  }

  public int getRideCooldownCounter() {
    return this.rideCooldownCounter;
  }

  public void setRideCooldownCounter(int cooldown) {
    this.rideCooldownCounter = cooldown;
  }

  public void increaseRideCooldownCounter() {
    ++this.rideCooldownCounter;
  }

  public void setWantedPosition(double x, double y, double z, double speed) {
    this.moveControl.setWantedPosition(x, y, z, speed);
  }

  public boolean setEntityOnShoulder(ServerPlayer serverPlayer) {
    CompoundTag compoundTag = new CompoundTag();
    compoundTag.putString("id", this.getEncodeId());
    this.saveWithoutId(compoundTag);
    if (serverPlayer.setEntityOnShoulder(compoundTag)) {
      this.discard();
      this.setSitOnShoulder(true);
      return true;
    } else {
      this.setSitOnShoulder(false);
      return false;
    }
  }

  public boolean canSitOnShoulder() {
    return false;
  }

  public void setSitOnShoulder(boolean sitOnShoulder) {
    if (this.sitOnShoulder == sitOnShoulder) {
      return;
    }
    this.sitOnShoulder = sitOnShoulder;
    this.syncData();
  }

  public boolean isSitOnShoulder() {
    return this.sitOnShoulder;
  }

  public boolean hasOwner() {
    return this.getOwnerUUID() != null;
  }

  public boolean hasOwnerAndIsAlive() {
    return this.getOwnerUUID() != null && this.isAlive();
  }

  public boolean isOrderedToPosition() {
    return this.orderedToPosition != null;
  }

  public void setOrderedToPosition(BlockPos blockPos) {
    if (this.orderedToPosition == blockPos) {
      return;
    }
    this.orderedToPosition = blockPos;
    this.setDataSyncNeeded();
  }

  public BlockPos getOrderedToPosition() {
    return this.orderedToPosition;
  }

  public void setActionType(ActionType actionType) {
    if (this.actionType == actionType) {
      return;
    }
    this.actionType = actionType;
    this.setDataSyncNeeded();
  }

  public ActionType getActionType() {
    return this.actionType;
  }

  public void setAggressionLevel(AggressionLevel aggressionLevel) {
    if (this.aggressionLevel == aggressionLevel) {
      return;
    }
    this.aggressionLevel = aggressionLevel;
    this.shouldAttack = this.aggressionLevel == AggressionLevel.NEUTRAL
        || this.aggressionLevel == AggressionLevel.AGGRESSIVE
        || this.aggressionLevel == AggressionLevel.AGGRESSIVE_ANIMALS
        || this.aggressionLevel == AggressionLevel.AGGRESSIVE_MONSTER
        || this.aggressionLevel == AggressionLevel.AGGRESSIVE_PLAYERS
        || this.aggressionLevel == AggressionLevel.AGGRESSIVE_ALL;
    this.setDataSyncNeeded();
  }

  public AggressionLevel getAggressionLevel() {
    return this.aggressionLevel;
  }

  public AggressionLevel getNextAggressionLevel() {
    return getNextAggressionLevel(this.aggressionLevel);
  }

  public AggressionLevel getPreviousAggressionLevel() {
    return getPreviousAggressionLevel(this.aggressionLevel);
  }

  public void toggleAggressionLevel() {
    if (this.aggressionLevel == getLastAggressionLevel()) {
      setAggressionLevel(getFirstAggressionLevel());
    } else {
      AggressionLevel nextAggressionLevel = getNextAggressionLevel();
      if (nextAggressionLevel != this.aggressionLevel) {
        setAggressionLevel(nextAggressionLevel);
      }
    }
  }

  public boolean shouldAttack() {
    return this.shouldAttack;
  }

  public boolean canAttack() {
    LivingEntity livingEntity = this.playerCompanionEntity.getTarget();
    if (livingEntity instanceof Player player && (player.isSpectator() || player.isCreative())) {
      return false;
    }
    return livingEntity != null && livingEntity.isAlive() && isAlive();
  }

  public BlockPos ownerBlockPosition() {
    if (this.hasOwner()) {
      LivingEntity owner = this.getOwner();
      if (owner != null && owner.isAlive() && owner.isAddedToWorld()) {
        return owner.blockPosition();
      }
    }
    return this.blockPosition();
  }

  public boolean isWeapon(ItemStack itemStack) {
    if (itemStack.isEmpty()) {
      return false;
    }
    Item item = itemStack.getItem();
    return item instanceof SwordItem || item instanceof CrossbowItem || item instanceof TridentItem
        || item instanceof BowItem || item instanceof AxeItem;
  }

  public PlayerCompanionData getData() {
    return getData(getUUID());
  }

  public void setArmorItem(EquipmentSlot equipmentSlot, ItemStack itemStack) {
    setItemSlot(equipmentSlot, itemStack);
  }

  public void setHandItem(int slot, ItemStack itemStack) {
    if (slot == 0) {
      setItemSlot(EquipmentSlot.MAINHAND, itemStack);
    } else if (slot == 1) {
      setItemSlot(EquipmentSlot.OFFHAND, itemStack);
    }
  }

  public void adjustMaxHealthPerLevel(int level) {
    int healthAdjustment = getHealthAdjustmentFromExperienceLevel(level, maxHealth,
        (int) getAttribute(Attributes.MAX_HEALTH).getBaseValue());
    increaseMaxHealth(healthAdjustment);
  }

  public void increaseMaxHealth(int health) {
    if (health > 0) {
      // Remove previous attribute modifier, if needed.
      Set<AttributeModifier> attributeModifiers =
          getAttribute(Attributes.MAX_HEALTH).getModifiers();
      if (!attributeModifiers.isEmpty()) {
        Iterator<AttributeModifier> attributeModifierIterator = attributeModifiers.iterator();
        while (attributeModifierIterator.hasNext()) {
          AttributeModifier attributeModifier = attributeModifierIterator.next();
          if (attributeModifier != null
              && attributeModifier.getName().equals(ATTRIBUTE_MAX_HEALTH)) {
            getAttribute(Attributes.MAX_HEALTH).removeModifier(attributeModifier);
          }
        }
      }

      // Add new health modifier
      AttributeModifier increaseHealthModifier =
          new AttributeModifier(ATTRIBUTE_MAX_HEALTH, health, AttributeModifier.Operation.ADDITION);
      getAttribute(Attributes.MAX_HEALTH).addTransientModifier(increaseHealthModifier);
      if (isAlive()) {
        heal(getMaxHealth());
      }
    }
  }

  public void adjustAttackDamagePerLevel(int level) {
    int attackDamageAdjustment = getAttackDamageAdjustmentFromExperienceLevel(level,
        maxAttackDamage, (int) getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue());
    increaseAttackDamage(attackDamageAdjustment);
  }

  public void increaseAttackDamage(int attackDamage) {
    if (attackDamage > 0) {
      // Remove previous attribute modifier, if needed.
      Set<AttributeModifier> attributeModifiers =
          getAttribute(Attributes.ATTACK_DAMAGE).getModifiers();
      if (!attributeModifiers.isEmpty()) {
        Iterator<AttributeModifier> attributeModifierIterator = attributeModifiers.iterator();
        while (attributeModifierIterator.hasNext()) {
          AttributeModifier attributeModifier = attributeModifierIterator.next();
          if (attributeModifier != null
              && attributeModifier.getName().equals(ATTRIBUTE_ATTACK_DAMAGE)) {
            getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(attributeModifier);
          }
        }
      }

      // Add new attackDamage modifier
      AttributeModifier increaseAttackDamageModifier = new AttributeModifier(
          ATTRIBUTE_ATTACK_DAMAGE, attackDamage, AttributeModifier.Operation.ADDITION);
      getAttribute(Attributes.ATTACK_DAMAGE).addTransientModifier(increaseAttackDamageModifier);
    }
  }

  public int getAttackDamage() {
    // Calculate the possible attack damage
    double baseAttackDamage = getAttribute(Attributes.ATTACK_DAMAGE).getValue();
    ItemStack mainHandItem = getMainHandItem();
    if (mainHandItem != null && !mainHandItem.isEmpty()) {
      Collection<AttributeModifier> attributeModifierCollection =
          mainHandItem.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE);
      if (attributeModifierCollection != null && !attributeModifierCollection.isEmpty()) {
        Iterator<AttributeModifier> attributeModifierIterator =
            attributeModifierCollection.iterator();
        while (attributeModifierIterator.hasNext()) {
          AttributeModifier attributeModifier = attributeModifierIterator.next();
          if (attributeModifier != null) {
            baseAttackDamage += attributeModifier.getAmount();
          }
        }
      }
    }
    return (int) baseAttackDamage;
  }

  public void onMainHandItemSlotChange(ItemStack itemStack) {
    // Placeholder
  }

  public void onOffHandItemSlotChange(ItemStack itemStack) {
    // Placeholder
  }

  @Override
  public void setCustomName(@Nullable Component name) {
    super.setCustomName(name);
    setDataSyncNeeded();
  }

  @Override
  public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
    super.setItemSlot(equipmentSlot, itemStack);
    PlayerCompanionData data = this.getData();
    if (data != null) {
      if (equipmentSlot.getType() == Type.HAND) {
        data.setHandItem(equipmentSlot.getIndex(), itemStack);
        if (equipmentSlot == EquipmentSlot.MAINHAND) {
          onMainHandItemSlotChange(itemStack);
        } else if (equipmentSlot == EquipmentSlot.OFFHAND) {
          onOffHandItemSlotChange(itemStack);
        }
      } else if (equipmentSlot.getType() == Type.ARMOR) {
        data.setArmorItem(equipmentSlot.getIndex(), itemStack);
      }
    }
    setDataSyncNeeded();
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_ACTIVE, true);
    this.entityData.define(DATA_EXPERIENCE, 1);
    this.entityData.define(DATA_EXPERIENCE_LEVEL, 1);
    this.entityData.define(DATA_IS_CHARGING, false);
    this.entityData.define(DATA_RESPAWN_TIMER, 0);
    this.entityData.define(DATA_CUSTOM_TEXTURE_SKIN, "");
    this.entityData.define(DATA_VARIANT, PlayerCompanionVariant.NONE.name());
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compoundTag) {
    super.addAdditionalSaveData(compoundTag);
    compoundTag.putBoolean(DATA_ACTIVE_TAG, this.isActive());
    compoundTag.putInt(DATA_EXPERIENCE_LEVEL_TAG, this.getExperienceLevel());
    compoundTag.putInt(DATA_EXPERIENCE_TAG, this.getExperience());
    compoundTag.putInt(DATA_RESPAWN_TIMER_TAG, this.getRespawnTimer());
    compoundTag.putString(DATA_CUSTOM_TEXTURE_SKIN_TAG, this.getCustomTextureSkin());
    compoundTag.putString(DATA_VARIANT_TAG, this.getVariant().name());
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compoundTag) {
    super.readAdditionalSaveData(compoundTag);
    this.setActive(compoundTag.getBoolean(DATA_ACTIVE_TAG));

    // Handle experience, level and relevant modifier.
    this.setExperienceLevel(Math.max(compoundTag.getInt(DATA_EXPERIENCE_LEVEL_TAG), 1));
    this.setExperience(Math.max(compoundTag.getInt(DATA_EXPERIENCE_TAG), 1));
    int experienceLevel = this.getExperienceLevel();
    if (experienceLevel > getMinExperienceLevel()) {
      adjustMaxHealthPerLevel(experienceLevel);
      adjustAttackDamagePerLevel(experienceLevel);
    }

    // Handle respawn ticker.
    if (compoundTag.contains(DATA_RESPAWN_TIMER_TAG)) {
      this.setRespawnTimer(compoundTag.getInt(DATA_RESPAWN_TIMER_TAG));
    }

    // Handle hand items for additional effects like light effects.
    ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
    if (itemStack != null && !itemStack.isEmpty() && itemStack.is(Items.TORCH)
        && !shouldGlowInTheDark) {
      shouldGlowInTheDark = true;
    }

    // Handle User texture.
    if (compoundTag.contains(DATA_CUSTOM_TEXTURE_SKIN_TAG)) {
      this.setCustomTextureSkin(compoundTag.getString(DATA_CUSTOM_TEXTURE_SKIN_TAG));
    }

    this.setVariant(PlayerCompanionVariant.getOrDefault(compoundTag.getString(DATA_VARIANT_TAG)));
  }

  @Override
  public PlayerCompanionEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
    return null;
  }

  @Override
  public void tick() {
    super.tick();

    // ServerSide: Automatically Sync Data, if needed.
    if (!this.level.isClientSide && this.dataSyncTicker++ >= DATA_SYNC_TICK && syncDataIfNeeded()) {
      this.dataSyncTicker = 0;
    }
  }

}
