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
import java.util.UUID;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkHooks;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.keymapping.ModKeyMapping;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.container.CompanionsMenu;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.network.NetworkHandler;

@EventBusSubscriber
public class PlayerCompanionEntity extends PlayerCompanionEntityData
    implements TameablePlayerCompanion {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  // Custom name format
  private static final ResourceLocation RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "companion_respawn_message");
  private static final ResourceLocation WILL_RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "companion_will_respawn_message");
  private static final ResourceLocation WILL_NOT_RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "companion_will_not_respawn_message");

  // Config settings
  private static boolean respawnOnDeath = COMMON.respawnOnDeath.get();
  private static int respawnDelay = COMMON.respawnDelay.get();
  private static boolean friendlyFire = COMMON.friendlyFire.get();

  private static final int INACTIVE_TICK = 100;

  // Temporary states
  private boolean wasOnGround;

  // Anger state
  private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
  private UUID persistentAngerTarget;
  private int ticker = 0;


  public PlayerCompanionEntity(EntityType<? extends PlayerCompanionEntity> entityType,
      Level level) {
    super(entityType, level);
    this.setTame(false);

    // Distribute Ticks along several entities
    this.ticker = (short) this.random.nextInt(0, 50);
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    respawnOnDeath = COMMON.respawnOnDeath.get();
    respawnDelay = COMMON.respawnDelay.get();
    friendlyFire = COMMON.friendlyFire.get();
    if (respawnOnDeath) {
      log.info("{} will be respawn on death with a {} secs delay.", Constants.LOG_ICON_NAME,
          COMMON.respawnDelay.get(), respawnDelay);
    } else {
      log.warn("{} will NOT respawn on death!", Constants.LOG_ICON_NAME);
    }
    if (!friendlyFire) {
      log.info("{} ignore entities from the same owner as attack target!");
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
  }

  protected SoundEvent getJumpSound() {
    return SoundEvents.SLIME_JUMP_SMALL;
  }

  protected SoundEvent getWaitSound() {
    return getAmbientSound();
  }

  protected Item getTameItem() {
    return null;
  }

  protected Ingredient getFoodItems() {
    return Ingredient.of(getTameItem());
  }

  protected boolean doPlayJumpSound() {
    return true;
  }

  public Item getCompanionItem() {
    return null;
  }

  public void tameAndFollow(Player player) {
    this.tame(player);
    this.follow();
  }

  protected SoundEvent getPetSound() {
    return SoundEvents.WOLF_WHINE;
  }

  protected ParticleOptions getParticleType() {
    return null;
  }

  public void startPersistentAngerTimer() {
    this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
  }

  @Nullable
  public UUID getPersistentAngerTarget() {
    return this.persistentAngerTarget;
  }

  public void setPersistentAngerTarget(@Nullable UUID uuid) {
    this.persistentAngerTarget = uuid;
  }

  protected void addParticle(ParticleOptions particleOptions) {
    for (int i = 0; i < 4; ++i) {
      float randomCircleDistance = this.random.nextFloat() * ((float) Math.PI * 2F);
      float randomOffset = this.random.nextFloat() * 0.5F + 0.5F;
      float randomOffsetX = Mth.sin(randomCircleDistance) * 0.5F * randomOffset;
      float randomOffsetZ = Mth.cos(randomCircleDistance) * 0.5F * randomOffset;
      this.level.addParticle(particleOptions, this.getX() + randomOffsetX, this.getY(),
          this.getZ() + randomOffsetZ, 0.0D, 0.0D, 0.0D);
    }
  }

  protected void playSound(Player player, SoundEvent sound) {
    playSound(player, sound, getSoundVolume(), getSoundPitch());
  }

  protected void playSound(Player player, SoundEvent sound, float volume, float pitch) {
    if (player.level.isClientSide) {
      player.playSound(sound, volume, pitch);
    }
  }

  public void openMenu() {
    LivingEntity owner = getOwner();
    if (owner != null && PlayerCompanionsServerData.available()) {
      ServerPlayer player = this.level.getServer().getPlayerList().getPlayer(owner.getUUID());
      if (player instanceof ServerPlayer) {
        UUID playerCompanionUUID = this.getUUID();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return new TranslatableComponent("container.player_companions.companions_menu");
          }

          @Nullable
          @Override
          public AbstractContainerMenu createMenu(int windowId, Inventory inventory,
              Player player) {
            return new CompanionsMenu(windowId, inventory, playerCompanionUUID);
          }
        };
        NetworkHooks.openGui(player, provider, buffer -> buffer.writeUUID(playerCompanionUUID));
      }
    }
  }

  protected void pet() {
    // Heal pet by 0.1 points.
    if (this.getHealth() < this.getMaxHealth()) {
      this.setHealth((float) (this.getHealth() + 0.1));
    }
  }

  protected void follow() {
    this.setOrderedToSit(false);
    this.navigation.recomputePath();
  }

  protected void sit() {
    this.setOrderedToSit(true);
    this.navigation.stop();
    super.setTarget(null);
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
      case OPEN_MENU:
        openMenu();
        break;
      case PET:
        pet();
        break;
    }
  }

  public PlayerCompanionsServerData getServerData() {
    return PlayerCompanionsServerData.get();
  }

  public PlayerCompanionData getData() {
    PlayerCompanionsServerData serverData = getServerData();
    if (serverData == null) {
      return null;
    }
    return PlayerCompanionsServerData.get().getCompanion(getUUID());
  }

  public void finalizeSpawn() {
    // Set random custom companion name, if not set.
    if (!this.hasCustomName()) {
      this.setCustomName(this.getCustomCompanionNameComponent());
    }

    // Reset respawn timer, if needed.
    int respawnTimer = this.getRespawnTimer();
    if (respawnTimer < java.time.Instant.now().getEpochSecond()) {
      this.stopRespawnTimer();
    }
  }

  @Override
  public int getAmbientSoundInterval() {
    return 400;
  }

  @Override
  protected float getSoundVolume() {
    // Is needed to delegate protection for access from move controller.
    return 1.0F;
  }

  @Override
  public void setRespawnTimer(int timer) {
    super.setRespawnTimer(timer);
    getServerData().updatePlayerCompanion(this);
  }

  @Override
  public void stopRespawnTimer() {
    super.stopRespawnTimer();
    LivingEntity owner = this.getOwner();
    if (owner != null) {
      owner.sendMessage(new TranslatableComponent(Util.makeDescriptionId("entity", RESPAWN_MESSAGE),
          this.getCustomCompanionName()), Util.NIL_UUID);
    }
    getServerData().updatePlayerCompanion(this);
  }

  @Override
  public void setTarget(@Nullable LivingEntity livingEntity) {
    // Early return for resetting target.
    if (livingEntity == null) {
      super.setTarget(null);
      return;
    }

    // Ignore entities from the same Owner.
    if (!friendlyFire && livingEntity instanceof TamableAnimal tamableAnimal
        && tamableAnimal.getOwner() == this.getOwner()) {
      return;
    }

    if (livingEntity.isAlive()) {
      super.setTarget(livingEntity);
    }
  }

  @Override
  public boolean canTamePlayerCompanion(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {
    return this.isTamable() && !this.isTame() && getTameItem() != null
        && itemStack.is(getTameItem()) && player.getInventory().canPlaceItem(1, itemStack);
  }

  @Override
  public InteractionResult tamePlayerCompanion(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {
    if (itemStack != null && !player.getAbilities().instabuild) {
      itemStack.shrink(1);
    }
    if (this.random.nextInt(4) == 0
        && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
      this.tameAndFollow(player);
      this.level.broadcastEntityEvent(this, (byte) 7);
      return InteractionResult.SUCCESS;
    } else {
      this.level.broadcastEntityEvent(this, (byte) 6);
      return InteractionResult.CONSUME;
    }
  }

  @Override
  public void tame(Player player) {
    super.tame(player);
    if (player instanceof ServerPlayer) {
      this.getServerData().updateOrRegisterCompanion(this);
    }
  }

  @Override
  public boolean causeFallDamage(float start, float end, DamageSource damageSource) {
    if (this.flying()) {
      return false;
    }
    return super.causeFallDamage(start, end, damageSource);
  }

  @Override
  protected void checkFallDamage(double height, boolean flag, BlockState blockState,
      BlockPos blockPos) {
    if (this.flying()) {
      return;
    }
    super.checkFallDamage(height, flag, blockState, blockPos);
  }

  @Override
  public boolean onClimbable() {
    if (this.flying()) {
      return false;
    }
    return super.onClimbable();
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new FloatGoal(this));
    this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
    this.goalSelector.addGoal(3, new TemptGoal(this, 0.75D, getFoodItems(), false));
    if (!this.isTame()) {
      this.goalSelector.addGoal(3, new TemptGoal(this, 0.9D, Ingredient.of(getTameItem()), false));
    }
    if (this.flyingAround()) {
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 8.0F, 4.0F, true));
    } else if (this.flying()) {
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 8.0F, 2.0F, true));
    } else {
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 4.0F, 1.0F, false));
    }
    this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
  }

  @Override
  public PlayerCompanionEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
    return null;
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);
    boolean isOwner = this.isTame() && this.isOwnedBy(player);

    // Most of the events will be client -> server side to make sure we have most of the flexibility
    // like additional keys and client-side animations.
    if (this.level.isClientSide) {

      if (isOwner) {

        // Handler Commands with CTRL Key pressed
        boolean ctrlKeyPressed = ModKeyMapping.KEY_COMMAND.isDown();
        if (ctrlKeyPressed) {
          // Order to sit is hand is empty or has an weapon in hand (during compat)
          if (itemStack.isEmpty() || isWeapon(itemStack)) {
            NetworkHandler.commandPlayerCompanion(getStringUUID(),
                PlayerCompanionCommand.SIT_FOLLOW_TOGGLE);
            return InteractionResult.SUCCESS;
          }
        }

        // Pet Player Companion
        else if (player.isCrouching() && itemStack.isEmpty()) {
          this.addParticle(ParticleTypes.HEART);
          if (this.getPetSound() != null) {
            this.playSound(player, this.getPetSound());
          }
          NetworkHandler.commandPlayerCompanion(getStringUUID(), PlayerCompanionCommand.PET);
          return InteractionResult.SUCCESS;
        }

        // Open Player Companion Inventory
        else if (itemStack.isEmpty()) {
          NetworkHandler.commandPlayerCompanion(getStringUUID(), PlayerCompanionCommand.OPEN_MENU);
          return InteractionResult.SUCCESS;
        }

      }

      // Early return for other client side events.
      boolean isOwnedOrTamed =
          getTameItem() != null && itemStack.is(getTameItem()) && !this.isTame();
      return isOwnedOrTamed ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    // Health companion with food item, from any player.
    Item item = itemStack.getItem();
    if (this.isTame() && this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
      if (!player.getAbilities().instabuild) {
        itemStack.shrink(1);
      }
      this.heal(item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 0.5F);
      SoundEvent eatingSound = this.getEatingSound(itemStack);
      if (eatingSound != null) {
        playSound(eatingSound, this.getSoundVolume(), this.getSoundPitch());
      }
      this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
      return InteractionResult.SUCCESS;
    }

    return super.mobInteract(player, hand);
  }

  @Override
  public boolean isFood(ItemStack itemStack) {
    if (getTameItem() != null) {
      return getFoodItems().test(itemStack);
    }
    return super.isFood(itemStack);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compoundTag) {
    super.readAdditionalSaveData(compoundTag);
    getServerData().updateOrRegisterCompanion(this);
  }

  @Override
  @Nullable
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor,
      DifficultyInstance difficulty, MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
    spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficulty, mobSpawnType,
        spawnGroupData, compoundTag);

    finalizeSpawn();
    return spawnGroupData;
  }

  @Override
  public void tick() {
    // Perform tick for AI and other important steps.
    super.tick();

    // Allow do disable entity to save performance and to allow basic respawn logic.
    if (!isActive()) {
      if (ticker++ >= INACTIVE_TICK) {
        ticker = 0;
      } else {
        return;
      }
    }

    // Shows particle and play sound after jump or fall.
    if (this.onGround && !this.wasOnGround) {
      if (this.getParticleType() != null) {
        for (int j = 0; j < 4; ++j) {
          float f = this.random.nextFloat() * ((float) Math.PI * 2F);
          float f1 = this.random.nextFloat() * 0.5F + 0.5F;
          float f2 = Mth.sin(f) * 0.5F * f1;
          float f3 = Mth.cos(f) * 0.5F * f1;
          this.level.addParticle(this.getParticleType(), this.getX() + f2, this.getY(),
              this.getZ() + f3, 0.0D, 0.0D, 0.0D);
        }
      }
      if (doPlayJumpSound() && getJumpSound() != null) {
        this.playSound(getJumpSound(), this.getSoundVolume(), this.getSoundPitch());
      }
    }
    this.wasOnGround = this.onGround;
  }

  @Override
  public void die(DamageSource damageSource) {

    // Remove fire, effects and items before dying but before we are storing the data.
    clearFire();
    dropLeash(true, true);
    removeAllEffects();

    LivingEntity owner = getOwner();
    if (isTame() && owner != null && !this.level.isClientSide()) {
      if (respawnOnDeath) {
        if (respawnDelay > 1) {
          setRespawnTimer((int) java.time.Instant.now().getEpochSecond() + respawnDelay);
        }
        owner.sendMessage(
            new TranslatableComponent(Util.makeDescriptionId("entity", WILL_RESPAWN_MESSAGE),
                getCustomCompanionName(), respawnDelay),
            Util.NIL_UUID);
      } else {
        owner.sendMessage(
            new TranslatableComponent(Util.makeDescriptionId("entity", WILL_NOT_RESPAWN_MESSAGE),
                getCustomCompanionName()),
            Util.NIL_UUID);
        setActive(false);
      }
    }
    super.die(damageSource);
  }

  @Override
  public void setOrderedToSit(boolean sit) {
    if (this.isOrderedToSit() != sit) {
      super.setOrderedToSit(sit);
      getServerData().updatePlayerCompanion(this);
    }
  }

  @Override
  public String toString() {
    RemovalReason removalReason = this.getRemovalReason();
    String level = this.level == null ? "~NULL~" : this.level.toString();
    String owner = this.getOwnerUUID() == null ? "~NULL~" : this.getOwnerUUID().toString();
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
