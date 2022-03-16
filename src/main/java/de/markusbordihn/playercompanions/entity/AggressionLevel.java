package de.markusbordihn.playercompanions.entity;

public enum AggressionLevel {
  PASSIVE_FLEE,
  PASSIVE,
  NEUTRAL,
  AGGRESSIVE,
  AGGRESSIVE_ANIMALS,
  AGGRESSIVE_MONSTER,
  AGGRESSIVE_PLAYERS,
  AGGRESSIVE_ALL,
  UNKNOWN;

  public AggressionLevel getNext() {
    return values()[(ordinal()+1) % values().length];
  }
}
