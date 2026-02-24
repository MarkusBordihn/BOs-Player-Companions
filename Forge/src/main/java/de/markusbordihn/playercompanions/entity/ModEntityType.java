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

package de.markusbordihn.playercompanions.entity;

import de.markusbordihn.easynpc.api.npc.base.slime.SlimeBase;
import de.markusbordihn.playercompanions.Constants;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityType {

  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
    DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);
  public static final Map<CompanionEntityType, RegistryObject<EntityType<?>>> COMPANION_TYPE =
    new EnumMap<>(CompanionEntityType.class);
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  static {
    for (CompanionEntityType type : CompanionEntityType.values()) {
      log.info("Registering companion entity type {}", type.getResourceKey());
      COMPANION_TYPE.put(
        type,
        ENTITY_TYPES.register(
          type.getId(), () -> type.getBuilder().build(type.getResourceKey().toString())));
    }
  }

  private ModEntityType() {
  }

  public static <T extends Entity> EntityType<T> getEntityType(CompanionEntityType type) {
    return (EntityType<T>) COMPANION_TYPE.get(type).get();
  }

  @SubscribeEvent
  public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
    log.info("{} Companion Entity Attributes ...", Constants.LOG_REGISTER_PREFIX);
    for (CompanionEntityType type : CompanionEntityType.values()) {
      switch (type) {
        case ROOSTER:
          event.put(
            (EntityType<? extends LivingEntity>) COMPANION_TYPE.get(type).get(),
            Chicken.createAttributes().build());
          break;
        case SMALL_SLIME:
          event.put(
            (EntityType<? extends LivingEntity>) COMPANION_TYPE.get(type).get(),
            SlimeBase.createAttributes().build());
          break;
        default:
          event.put(
            (EntityType<? extends LivingEntity>) COMPANION_TYPE.get(type).get(),
            Pig.createAttributes().build());
          break;
      }
    }
  }
}
