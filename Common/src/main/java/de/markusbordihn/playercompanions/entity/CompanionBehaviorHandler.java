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

import de.markusbordihn.easynpc.api.pose.ModelPoseAPI;
import de.markusbordihn.easynpc.data.objective.ObjectiveDataEntry;
import de.markusbordihn.easynpc.data.objective.ObjectiveType;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.ObjectiveDataCapable;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerDataCapable;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.entity.taming.CompanionFoodRegistry;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionBehaviorHandler {

  private static final Logger log = LogManager.getLogger(CompanionBehaviorHandler.class);

  private static final ObjectiveType[] MOVEMENT_OBJECTIVES = {
    ObjectiveType.FOLLOW_OWNER,
    ObjectiveType.RANDOM_STROLL,
    ObjectiveType.RANDOM_STROLL_AROUND_HOME,
    ObjectiveType.WATER_AVOIDING_RANDOM_STROLL,
  };

  private static final ObjectiveType[] COMBAT_OBJECTIVES = {
    ObjectiveType.OWNER_HURT_BY_TARGET,
    ObjectiveType.HURT_BY_TARGET,
    ObjectiveType.MELEE_ATTACK,
    ObjectiveType.ATTACK_MONSTER,
    ObjectiveType.ATTACK_ANIMAL,
    ObjectiveType.ATTACK_MOB,
    ObjectiveType.ATTACK_PLAYER_WITHOUT_OWNER,
    ObjectiveType.PANIC,
  };

  private static final ObjectiveType[] BASE_OBJECTIVES = {
    ObjectiveType.FLOAT,
  };

  private static final ObjectiveType[] WILD_OBJECTIVES = {
    ObjectiveType.FLOAT,
    ObjectiveType.PANIC,
    ObjectiveType.WATER_AVOIDING_RANDOM_STROLL,
  };

  private CompanionBehaviorHandler() {
  }

  public static void initializeWildBehavior(PlayerCompanion companion) {
    if (!(companion instanceof EasyNPC<?> easyNPC) || companion.level().isClientSide) {
      return;
    }
    if (!(easyNPC instanceof ObjectiveDataCapable<?> objectives)) {
      return;
    }
    log.debug("Initializing wild behavior for {}", companion.asEntity());
    for (ObjectiveType type : WILD_OBJECTIVES) {
      int priority = 5;
      if (type == ObjectiveType.FLOAT) {
        priority = 0;
      } else if (type == ObjectiveType.PANIC) {
        priority = 1;
      }
      ObjectiveDataEntry entry = new ObjectiveDataEntry(type, priority);
      if (type == ObjectiveType.PANIC) {
        entry.setSpeedModifier(TamingConfig.COMPANION_COMBAT_SPEED);
      }
      objectives.addOrUpdateCustomObjective(entry);
    }
    Ingredient foodIngredient = CompanionFoodRegistry.getFoodIngredient(companion.getType());
    if (!foodIngredient.isEmpty() && easyNPC.getPathfinderMob() != null) {
      objectives.getEntityGoalSelector().addGoal(3,
        new TemptGoal(easyNPC.getPathfinderMob(), 0.8, foodIngredient, false));
    }
  }

  public static void initializeTamedBehavior(PlayerCompanion companion) {
    if (!(companion instanceof EasyNPC<?> easyNPC) || companion.level().isClientSide) {
      return;
    }
    log.debug("Initializing tamed behavior for {}", companion.asEntity());
    clearWildGoals(easyNPC);
    applyCommand(companion, CompanionCommand.FOLLOW);
    if (companion.getCompanionRole() == CompanionRole.GUARD) {
      applyGuardObjectives(companion, companion.getAggressionLevel());
    }
  }

  public static void applyGuardObjectives(PlayerCompanion companion, AggressionLevel level) {
    if (!(companion instanceof EasyNPC<?> easyNPC) || companion.level().isClientSide) {
      return;
    }
    log.debug("Applying guard objectives {} to {}", level, companion.asEntity());
    if (!(easyNPC instanceof ObjectiveDataCapable<?> objectives)) {
      return;
    }
    removeCombatObjectives(objectives);

    switch (level) {
      case PASSIVE_FLEE -> {
        ObjectiveDataEntry panicEntry = new ObjectiveDataEntry(ObjectiveType.PANIC, 1);
        panicEntry.setSpeedModifier(1.2);
        objectives.addOrUpdateCustomObjective(panicEntry);
      }

      case PASSIVE -> {
        // no combat objectives — guard stands still and does nothing
      }

      case NEUTRAL -> addBaseCombatObjectives(objectives);

      case AGGRESSIVE_MONSTER -> {
        addBaseCombatObjectives(objectives);
        objectives.addOrUpdateCustomObjective(
          new ObjectiveDataEntry(ObjectiveType.ATTACK_MONSTER, 3));
      }

      case AGGRESSIVE_ANIMALS -> {
        addBaseCombatObjectives(objectives);
        objectives.addOrUpdateCustomObjective(
          new ObjectiveDataEntry(ObjectiveType.ATTACK_ANIMAL, 3));
      }

      case AGGRESSIVE_PLAYERS -> {
        addBaseCombatObjectives(objectives);
        objectives.addOrUpdateCustomObjective(
          new ObjectiveDataEntry(ObjectiveType.ATTACK_PLAYER_WITHOUT_OWNER, 3));
      }

      case AGGRESSIVE_ALL -> {
        addBaseCombatObjectives(objectives);
        objectives.addOrUpdateCustomObjective(new ObjectiveDataEntry(ObjectiveType.ATTACK_MOB, 3));
      }
    }
  }

  private static void addBaseCombatObjectives(ObjectiveDataCapable<?> objectives) {
    objectives.addOrUpdateCustomObjective(new ObjectiveDataEntry(ObjectiveType.HURT_BY_TARGET, 2));
    objectives.addOrUpdateCustomObjective(
      new ObjectiveDataEntry(ObjectiveType.OWNER_HURT_BY_TARGET, 2));
    ObjectiveDataEntry melee = new ObjectiveDataEntry(ObjectiveType.MELEE_ATTACK, 2);
    melee.setSpeedModifier(TamingConfig.COMPANION_COMBAT_SPEED);
    objectives.addOrUpdateCustomObjective(melee);
  }

  public static void applyCommand(PlayerCompanion companion, CompanionCommand command) {
    if (!(companion instanceof EasyNPC<?> easyNPC) || companion.level().isClientSide) {
      return;
    }
    if (!(easyNPC instanceof ObjectiveDataCapable<?> objectives)) {
      return;
    }
    log.debug("Applying command {} to {}", command, companion.asEntity());

    clearMovementObjectives(objectives);
    applyBaseObjectives(objectives);

    switch (command) {
      case FOLLOW -> {
        applyFollowObjectives(easyNPC, objectives);
        ModelPoseAPI.resetPose(easyNPC);
      }
      case SIT -> {
        companion.asMob().getNavigation().stop();
        ModelPoseAPI.setPose(easyNPC, "sitting");
      }
      case WANDER -> {
        objectives.addOrUpdateCustomObjective(
          new ObjectiveDataEntry(ObjectiveType.RANDOM_STROLL_AROUND_HOME, 5));
        ModelPoseAPI.resetPose(easyNPC);
      }
    }

    companion.setCompanionCommand(command);
  }

  private static void clearWildGoals(EasyNPC<?> easyNPC) {
    GoalSelector goalSelector = easyNPC.getEntityGoalSelector();
    goalSelector.getAvailableGoals().stream()
      .map(WrappedGoal::getGoal)
      .filter(TemptGoal.class::isInstance)
      .toList()
      .forEach(goalSelector::removeGoal);
  }

  private static void clearMovementObjectives(ObjectiveDataCapable<?> objectives) {
    for (ObjectiveType type : MOVEMENT_OBJECTIVES) {
      if (objectives.hasObjective(type)) {
        objectives.removeCustomObjective(type);
      }
    }
  }

  private static void applyBaseObjectives(ObjectiveDataCapable<?> objectives) {
    for (ObjectiveType type : BASE_OBJECTIVES) {
      if (!objectives.hasObjective(type)) {
        objectives.addOrUpdateCustomObjective(new ObjectiveDataEntry(type));
      }
    }
  }

  private static void applyFollowObjectives(EasyNPC<?> easyNPC,
    ObjectiveDataCapable<?> objectives) {
    OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
    if (ownerData != null && ownerData.getOwnerUUID() != null) {
      ObjectiveDataEntry followOwner = new ObjectiveDataEntry(ObjectiveType.FOLLOW_OWNER, 6);
      followOwner.setTargetOwnerUUID(ownerData.getOwnerUUID());
      followOwner.setSpeedModifier(TamingConfig.COMPANION_FOLLOW_SPEED);
      objectives.addOrUpdateCustomObjective(followOwner);
    } else {
      log.warn("Cannot apply FOLLOW_OWNER: no owner set on {}", easyNPC.getEntity());
    }
    objectives.addOrUpdateCustomObjective(
      new ObjectiveDataEntry(ObjectiveType.WATER_AVOIDING_RANDOM_STROLL, 8));
  }

  private static void removeCombatObjectives(ObjectiveDataCapable<?> objectives) {
    for (ObjectiveType type : COMBAT_OBJECTIVES) {
      if (objectives.hasObjective(type)) {
        objectives.removeCustomObjective(type);
      }
    }
  }
}
