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

import de.markusbordihn.easynpc.data.objective.ObjectiveDataEntry;
import de.markusbordihn.easynpc.data.objective.ObjectiveType;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.ObjectiveDataCapable;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerDataCapable;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
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
  };

  private static final ObjectiveType[] BASE_OBJECTIVES = {
    ObjectiveType.FLOAT,
  };

  private CompanionBehaviorHandler() {}

  public static void initializeTamedBehavior(PlayerCompanion companion) {
    if (!(companion instanceof EasyNPC<?>) || companion.level().isClientSide) {
      return;
    }
    log.debug("Initializing tamed behavior for {}", companion.asEntity());
    applyCommand(companion, CompanionCommand.FOLLOW);
    if (companion.getCompanionRole() == CompanionRole.GUARD) {
      applyGuardObjectives((EasyNPC<?>) companion);
    }
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
      case FOLLOW -> applyFollowObjectives(easyNPC, objectives);
      case SIT -> companion.asMob().getNavigation().stop();
      case WANDER -> objectives.addOrUpdateCustomObjective(
          new ObjectiveDataEntry(ObjectiveType.RANDOM_STROLL_AROUND_HOME, 5));
    }

    companion.setCompanionCommand(command);
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
      followOwner.setSpeedModifier(0.8);
      objectives.addOrUpdateCustomObjective(followOwner);
    } else {
      log.warn("Cannot apply FOLLOW_OWNER: no owner set on {}", easyNPC.getEntity());
    }
    objectives.addOrUpdateCustomObjective(
        new ObjectiveDataEntry(ObjectiveType.WATER_AVOIDING_RANDOM_STROLL, 8));
  }

  private static void applyGuardObjectives(EasyNPC<?> easyNPC) {
    if (!(easyNPC instanceof ObjectiveDataCapable<?> objectives)) {
      return;
    }
    for (ObjectiveType type : COMBAT_OBJECTIVES) {
      if (!objectives.hasObjective(type)) {
        ObjectiveDataEntry entry = new ObjectiveDataEntry(type, 2);
        if (type == ObjectiveType.MELEE_ATTACK) {
          entry.setSpeedModifier(1.2);
        }
        objectives.addOrUpdateCustomObjective(entry);
      }
    }
  }
}
