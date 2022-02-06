package de.markusbordihn.playercompanions.entity.healer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.item.ModItems;

public class Snail extends HealerEntity {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  public static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  // General Information
  public static final String ID = "snail";
  public static final String NAME = "Snail";
  public static final MobCategory CATEGORY = MobCategory.CREATURE;

  public Snail(EntityType<? extends Snail> entityType, Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F)
        .add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_SEAGRASS.get();
  }

  @Override
  protected Ingredient getFoodItems() {
    return Ingredient.of(Items.SEAGRASS);
  }

  @Override
  public Item getCompanionItem() {
    return ModItems.SNAIL.get();
  }

}
