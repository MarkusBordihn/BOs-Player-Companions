/*
 * Copyright 2021 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.markusbordihn.playercompanions.entity.companions;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityType {

  public static final DeferredRegister<EntityType<?>> ENTITIES =
      DeferredRegister.create(ForgeRegistries.ENTITIES, Constants.MOD_ID);
  // Collector Entity
  public static final RegistryObject<EntityType<Pig>> PIG =
      ENTITIES.register(
          Pig.ID,
          () ->
              EntityType.Builder.of(Pig::new, PlayerCompanionEntity.CATEGORY)
                  .sized(1.0F, 1.2F)
                  .clientTrackingRange(8)
                  .build(Pig.ID));
  public static final RegistryObject<EntityType<Snail>> SNAIL =
      ENTITIES.register(
          Snail.ID,
          () ->
              EntityType.Builder.of(Snail::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.8F, 0.9F)
                  .clientTrackingRange(8)
                  .build(Snail.ID));
  // Follower Entity
  public static final RegistryObject<EntityType<Dobutsu>> DOBUTSU =
      ENTITIES.register(
          Dobutsu.ID,
          () ->
              EntityType.Builder.of(Dobutsu::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.5F, 0.9F)
                  .clientTrackingRange(8)
                  .build(Dobutsu.ID));
  public static final RegistryObject<EntityType<Lizard>> LIZARD =
      ENTITIES.register(
          Lizard.ID,
          () ->
              EntityType.Builder.of(Lizard::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.6F, 0.5F)
                  .clientTrackingRange(8)
                  .build(Lizard.ID));
  public static final RegistryObject<EntityType<SmallSlime>> SMALL_SLIME =
      ENTITIES.register(
          SmallSlime.ID,
          () ->
              EntityType.Builder.of(SmallSlime::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.5F, 0.5F)
                  .clientTrackingRange(8)
                  .build(SmallSlime.ID));
  // Guard Entity
  public static final RegistryObject<EntityType<Samurai>> SAMURAI =
      ENTITIES.register(
          Samurai.ID,
          () ->
              EntityType.Builder.of(Samurai::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.5F, 1.4F)
                  .clientTrackingRange(16)
                  .build(Samurai.ID));
  public static final RegistryObject<EntityType<Raptor>> RAPTOR =
      ENTITIES.register(
          Raptor.ID,
          () ->
              EntityType.Builder.of(Raptor::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.8F, 1.5F)
                  .clientTrackingRange(16)
                  .build(Raptor.ID));
  public static final RegistryObject<EntityType<Rooster>> ROOSTER =
      ENTITIES.register(
          Rooster.ID,
          () ->
              EntityType.Builder.of(Rooster::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.6F, 1.1F)
                  .clientTrackingRange(16)
                  .build(Rooster.ID));
  public static final RegistryObject<EntityType<SmallGhast>> SMALL_GHAST =
      ENTITIES.register(
          SmallGhast.ID,
          () ->
              EntityType.Builder.of(SmallGhast::new, PlayerCompanionEntity.CATEGORY)
                  .sized(1.0F, 2.13F)
                  .clientTrackingRange(16)
                  .build(SmallGhast.ID));
  // Healer Entity
  public static final RegistryObject<EntityType<Fairy>> FAIRY =
      ENTITIES.register(
          Fairy.ID,
          () ->
              EntityType.Builder.of(Fairy::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.8F, 2.0F)
                  .clientTrackingRange(16)
                  .build(Fairy.ID));
  // Lighting Entity
  public static final RegistryObject<EntityType<Firefly>> FIREFLY =
      ENTITIES.register(
          Firefly.ID,
          () ->
              EntityType.Builder.of(Firefly::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.25F, 0.25F)
                  .clientTrackingRange(16)
                  .build(Firefly.ID));
  // Supporter Entity
  public static final RegistryObject<EntityType<WelshCorgi>> WELSH_CORGI =
      ENTITIES.register(
          WelshCorgi.ID,
          () ->
              EntityType.Builder.of(WelshCorgi::new, PlayerCompanionEntity.CATEGORY)
                  .sized(0.65F, 1.0F)
                  .clientTrackingRange(16)
                  .build(WelshCorgi.ID));

  protected ModEntityType() {}

  @SubscribeEvent
  public static final void entityAttributCreation(EntityAttributeCreationEvent event) {
    // Create Attributes for Entities
    event.put(DOBUTSU.get(), Dobutsu.createAttributes().build());
    event.put(FAIRY.get(), Fairy.createAttributes().build());
    event.put(FIREFLY.get(), Firefly.createAttributes().build());
    event.put(LIZARD.get(), Lizard.createAttributes().build());
    event.put(PIG.get(), Pig.createAttributes().build());
    event.put(RAPTOR.get(), Rooster.createAttributes().build());
    event.put(ROOSTER.get(), Rooster.createAttributes().build());
    event.put(SAMURAI.get(), Samurai.createAttributes().build());
    event.put(SMALL_GHAST.get(), SmallGhast.createAttributes().build());
    event.put(SMALL_SLIME.get(), SmallSlime.createAttributes().build());
    event.put(SNAIL.get(), Snail.createAttributes().build());
    event.put(WELSH_CORGI.get(), WelshCorgi.createAttributes().build());
  }
}
