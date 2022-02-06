package de.markusbordihn.playercompanions.entity.healer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.entity.CompanionEntity;
import de.markusbordihn.playercompanions.entity.CompanionType;

public class HealerEntity extends CompanionEntity implements NeutralMob {

  public HealerEntity(EntityType<? extends HealerEntity> entityType, Level level) {
    super(entityType, level);
    this.setTame(false);
    this.setCompanionType(CompanionType.HEALER);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
  }

}
