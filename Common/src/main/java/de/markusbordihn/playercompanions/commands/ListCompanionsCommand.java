/*
 * Copyright 2026 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.playercompanions.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.markusbordihn.easynpc.data.npc.NPCEntityMetadata;
import de.markusbordihn.easynpc.data.npc.SavedNPCEntityEntry;
import de.markusbordihn.easynpc.data.saveddata.NPCEntityData;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ListCompanionsCommand extends CompanionCommand {

  private ListCompanionsCommand() {
  }

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("list")
      .requires(cs -> cs.hasPermission(Commands.LEVEL_ALL))
      .executes(context -> listCompanions(context.getSource()));
  }

  private static int listCompanions(CommandSourceStack context) {
    ServerPlayer player;
    try {
      player = context.getPlayerOrException();
    } catch (Exception e) {
      return sendFailureMessage(context, "This command must be run by a player.");
    }

    Collection<SavedNPCEntityEntry> entries =
      NPCEntityData.get(context.getServer()).getEntriesByOwner(player.getUUID());

    if (entries.isEmpty()) {
      return sendSuccessMessage(context, "You have no companions.", ChatFormatting.YELLOW);
    }

    sendSuccessMessage(
      context, "✦ Your Companions (" + entries.size() + "):", ChatFormatting.GREEN);

    for (SavedNPCEntityEntry entry : entries) {
      NPCEntityMetadata meta = entry.metadata();
      StringBuilder info = new StringBuilder();

      // Name from saved NPC data if available
      String name = entry.entityUUID().toString().substring(0, 8);
      if (entry.npcData() != null && entry.npcData().contains("CustomName")) {
        String rawName = entry.npcData().getString("CustomName");
        if (!rawName.isEmpty()) {
          try {
            Component parsed = Component.Serializer.fromJson(rawName);
            name = parsed != null ? parsed.getString() : rawName;
          } catch (Exception ignored) {
            name = rawName;
          }
        }
      }

      String type = meta.hasEntityType() ? meta.entityType() : "unknown";
      if (type.contains(":")) {
        type = type.substring(type.indexOf(':') + 1);
      }

      // Make type more readable: pig_companion -> Pig
      type = type.replace("_companion", "");
      type = type.substring(0, 1).toUpperCase() + type.substring(1);
      type = type.replace("Small_slime", "Small Slime");
      String dimension = meta.hasDimension() ? meta.dimension() : "unknown";
      if (dimension.contains(":")) {
        dimension = dimension.substring(dimension.indexOf(':') + 1);
      }

      // Level from progression data
      int level = 0;
      if (entry.npcData() != null && entry.npcData().contains("Progression")) {
        CompoundTag progression = entry.npcData().getCompound("Progression");
        level = progression.getInt("EntityExperienceLevel");
      }

      boolean isSpawned = !meta.hasRemovalReason();
      String status = isSpawned ? "§a✔ alive" : "§c✘ dead";

      info.append("  ")
        .append(name)
        .append(" §7[")
        .append(type)
        .append(" Lv.")
        .append(level)
        .append("]§r ")
        .append(status)
        .append(" §7@ ")
        .append(dimension);

      sendSuccessMessage(context, info.toString(), ChatFormatting.WHITE);
    }

    return SINGLE_SUCCESS;
  }
}
