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

package de.markusbordihn.playercompanions.network.message;

import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

public class MessagePlayerCompanionsData {

  protected final CompoundTag data;

  public MessagePlayerCompanionsData(CompoundTag data) {
    this.data = data;
  }

  public static MessagePlayerCompanionsData decode(final FriendlyByteBuf buffer) {
    return new MessagePlayerCompanionsData(buffer.readNbt());
  }

  public static void encode(MessagePlayerCompanionsData message, FriendlyByteBuf buffer) {
    buffer.writeNbt(message.getData());
  }

  public static void handle(
      MessagePlayerCompanionsData message, CustomPayloadEvent.Context context) {
    context.enqueueWork(
        () -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(message)));
    context.setPacketHandled(true);
  }

  public static void handlePacket(MessagePlayerCompanionsData message) {
    PlayerCompanionsClientData.load(message.getData());
  }

  public CompoundTag getData() {
    return this.data;
  }
}
