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

package de.markusbordihn.playercompanions.entity;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.block.LightBlock;
import de.markusbordihn.playercompanions.client.keymapping.ModKeyMapping;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.ai.goal.FoodItemGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.TameItemGoal;
import de.markusbordihn.playercompanions.item.CapturedCompanion;
import de.markusbordihn.playercompanions.network.NetworkHandler;
import de.markusbordihn.playercompanions.skin.SkinType;

@EventBusSubscriber
public class PlayerCompanionEntity extends PlayerCompanionEntityData
    implements TameablePlayerCompanion {

  // Shared constants
  public static final MobCategory CATEGORY = MobCategory.CREATURE;

  // Custom name format
  private static final ResourceLocation RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "companion_respawn_message");
  private static final ResourceLocation LEVEL_UP_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "companion_level_up_message");
  private static final ResourceLocation WILL_RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "companion_will_respawn_message");
  private static final ResourceLocation WILL_NOT_RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "companion_will_not_respawn_message");

  // Config
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static final int BABY_GROW_TIME = 20 * 60 * 60 * 24; // In ticks: 1 day

  // Additional ticker
  private static final int INACTIVE_TICK = 100;
  private static final int GLOW_TICK = LightBlock.TICK_TTL / 2;
  private static final int ANNOYING_COUNTER_TICK = 20 * 60 * 5; // In ticks: 5 minutes
  private int ticker = 0;
  private int glowTicker = 0;
  private int annoyingCounterTicker = 0;
  private int annoyingCounter = 0;

  // Temporary states
  private boolean wasOnGround;

  public PlayerCompanionEntity(EntityType<? extends PlayerCompanionEntity> entityType, Level level,
      Map<PlayerCompanionVariant, Item> companionItemByVariant) {
    super(entityType, level, companionItemByVariant);

    // Set Reference to this object for easier sync and other tasks.
    this.setSyncReference(this);

    // Distribute Ticks along several entities.
    this.ticker = this.random.nextInt(0, INACTIVE_TICK / 2);
    this.glowTicker = this.random.nextInt(0, GLOW_TICK / 2);

    // Force data sync.
    setDataSyncNeeded();
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    if (Boolean.TRUE.equals(COMMON.respawnOnDeath.get())) {
      log.info("{} will be respawn on death with a {} secs delay.", Constants.LOG_ICON_NAME,
          COMMON.respawnDelay.get());
    } else {
      log.warn("{} will NOT respawn on death!", Constants.LOG_ICON_NAME);
    }
    if (Boolean.FALSE.equals(COMMON.friendlyFire.get())) {
      log.info("{} ignore entities from the same owner as attack target!", Constants.LOG_ICON_NAME);
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
  }

  protected void addParticle(ParticleOptions particleOptions) {
    for (int i = 0; i < 4; ++i) {
      float randomCircleDistance = this.random.nextFloat() * ((float) Math.PI * 2F);
      float randomOffset = this.random.nextFloat() * 0.5F + 0.5F;
      float randomOffsetX = Mth.sin(randomCircleDistance) * 0.5F * randomOffset;
      float randomOffsetZ = Mth.cos(randomCircleDistance) * 0.5F * randomOffset;
      this.level().addParticle(particleOptions, this.getX() + randomOffsetX, this.getY() + 0.5,
          this.getZ() + randomOffsetZ, 0.0D, 0.0D, 0.0D);
    }
  }

  protected void playSound(Player player, SoundEvent sound) {
    playSound(player, sound, getSoundVolume(), getSoundPitch());
  }

  protected void playSound(Player player, SoundEvent sound, float volume, float pitch) {
    Level level = player.level();
    if (level.isClientSide && sound != null && sound.getLocation() != null && volume > 0.0f
        && pitch >= 0.0f) {
      player.playSound(sound, volume, pitch);
    }
  }

  public void follow() {
    this.setActionType(ActionType.FOLLOW);
    this.setOrderedToSit(false);
    this.navigation.recomputePath();
  }

  protected void sit() {
    this.setActionType(ActionType.SIT);
    this.setOrderedToSit(true);
    this.navigation.stop();
    super.setTarget(null);
  }

  protected void pet() {
    // Heal pet by 0.1 points.
    if (this.getHealth() < this.getMaxHealth()) {
      this.setHealth((float) (this.getHealth() + 0.1));
    }
  }

  public boolean eat(ItemStack itemStack, Player player) {
    if (!canEat(itemStack)) {
      return false;
    }
    this.gameEvent(GameEvent.EAT, this);
    SoundEvent eatingSound = this.getEatingSound(itemStack);
    if (eatingSound != null) {
      playSound(eatingSound, this.getSoundVolume(), this.getSoundPitch());
    }
    Item item = itemStack.getItem();
    FoodProperties foodProperties = item.getFoodProperties(itemStack, this);
    this.heal(foodProperties != null ? foodProperties.getNutrition() : 0.5F);
    if (foodProperties != null) {
      for (Pair<MobEffectInstance, Float> pair : foodProperties.getEffects()) {
        Level level = this.level();
        if (!level.isClientSide && pair.getFirst() != null
            && level.random.nextFloat() < pair.getSecond()) {
          this.addEffect(new MobEffectInstance(pair.getFirst()));
        }
      }
    }
    if (player != null && !player.getAbilities().instabuild) {
      itemStack.shrink(1);
    }
    this.gameEvent(GameEvent.EAT);
    return true;
  }

  public boolean canEat() {
    return this.getHealth() < this.getMaxHealth();
  }

  public boolean canEat(ItemStack itemStack) {
    return this.isFood(itemStack) && this.getHealth() < this.getMaxHealth();
  }

  public void handleCommand(PlayerCompanionCommand command) {
    switch (command) {
      case SIT:
        sit();
        break;
      case FOLLOW:
        follow();
        break;
      case SIT_FOLLOW_TOGGLE:
        if (isOrderedToSit()) {
          follow();
        } else {
          sit();
        }
        break;
      case AGGRESSION_LEVEL_DEFAULT:
        setAggressionLevel(getDefaultAggressionLevel());
        break;
      case AGGRESSION_LEVEL_NEXT:
        setAggressionLevel(getNextAggressionLevel());
        break;
      case AGGRESSION_LEVEL_PREVIOUS:
        setAggressionLevel(getPreviousAggressionLevel());
        break;
      case AGGRESSION_LEVEL_TOGGLE:
        toggleAggressionLevel();
        break;
      case OPEN_MENU:
        PlayerCompanionMenu.openMenu(this);
        break;
      case PET:
        pet();
        break;
    }
  }

  public void finalizeSpawn() {

    // Set random custom companion name, if not already set.
    if (!this.hasCustomName()) {
      this.setCustomName(Component.literal(this.getRandomName()));
    }

    // Reset charging, if needed.
    if (this.isCharging()) {
      this.setCharging(false);
    }

    // Reset respawn timer, if needed.
    int respawnTimer = this.getRespawnTimer();
    if (respawnTimer > 0 && respawnTimer < java.time.Instant.now().getEpochSecond()) {
      this.stopRespawnTimer();
    }

    // Trigger hand change events, if needed.
    ItemStack mainHandItemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
    if (mainHandItemStack != null) {
      this.onMainHandItemSlotChange(mainHandItemStack);
    }
    ItemStack offHandItemStack = this.getItemBySlot(EquipmentSlot.OFFHAND);
    if (offHandItemStack != null) {
      this.onOffHandItemSlotChange(offHandItemStack);
    }

    // Make sure to set variant to DEFAULT instead of NONE, if not was define until now.
    if (this.getVariant() == PlayerCompanionVariant.NONE) {
      this.setVariant(PlayerCompanionVariant.DEFAULT);
    }
  }

  public void sendOwnerMessage(Component component) {
    LivingEntity owner = this.getOwner();
    if (component != null && owner != null) {
      owner.sendSystemMessage(component);
    }
  }

  public boolean canRespawnOnDeath() {
    return Boolean.TRUE.equals(COMMON.respawnOnDeath.get());
  }

  @Override
  public void onLevelUp(int level) {
    super.onLevelUp(level);

    if (level > 1) {
      addParticle(ParticleTypes.ENCHANT);
      sendOwnerMessage(Component.translatable(
          Util.makeDescriptionId(Constants.ENTITY_TEXT_PREFIX, LEVEL_UP_MESSAGE),
          this.getCustomName(), level));
    }
  }

  @Override
  public int getAmbientSoundInterval() {
    return 400;
  }

  @Override
  public float getSoundVolume() {
    // This is needed to delegate protection for access from move controller.
    return 1.0F;
  }

  @Override
  public void setRespawnTimer(int timer) {
    super.setRespawnTimer(timer);
    setDataSyncNeeded();
  }

  @Override
  public void stopRespawnTimer() {
    super.stopRespawnTimer();
    sendOwnerMessage(Component.translatable(
        Util.makeDescriptionId(Constants.ENTITY_TEXT_PREFIX, RESPAWN_MESSAGE),
        this.getCustomName()));
    setDataSyncNeeded();
  }

  @Override
  public void setTarget(@Nullable LivingEntity livingEntity) {
    // Not set the same target again.
    if (this.getTarget() == livingEntity) {
      return;
    }

    // Early return for resetting target or dead targets.
    if (livingEntity == null || !livingEntity.isAlive()) {
      super.setTarget(null);
      setDataSyncNeeded();
      return;
    }

    // Ignore entities from the same Owner.
    if (!COMMON.friendlyFire.get() && livingEntity instanceof TamableAnimal tamableAnimal
        && tamableAnimal.getOwner() == this.getOwner()) {
      return;
    }

    // Add target if it passed all former criteria.
    super.setTarget(livingEntity);
    setDataSyncNeeded();
  }

  @Override
  public boolean canTamePlayerCompanion(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {
    return this.isTamable() && getTameItem() != null && itemStack.is(getTameItem())
        && player.getInventory().canPlaceItem(1, itemStack);
  }

  @Override
  public InteractionResult tamePlayerCompanion(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {
    if (itemStack != null && !player.getAbilities().instabuild) {
      itemStack.shrink(1);
    }
    if (this.random.nextInt(4) == 0
        && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
      this.tame(player);
      this.follow();
      this.level().broadcastEntityEvent(this, (byte) 7);
      return InteractionResult.SUCCESS;
    } else {
      this.level().broadcastEntityEvent(this, (byte) 6);
      return InteractionResult.CONSUME;
    }
  }

  @Override
  public void tame(Player player) {
    super.tame(player);
    log.info("Player {} tamed player companion {} ...", player, this);
    if (player instanceof ServerPlayer) {
      this.registerData();
    }
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();

    // It not tamed, the companion will react to tame items and stroll around.
    if (this.isTamable() && this.getTameItem() != null) {
      this.goalSelector.addGoal(3, new TameItemGoal(this, 0.8D));
      this.goalSelector.addGoal(10, new RandomStrollGoal(this, 0.8D));
    }

    // Adding food goals for tamed and untamed player companions.
    if (this.getFoodItems() != null && !this.getFoodItems().isEmpty()) {
      this.goalSelector.addGoal(3, new FoodItemGoal(this, 1.0D));
    }
  }

  @Override
  public PlayerCompanionEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
    // Nope no breeding for now.
    return null;
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    // We are using the used item hand instead of interaction hand.
    ItemStack itemStack = player.getItemInHand(player.getUsedItemHand());
    boolean isOwner = this.isTame() && this.isOwnedBy(player);

    // Most of the events will be client -> server side to make sure we have most of the flexibility
    // like additional keys and client-side animations.
    Level level = this.level();
    if (level.isClientSide) {

      if (isOwner) {

        // Handle Commands with CTRL Key pressed
        boolean commandKeyPressed = ModKeyMapping.COMMAND_KEY.isDown();
        if (commandKeyPressed && (itemStack.isEmpty() || isWeapon(itemStack))) {
          NetworkHandler.commandPlayerCompanion(getStringUUID(),
              PlayerCompanionCommand.SIT_FOLLOW_TOGGLE);
          return InteractionResult.SUCCESS;
        }

        // Handle Aggression level with ALT Key pressed
        boolean aggressionKeyPressed = ModKeyMapping.AGGRESSION_KEY.isDown();
        if (aggressionKeyPressed && (itemStack.isEmpty() || isWeapon(itemStack))) {
          NetworkHandler.commandPlayerCompanion(getStringUUID(),
              PlayerCompanionCommand.AGGRESSION_LEVEL_TOGGLE);
          return InteractionResult.SUCCESS;
        }

        // Pet Player Companion
        if (player.isCrouching() && itemStack.isEmpty()) {
          this.addParticle(ParticleTypes.HEART);
          if (this.getPetSound() != null) {
            this.playSound(player, this.getPetSound());
          }
          NetworkHandler.commandPlayerCompanion(getStringUUID(), PlayerCompanionCommand.PET);
          return InteractionResult.SUCCESS;
        }

        // No further interaction with "empty" captured companion items.
        else if (itemStack != null && !itemStack.isEmpty()
            && itemStack.getItem() instanceof CapturedCompanion capturedCompanion
            && !capturedCompanion.hasCompanion(itemStack)) {
          return InteractionResult.FAIL;
        }

        // Open Player Companion Inventory, if none eatable item.
        else if (!this.isFood(itemStack)) {
          NetworkHandler.commandPlayerCompanion(getStringUUID(), PlayerCompanionCommand.OPEN_MENU);
          return InteractionResult.SUCCESS;
        }

      } else if (getTameItem() != null && itemStack.is(getTameItem()) && !this.isTame()) {
        return InteractionResult.CONSUME;
      }

      // Send player a hint how to tame any untamed companion.
      else if (!this.isTame() && !this.isFood(itemStack) && getTameItem() != null) {
        if (annoyingCounter < 10) {
          player.sendSystemMessage(
              Component.translatable(Constants.TEXT_PREFIX + "untamed_companion.interaction",
                  this.getCustomName(), getTameItem()));
          annoyingCounter++;
        } else {
          player.sendSystemMessage(Component
              .translatable(Constants.TEXT_PREFIX + "untamed_companion.interaction.annoying",
                  this.getCustomName())
              .withStyle(ChatFormatting.RED));
        }
        return InteractionResult.SUCCESS;
      }
    }

    // Health companion with food item, from any player.
    if (this.isFood(itemStack)) {
      if (this.canEat(itemStack)) {
        if (this.eat(itemStack, player)) {
          this.addParticle(ParticleTypes.HEART);
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      } else {
        log.debug("{} is fully staffed ...", this);
      }
      return InteractionResult.PASS;
    }

    return super.mobInteract(player, hand);
  }

  @Override
  public void playSound(SoundEvent sound, float volume, float pitch) {
    if (sound != null && sound.getLocation() != null && volume > 0.0f && pitch >= 0.0f) {
      super.playSound(sound, volume, pitch);
    }
  }

  @Override
  public boolean isFood(ItemStack itemStack) {
    if (this.getFoodItems() != null) {
      return this.getFoodItems().test(itemStack);
    }
    if (!itemStack.isEdible()) {
      return false;
    }
    return super.isFood(itemStack);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compoundTag) {
    super.readAdditionalSaveData(compoundTag);
    setDataSyncNeeded();
  }

  @Override
  @Nullable
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor,
      DifficultyInstance difficulty, MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
    spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficulty, mobSpawnType,
        spawnGroupData, compoundTag);

    // Set random texture variants, if available.
    if (SkinType.DEFAULT.equals(getSkinType())
        && this.getVariant() == PlayerCompanionVariant.NONE) {
      this.setVariant(this.getRandomVariant());
    }

    // Set random baby variants for more fun.
    if (this.random.nextInt(10) == 0) {
      this.setAge(-BABY_GROW_TIME);
    }

    finalizeSpawn();
    return spawnGroupData;
  }

  @Override
  public void tick() {
    // Perform tick for AI and other important steps.
    super.tick();

    // Allow do disable entity to save performance and to allow basic respawn logic.
    if (!isActive()) {
      if (this.ticker++ >= INACTIVE_TICK) {
        this.ticker = 0;
      } else {
        return;
      }
    }

    // ClientSide: Annoying counter reset.
    Level level = this.level();
    if (level.isClientSide && this.annoyingCounter > 0
        && this.annoyingCounterTicker++ >= ANNOYING_COUNTER_TICK) {
      this.annoyingCounter = 0;
      this.annoyingCounterTicker = 0;
    }

    // ServerSide: Place light block, if companion should glow in the dark.
    if (!level.isClientSide && this.shouldGlowInTheDark() && this.glowTicker++ >= GLOW_TICK) {
      BlockPos lightBlockPos = this.getOnPos();
      if (level.isNight() || level.isRaining() || level.isThundering()
          || !level.canSeeSky(lightBlockPos)) {
        LightBlock.place(level, lightBlockPos);
      }
      this.glowTicker = 0;
    }

    // Shows particle and play sound after jump or fall.
    if (this.onGround() && !this.wasOnGround) {
      if (this.getParticleType() != null) {
        for (int j = 0; j < 4; ++j) {
          float f = this.random.nextFloat() * ((float) Math.PI * 2F);
          float f1 = this.random.nextFloat() * 0.5F + 0.5F;
          float f2 = Mth.sin(f) * 0.5F * f1;
          float f3 = Mth.cos(f) * 0.5F * f1;
          level.addParticle(this.getParticleType(), this.getX() + f2, this.getY(), this.getZ() + f3,
              0.0D, 0.0D, 0.0D);
        }
      }
      if (doPlayJumpSound() && getJumpSound() != null) {
        this.playSound(getJumpSound(), this.getSoundVolume(), this.getSoundPitch());
      }
    }
    this.wasOnGround = this.onGround();
  }

  @Override
  public void die(DamageSource damageSource) {
    super.die(damageSource);

    // Remove fire, effects and items before dying but before we are storing the data.
    if (canRespawnOnDeath()) {
      clearFire();
      dropLeash(true, true);
      removeAllEffects();
      setNoGravity(false);
    }

    Level level = this.level();
    if (isTame() && !level.isClientSide()) {
      // Inform owner about dead of companion and possible respawn.
      if (canRespawnOnDeath()) {
        // Decrease Experience Level
        decreaseExperienceLevel();

        if (COMMON.respawnDelay.get() > 1) {
          setRespawnTimer(
              (int) java.time.Instant.now().getEpochSecond() + COMMON.respawnDelay.get());
        }
        sendOwnerMessage(Component.translatable(
            Util.makeDescriptionId(Constants.ENTITY_TEXT_PREFIX, WILL_RESPAWN_MESSAGE),
            getCustomName(), COMMON.respawnDelay.get()));
      } else {
        // Inform owner about dead of companion and possible respawn.
        sendOwnerMessage(Component.translatable(
            Util.makeDescriptionId(Constants.ENTITY_TEXT_PREFIX, WILL_NOT_RESPAWN_MESSAGE),
            getCustomName()));
        setActive(false);

        // Unregister companion from server data and remove entity.
        PlayerCompanionsServerData.get().unregisterCompanion(this);
        this.remove(RemovalReason.KILLED);
      }
    }

  }

  @Override
  public void setOrderedToSit(boolean sit) {
    if (this.isOrderedToSit() != sit) {
      super.setOrderedToSit(sit);
      setDataSyncNeeded();
    }
  }

  @Override
  public String toString() {
    RemovalReason removalReason = this.getRemovalReason();
    String level = this.level() == null ? "~NULL~" : this.level().toString();
    UUID ownerUUID = this.getOwnerUUID();
    String owner = ownerUUID == null ? "~NULL~" : ownerUUID.toString();
    Boolean tamed = this.isTame();
    int id = this.getId();

    return removalReason != null
        ? String.format(Locale.ROOT,
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, tamed=%s, owner='%s', removed=%s]",
            this.getClass().getSimpleName(), this.getName().getString(), id, level, this.getX(),
            this.getY(), this.getZ(), tamed, owner, removalReason)
        : String.format(Locale.ROOT,
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, tamed=%s, owner='%s']",
            this.getClass().getSimpleName(), this.getName().getString(), id, level, this.getX(),
            this.getY(), this.getZ(), tamed, owner);
  }

}
