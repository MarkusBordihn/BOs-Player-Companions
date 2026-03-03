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

package de.markusbordihn.playercompanions.menu;

import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

  public static MenuType<CompanionDefaultMenu> COMPANION_DEFAULT;
  public static MenuType<CompanionCollectorMenu> COMPANION_COLLECTOR;
  public static MenuType<CompanionGuardMenu> COMPANION_GUARD;
  public static MenuType<CompanionFollowerMenu> COMPANION_FOLLOWER;
  public static MenuType<CompanionShrineMenu> COMPANION_SHRINE;

  private ModMenuTypes() {
  }

  public static MenuType<CompanionDefaultMenu> getDefaultMenuType() {
    return COMPANION_DEFAULT;
  }

  public static MenuType<CompanionCollectorMenu> getCollectorMenuType() {
    return COMPANION_COLLECTOR;
  }

  public static MenuType<CompanionGuardMenu> getGuardMenuType() {
    return COMPANION_GUARD;
  }

  public static MenuType<CompanionFollowerMenu> getFollowerMenuType() {
    return COMPANION_FOLLOWER;
  }
}
