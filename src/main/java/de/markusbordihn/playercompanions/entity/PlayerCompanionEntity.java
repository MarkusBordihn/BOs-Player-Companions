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

import com.mojang.datafixers.util.Pair;

import net.minecraft.Util;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkHooks;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.keymapping.ModKeyMapping;
import de.markusbordihn.playercompanions.container.CompanionsMenu;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.entity.ai.goal.FoodItemGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.TameItemGoal;
import de.markusbordihn.playercompanions.network.NetworkHandler;

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

  // Config settings
  private static boolean respawnOnDeath = COMMON.respawnOnDeath.get();
  private static int respawnDelay = COMMON.respawnDelay.get();
  private static boolean friendlyFire = COMMON.friendlyFire.get();

  // Additional ticker
  private static final int INACTIVE_TICK = 100;
  private int ticker = 0;

  // Temporary states
  private boolean wasOnGround;

  public PlayerCompanionEntity(EntityType<? extends PlayerCompanionEntity> entityType,
      Level level) {
    super(entityType, level);

    // Set Reference to this object for easier sync and other tasks.
    this.setSyncReference(this);

    // Distribute Ticks along several entities.
    this.ticker = (short) this.random.nextInt(0, 50);

    setDataSyncNeeded();
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
      log.info("{} ignore entities from the same owner as attack target!", Constants.LOG_ICON_NAME);
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
  }

  public SoundEvent getJumpSound() {
    return SoundEvents.SLIME_JUMP_SMALL;
  }

  public SoundEvent getWaitSound() {
    return getAmbientSound();
  }

  public Item getTameItem() {
    return null;
  }

  public Ingredient getFoodItems() {
    return Ingredient.of(getTameItem());
  }

  public boolean doPlayJumpSound() {
    return true;
  }

  public Item getCompanionItem() {
    return null;
  }

  protected SoundEvent getPetSound() {
    return SoundEvents.WOLF_WHINE;
  }

  protected ParticleOptions getParticleType() {
    return null;
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
        Component playerCompanionName = this.getCustomName();
        MenuProvider provider = new MenuProvider() {
          @Override
          public Component getDisplayName() {
            return playerCompanionName;
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

  public void follow() {
    this.setOrderedToSit(false);
    this.navigation.recomputePath();
  }

  public boolean eat(ItemStack itemStack, Player player) {
    if (!canEat(itemStack)) {
      return false;
    }
    this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
    SoundEvent eatingSound = this.getEatingSound(itemStack);
    if (eatingSound != null) {
      playSound(eatingSound, this.getSoundVolume(), this.getSoundPitch());
    }
    Item item = itemStack.getItem();
    this.heal(item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 0.5F);
    if (item.getFoodProperties() != null) {
      for (Pair<MobEffectInstance, Float> pair : item.getFoodProperties().getEffects()) {
        if (!this.level.isClientSide && pair.getFirst() != null
            && this.level.random.nextFloat() < pair.getSecond()) {
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

  public void finalizeSpawn() {
    // Set random custom companion name, if not set.
    if (!this.hasCustomName()) {
      this.setCustomName(this.getCustomCompanionNameComponent());
    }

    // Reset respawn timer, if needed.
    int respawnTimer = this.getRespawnTimer();
    if (respawnTimer > 0 && respawnTimer < java.time.Instant.now().getEpochSecond()) {
      this.stopRespawnTimer();
    }
  }

  public void sendOwnerMessage(Component component) {
    LivingEntity owner = this.getOwner();
    if (component != null && owner != null) {
      owner.sendMessage(component, Util.NIL_UUID);
    }
  }

  @Override
  public void onLevelUp(int level) {
    super.onLevelUp(level);

    if (level > 1) {
      addParticle(ParticleTypes.ENCHANT);
      sendOwnerMessage(new TranslatableComponent(Util.makeDescriptionId("entity", LEVEL_UP_MESSAGE),
          this.getCustomCompanionName(), level));
    }
  }

  @Override
  public int getAmbientSoundInterval() {
    return 400;
  }

  @Override
  public float getSoundVolume() {
    // Is needed to delegate protection for access from move controller.
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
    sendOwnerMessage(new TranslatableComponent(Util.makeDescriptionId("entity", RESPAWN_MESSAGE),
        this.getCustomCompanionName()));
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
    if (!friendlyFire && livingEntity instanceof TamableAnimal tamableAnimal
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
    log.info("Player {} tamed player companion {} ...", player, this);
    if (player instanceof ServerPlayer) {
      this.registerData();
    }
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();

    // It not tamed, the companion will react to tame items.
    if (this.isTamable() && this.getTameItem() != null) {
      this.goalSelector.addGoal(3, new TameItemGoal(this, 0.8D));
    }

    // Adding food goals for tamed and untamed player companions.
    if (this.getFoodItems() != null) {
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

        // Open Player Companion Inventory, if none eatable item.
        else if (!this.isFood(itemStack)) {
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
    if (this.canEat(itemStack) && this.eat(itemStack, player)) {
      return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    return super.mobInteract(player, hand);
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
    super.die(damageSource);

    // Remove fire, effects and items before dying but before we are storing the data.
    clearFire();
    dropLeash(true, true);
    removeAllEffects();
    setNoGravity(false);

    if (isTame() && !this.level.isClientSide()) {

      // Decrease Experience Level
      decreaseExperienceLevel();

      if (respawnOnDeath) {
        if (respawnDelay > 1) {
          setRespawnTimer((int) java.time.Instant.now().getEpochSecond() + respawnDelay);
        }
        sendOwnerMessage(
            new TranslatableComponent(Util.makeDescriptionId("entity", WILL_RESPAWN_MESSAGE),
                getCustomCompanionName(), respawnDelay));
      } else {
        sendOwnerMessage(new TranslatableComponent(
            Util.makeDescriptionId("entity", WILL_NOT_RESPAWN_MESSAGE), getCustomCompanionName()));
        setActive(false);
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
