package de.markusbordihn.playercompanions.entity;

import de.markusbordihn.playercompanions.data.Experience;

public interface PlayerCompanionExperience {

  public int getExperience();

  public int setExperience(int experience);

  public int getExperienceLevel();

  public int setExperienceLevel(int level);

  public void onLevelUp(int level);

  public void onLevelDown(int level);

  public void onExperienceChange(int experience);

  public default boolean isMaxExperienceLevel() {
    return getExperienceLevel() >= getMaxExperienceLevel();
  }

  public default boolean isMinExperienceLevel() {
    return getExperienceLevel() == getMinExperienceLevel();
  }

  public default int getMaxExperienceLevel() {
    return Experience.MAX_LEVEL;
  }

  public default int getMinExperienceLevel() {
    return Experience.MIN_LEVEL;
  }

  public default int increaseExperienceLevel(int level) {
    return setExperienceLevel(getExperienceLevel() + level);
  }

  public default void decreaseExperienceLevel(int level) {
    setExperienceLevel(getExperienceLevel() - level);
  }

  public default void decreaseExperienceAndExperienceLevel() {
    decreaseExperience(Experience.getExperienceDifferenceForLevel(getExperienceLevel()));
  }

  public default void increaseExperience(int experience) {
    int levelUp = setExperience(getExperience() + experience);
    if (levelUp > getMinExperienceLevel()) {
      this.onLevelUp(levelUp);
    }
    onExperienceChange(getExperience());
  }

  public default void decreaseExperience(int experience) {
    setExperience(getExperience() - experience);
  }

  public default void decreaseExperienceLevel() {
    decreaseExperienceAndExperienceLevel();
    this.onLevelDown(getExperienceLevel());
  }

  public default int getExperienceForNextLevel() {
    return Experience.getExperienceForNextLevel(getExperienceLevel());
  }

  public default int getExperienceForLevel() {
    return Experience.getExperienceForLevel(getExperienceLevel());
  }

  public default int getHealthAdjustmentFromExperienceLevel(int level, int maxHealth,
      int baseHealth) {
    // Early return if we don't need to calculate anything.
    if (level == 1 || maxHealth == 0 || baseHealth >= maxHealth) {
      return 0;
    }
    double healthFactorPerLevel = (double) (maxHealth - baseHealth) / getMaxExperienceLevel();
    if (healthFactorPerLevel > 0) {
      return (int) Math.floor(level * healthFactorPerLevel + 0.5);
    }
    return 0;
  }

  public default int getHealingAmountFromExperienceLevel(int level, int minHealing,
      int maxHealing) {
    if (level == 1 || minHealing >= maxHealing) {
      return minHealing;
    }
    double healingFactorPerLevel = (double) (maxHealing - minHealing) / getMaxExperienceLevel();
    if (healingFactorPerLevel > 0) {
      return minHealing + (int) Math.floor(level * healingFactorPerLevel + 0.5);
    }
    return minHealing;
  }

}
