/**
 * Copyright 2022 Markus Bordihn
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

package de.markusbordihn.playercompanions.item.companions;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;

import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.companions.Dobutsu;
import de.markusbordihn.playercompanions.entity.companions.ModEntityType;
import de.markusbordihn.playercompanions.item.CapturedCompanion;

public class DobutsuItem extends CapturedCompanion {

  public static final String ID = "dobutsu";

  public DobutsuItem() {
    super();
  }

  public DobutsuItem(PlayerCompanionVariant variant) {
    super(variant);
  }

  public DobutsuItem(Properties properties) {
    super(properties);
  }

  @Override
  public Ingredient getEntityFood() {
    return Dobutsu.FOOD_ITEMS;
  }

  @Override
  public EntityType<Dobutsu> getEntityType() {
    return ModEntityType.DOBUTSU.get();
  }
}
