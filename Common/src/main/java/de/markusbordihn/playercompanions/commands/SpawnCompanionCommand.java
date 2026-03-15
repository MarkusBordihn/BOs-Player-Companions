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
import de.markusbordihn.easynpc.data.saveddata.NPCEntityData;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class SpawnCompanionCommand extends CompanionCommand {

  private static final String UUID_ARG = "uuid";

  private SpawnCompanionCommand() {
  }

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("spawn")
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
                  .filter(e -> e.metadata().hasRemovalReason())
                  .map(e -> e.entityUUID().toString())
                  .forEach(builder::suggest);
              } catch (Exception ignored) {
              }
              return SharedSuggestionProvider.suggest(new String[0], builder);
            })
          .executes(
            context ->
              spawn(
                context.getSource(),
                StringArgumentType.getString(context, UUID_ARG))));
  }

  private static int spawn(CommandSourceStack context, String uuidString) {
    UUID uuid = DespawnCompanionCommand.parseUUID(context, uuidString);
    if (uuid == null) {
      return FAILURE;
    }

    if (!DespawnCompanionCommand.isOwner(context, uuid)) {
      return sendFailureMessage(context, "You do not own companion " + uuid + ".");
    }

    Optional<NPCEntityMetadata> meta =
      NPCEntityData.get(context.getServer()).getMetadata(uuid);
    if (meta.isEmpty()) {
      return sendFailureMessage(context, "No saved data found for companion " + uuid + ".");
    }

    boolean bypassCooldown = context.hasPermission(Commands.LEVEL_GAMEMASTERS);
    if (!bypassCooldown) {
      try {
        ServerPlayer player = context.getPlayerOrException();
        bypassCooldown = player.isCreative();
      } catch (Exception ignored) {
      }
    }

    if (!bypassCooldown) {
      long gameTime = context.getLevel().getGameTime();
      if (!CompanionSpawnCooldown.canSpawn(uuid, gameTime)) {
        long remaining = CompanionSpawnCooldown.remainingTicks(uuid, gameTime);
        long remainingSeconds = remaining / 20L;
        if (CompanionSpawnCooldown.wasDead(uuid)) {
          return sendFailureMessage(
            context,
            "Companion "
              + uuid
              + " died recently. Wait "
              + remainingSeconds
              + " more seconds before respawning.");
        } else {
          return sendFailureMessage(
            context,
            "Companion "
              + uuid
              + " cannot be spawned yet. Wait "
              + remainingSeconds
              + " more seconds.");
        }
      }
    }

    ServerPlayer spawnTarget;
    try {
      spawnTarget = context.getPlayerOrException();
    } catch (Exception e) {
      return sendFailureMessage(context, "This command must be run by a player.");
    }

    ServerLevel serverLevel = (ServerLevel) spawnTarget.level();

    if (EasyNPCEntityHandler.spawn(uuid, serverLevel, spawnTarget.position())) {
      CompanionSpawnCooldown.recordSpawn(uuid, serverLevel.getGameTime());
      return sendSuccessMessage(context, "Companion " + uuid + " spawned.");
    }
    return sendFailureMessage(context, "Failed to spawn companion " + uuid + ".");
  }
}
