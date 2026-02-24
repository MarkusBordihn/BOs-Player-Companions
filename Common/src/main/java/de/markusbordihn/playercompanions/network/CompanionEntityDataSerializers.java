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

package de.markusbordihn.playercompanions.network;

import de.markusbordihn.playercompanions.entity.CompanionRelationshipData;
import java.util.UUID;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class CompanionEntityDataSerializers {

  public static final EntityDataSerializer<CompanionRelationshipData> RELATIONSHIP_DATA =
    EntityDataSerializer.simple(
      (buf, data) -> {
        buf.writeInt(data.level());
        buf.writeInt(data.cooldown());
        buf.writeBoolean(data.tamingPlayer() != null);
        if (data.tamingPlayer() != null) {
          buf.writeUUID(data.tamingPlayer());
        }
        buf.writeLong(data.lastInteraction());
      },
      (buf) -> {
        int level = buf.readInt();
        int cooldown = buf.readInt();
        UUID tamingPlayer = buf.readBoolean() ? buf.readUUID() : null;
        long lastInteraction = buf.readLong();
        return new CompanionRelationshipData(level, cooldown, tamingPlayer, lastInteraction);
      });

  private CompanionEntityDataSerializers() {
  }

  public static void register() {
    EntityDataSerializers.registerSerializer(RELATIONSHIP_DATA);
  }
}
