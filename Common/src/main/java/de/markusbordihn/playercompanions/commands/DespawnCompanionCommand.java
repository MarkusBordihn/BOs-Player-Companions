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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.markusbordihn.easynpc.api.handler.EasyNPCEntityHandler;
import de.markusbordihn.easynpc.data.npc.NPCEntityMetadata;
import de.markusbordihn.easynpc.data.npc.NPCRemovalReason;
import de.markusbordihn.easynpc.data.saveddata.NPCEntityData;
import de.markusbordihn.easynpc.entity.LivingEntityManager;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class DespawnCompanionCommand extends CompanionCommand {

  private static final String UUID_ARG = "uuid";

  private DespawnCompanionCommand() {
  }

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("despawn")
      .requires(cs -> cs.hasPermission(Commands.LEVEL_ALL))
      .then(
        Commands.argument(UUID_ARG, StringArgumentType.string())
          .suggests(
            (context, builder) -> {
              try {
                ServerPlayer player = context.getSource().getPlayerOrException();
                MinecraftServer server = context.getSource().getServer();
                NPCEntityData.get(server)
                  .getEntriesByOwner(player.getUUID())
                  .stream()
                  .map(e -> e.entityUUID().toString())
                  .forEach(builder::suggest);
              } catch (Exception ignored) {
              }
              return SharedSuggestionProvider.suggest(new String[0], builder);
            })
          .executes(
            context ->
              despawn(
                context.getSource(),
                StringArgumentType.getString(context, UUID_ARG))));
  }

  private static int despawn(CommandSourceStack context, String uuidString) {
    UUID uuid = parseUUID(context, uuidString);
    if (uuid == null) {
      return FAILURE;
    }

    if (!isOwner(context, uuid)) {
      return sendFailureMessage(context, "You do not own companion " + uuid + ".");
    }

    EasyNPC<?> easyNPC =
      LivingEntityManager.getEasyNPCEntityByUUID(uuid, context.getLevel());
    if (easyNPC == null) {
      return sendFailureMessage(
        context, "Companion " + uuid + " not found or not currently in world.");
    }

    if (EasyNPCEntityHandler.despawn(easyNPC, NPCRemovalReason.UNLOADED_BY_PLAYER)) {
      return sendSuccessMessage(
        context,
        "Companion " + easyNPC.getEntity().getDisplayName().getString() + " despawned.");
    }
    return sendFailureMessage(context, "Failed to despawn companion " + uuid + ".");
  }

  static UUID parseUUID(CommandSourceStack context, String uuidString) {
    try {
      return UUID.fromString(uuidString);
    } catch (IllegalArgumentException e) {
      sendFailureMessage(context, "Invalid UUID: " + uuidString);
      return null;
    }
  }

  static boolean isOwner(CommandSourceStack context, UUID companionUUID) {
    if (context.hasPermission(Commands.LEVEL_GAMEMASTERS)) {
      return true;
    }
    try {
      ServerPlayer player = context.getPlayerOrException();
      if (player.isCreative()) {
        return true;
      }
      Optional<NPCEntityMetadata> meta =
        NPCEntityData.get(context.getServer()).getMetadata(companionUUID);
      return meta.isPresent()
        && meta.get().hasOwner()
        && meta.get().ownerUUID().equals(player.getUUID());
    } catch (Exception e) {
      return false;
    }
  }
}
