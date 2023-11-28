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

package de.markusbordihn.playercompanions.sounds;

import de.markusbordihn.playercompanions.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundEvents {

  public static final DeferredRegister<SoundEvent> SOUNDS =
      DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Constants.MOD_ID);
  // Fairy Sound Events
  public static final RegistryObject<SoundEvent> COMPANION_FAIRY_DEATH =
      SOUNDS.register(
          "companion.fairy.death",
          () ->
              SoundEvent.createVariableRangeEvent(
                  new ResourceLocation(Constants.MOD_ID, "companion.fairy.death")));
  public static final RegistryObject<SoundEvent> COMPANION_FAIRY_HURT =
      SOUNDS.register(
          "companion.fairy.hurt",
          () ->
              SoundEvent.createVariableRangeEvent(
                  new ResourceLocation(Constants.MOD_ID, "companion.fairy.hurt")));
  public static final RegistryObject<SoundEvent> COMPANION_FAIRY_HAGGLE =
      SOUNDS.register(
          "companion.fairy.haggle",
          () ->
              SoundEvent.createVariableRangeEvent(
                  new ResourceLocation(Constants.MOD_ID, "companion.fairy.haggle")));
  public static final RegistryObject<SoundEvent> COMPANION_FAIRY_AMBIENT =
      SOUNDS.register(
          "companion.fairy.ambient",
          () ->
              SoundEvent.createVariableRangeEvent(
                  new ResourceLocation(Constants.MOD_ID, "companion.fairy.ambient")));
  public static final RegistryObject<SoundEvent> COMPANION_FAIRY_YES =
      SOUNDS.register(
          "companion.fairy.yes",
          () ->
              SoundEvent.createVariableRangeEvent(
                  new ResourceLocation(Constants.MOD_ID, "companion.fairy.yes")));
  public static final RegistryObject<SoundEvent> COMPANION_FAIRY_NO =
      SOUNDS.register(
          "companion.fairy.no",
          () ->
              SoundEvent.createVariableRangeEvent(
                  new ResourceLocation(Constants.MOD_ID, "companion.fairy.no")));

  protected ModSoundEvents() {}
}
