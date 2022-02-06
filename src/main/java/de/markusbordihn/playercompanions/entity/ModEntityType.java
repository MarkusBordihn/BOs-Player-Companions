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

import net.minecraft.world.entity.EntityType;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.follower.SmallSlime;
import de.markusbordihn.playercompanions.entity.guard.GuardEntity;
import de.markusbordihn.playercompanions.entity.guard.SmallGhast;
import de.markusbordihn.playercompanions.entity.healer.Snail;

@EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityType {

  protected ModEntityType() {

  }

  public static final DeferredRegister<EntityType<?>> ENTITIES =
      DeferredRegister.create(ForgeRegistries.ENTITIES, Constants.MOD_ID);

  // Healer Entity
  public static final RegistryObject<EntityType<Snail>> SNAIL =
      ENTITIES.register(Snail.ID, () -> EntityType.Builder.<Snail>of(Snail::new, Snail.CATEGORY)
          .sized(0.9F, 1.2F).clientTrackingRange(12).build(Snail.ID));

  // Guard Entity
  public static final RegistryObject<EntityType<SmallGhast>> SMALL_GHAST = ENTITIES.register(
      SmallGhast.ID, () -> EntityType.Builder.<SmallGhast>of(SmallGhast::new, SmallGhast.CATEGORY)
          .sized(0.9F, 1.2F).clientTrackingRange(12).build(SmallGhast.ID));

  // Follower Entity
  public static final RegistryObject<EntityType<SmallSlime>> SMALL_SLIME = ENTITIES.register(
      SmallSlime.ID, () -> EntityType.Builder.<SmallSlime>of(SmallSlime::new, SmallSlime.CATEGORY)
          .sized(0.5F, 0.5F).clientTrackingRange(12).build(SmallSlime.ID));

  @SubscribeEvent
  public static final void entityAttributCreation(EntityAttributeCreationEvent event) {
    // Create Attributes for Entities
    event.put(SMALL_GHAST.get(), GuardEntity.createAttributes().build());
    event.put(SMALL_SLIME.get(), SmallSlime.createAttributes().build());
    event.put(SNAIL.get(), Snail.createAttributes().build());
  }
}