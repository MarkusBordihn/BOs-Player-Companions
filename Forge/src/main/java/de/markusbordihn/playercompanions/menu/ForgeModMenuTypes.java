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

import de.markusbordihn.playercompanions.Constants;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ForgeModMenuTypes {

  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
    DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MOD_ID);

  public static final RegistryObject<MenuType<CompanionDefaultMenu>>
    COMPANION_DEFAULT = MENU_TYPES.register("companion_menu",
    () -> IForgeMenuType.create(
      (IContainerFactory<CompanionDefaultMenu>) CompanionDefaultMenu::new));

  public static final RegistryObject<MenuType<CompanionCollectorMenu>>
    COMPANION_COLLECTOR = MENU_TYPES.register("companion_collector_menu",
    () -> IForgeMenuType.create(
      (IContainerFactory<CompanionCollectorMenu>) CompanionCollectorMenu::new));

  public static final RegistryObject<MenuType<CompanionGuardMenu>>
    COMPANION_GUARD = MENU_TYPES.register("companion_guard_menu",
    () -> IForgeMenuType.create(
      (IContainerFactory<CompanionGuardMenu>) CompanionGuardMenu::new));

  public static final RegistryObject<MenuType<CompanionFollowerMenu>>
    COMPANION_FOLLOWER = MENU_TYPES.register("companion_follower_menu",
    () -> IForgeMenuType.create(
      (IContainerFactory<CompanionFollowerMenu>) CompanionFollowerMenu::new));

  private ForgeModMenuTypes() {
  }

  public static void register() {
    ModMenuTypes.COMPANION_DEFAULT = COMPANION_DEFAULT.get();
    ModMenuTypes.COMPANION_COLLECTOR = COMPANION_COLLECTOR.get();
    ModMenuTypes.COMPANION_GUARD = COMPANION_GUARD.get();
    ModMenuTypes.COMPANION_FOLLOWER = COMPANION_FOLLOWER.get();
  }
}
