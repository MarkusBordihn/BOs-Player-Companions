/*
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

package de.markusbordihn.playercompanions.container;

import de.markusbordihn.playercompanions.Constants;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainer {

  public static final DeferredRegister<MenuType<?>> CONTAINERS =
      DeferredRegister.create(ForgeRegistries.CONTAINERS, Constants.MOD_ID);

  protected ModContainer() {}

  public static final RegistryObject<MenuType<CompanionMenu>> DEFAULT_COMPANION_MENU =
      CONTAINERS.register(
          "default_companion_menu", () -> IForgeMenuType.create(DefaultCompanionMenu::new));
  public static final RegistryObject<MenuType<CollectorCompanionMenu>> COLLECTOR_COMPANION_MENU =
      CONTAINERS.register(
          "collector_companion_menu", () -> IForgeMenuType.create(CollectorCompanionMenu::new));
  public static final RegistryObject<MenuType<FollowerCompanionMenu>> FOLLOWER_COMPANION_MENU =
      CONTAINERS.register(
          "follower_companion_menu", () -> IForgeMenuType.create(FollowerCompanionMenu::new));
  public static final RegistryObject<MenuType<GuardCompanionMenu>> GUARD_COMPANION_MENU =
      CONTAINERS.register(
          "guard_companion_menu", () -> IForgeMenuType.create(GuardCompanionMenu::new));
  public static final RegistryObject<MenuType<HealerCompanionMenu>> HEALER_COMPANION_MENU =
      CONTAINERS.register(
          "healer_companion_menu", () -> IForgeMenuType.create(HealerCompanionMenu::new));
  public static final RegistryObject<MenuType<SupporterCompanionMenu>> SUPPORTER_COMPANION_MENU =
      CONTAINERS.register(
          "supporter_companion_menu", () -> IForgeMenuType.create(SupporterCompanionMenu::new));
}