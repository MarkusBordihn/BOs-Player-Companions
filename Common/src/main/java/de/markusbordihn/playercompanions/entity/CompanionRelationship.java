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

import de.markusbordihn.playercompanions.config.TamingConfig;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class CompanionRelationship {

  private final Mob entity;
  private final EntityDataAccessor<CompanionRelationshipData> dataAccessor;

  public CompanionRelationship(
    Mob entity,
    EntityDataAccessor<CompanionRelationshipData> dataAccessor) {
    this.entity = entity;
    this.dataAccessor = dataAccessor;
  }

  private CompanionRelationshipData getData() {
    return entity.getEntityData().get(dataAccessor);
  }

  private void setData(CompanionRelationshipData data) {
    entity.getEntityData().set(dataAccessor, data);
  }

  public int getLevel() {
    return getData().level();
  }

  public void setLevel(int level) {
    setData(getData().withLevel(level));
  }

  public void addLevel(int amount) {
    setLevel(getLevel() + amount);
  }

  public int getCooldown() {
    return getData().cooldown();
  }

  private void setCooldown(int ticks) {
    setData(getData().withCooldown(ticks));
  }

  public UUID getTamingPlayer() {
    return getData().tamingPlayer();
  }

  public void setTamingPlayer(UUID player) {
    setData(getData().withTamingPlayer(player));
  }

  public boolean isTamingInProgress() {
    return getTamingPlayer() != null;
  }

  public boolean canInteract(Player player) {
    if (getCooldown() > 0) {
      return false;
    }
    if (TamingConfig.ONLY_ONE_PLAYER_CAN_TAME && getTamingPlayer() != null) {
      return getTamingPlayer().equals(player.getUUID());
    }
    return true;
  }

  public void recordInteraction(long gameTime) {
    setData(getData().withLastInteraction(gameTime).withCooldown(TamingConfig.FEED_COOLDOWN));
  }

  public void tick(long gameTime) {
    if (getCooldown() > 0) {
      setData(getData().tick());
    }
  }

  public void save(CompoundTag tag) {
    getData().save(tag);
  }

  public void load(CompoundTag tag) {
    setData(CompanionRelationshipData.load(tag));
  }

  public void reset() {
    setData(CompanionRelationshipData.EMPTY);
  }
}
