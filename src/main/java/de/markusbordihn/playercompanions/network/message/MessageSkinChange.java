/**
 * Copyright 2023 Markus Bordihn
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

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.network.NetworkEvent;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.skin.SkinType;
import de.markusbordihn.playercompanions.utils.PlayersUtils;

public class MessageSkinChange {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected final UUID uuid;
  protected final String skin;
  protected final String skinURL;
  protected final UUID skinUUID;
  protected final SkinType skinType;

  public MessageSkinChange(UUID uuid, String skin, String skinURL, UUID skinUUID,
      SkinType skinType) {
    this.uuid = uuid;
    this.skin = skin;
    this.skinURL = skinURL;
    this.skinUUID = skinUUID;
    this.skinType = skinType;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public String getSkin() {
    return this.skin;
  }

  public String getSkinURL() {
    return this.skinURL;
  }

  public UUID getSkinUUID() {
    return this.skinUUID;
  }

  public SkinType getSkinType() {
    return this.skinType;
  }

  public static MessageSkinChange decode(final FriendlyByteBuf buffer) {
    return new MessageSkinChange(buffer.readUUID(), buffer.readUtf(), buffer.readUtf(),
        buffer.readUUID(), buffer.readEnum(SkinType.class));
  }

  public static void encode(final MessageSkinChange message, final FriendlyByteBuf buffer) {
    buffer.writeUUID(message.uuid);
    buffer.writeUtf(message.skin);
    buffer.writeUtf(message.skinURL);
    buffer.writeUUID(message.skinUUID);
    buffer.writeEnum(message.getSkinType());
  }

  public static void handle(MessageSkinChange message,
      Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    context.enqueueWork(() -> handlePacket(message, context));
    context.setPacketHandled(true);
  }

  public static void handlePacket(MessageSkinChange message, NetworkEvent.Context context) {
    ServerPlayer serverPlayer = context.getSender();
    if (serverPlayer == null) {
      log.error("Unable to get server player for message {} from {}", message, context);
      return;
    }
    ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
    UUID uuid = message.getUUID();

    // Validate skin.
    String skin = message.getSkin();
    if (skin == null) {
      log.error("Invalid skin {} for {} from {}", skin, message, serverPlayer);
      return;
    }

    // Only accepts commands from owner, log attempts.
    Entity entity = serverLevel.getEntity(uuid);
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity
        && !serverPlayer.getUUID().equals(playerCompanionEntity.getOwnerUUID())) {
      log.error("Player {} tried to change skin {} ({}) for unowned {}", serverPlayer, skin,
          message.getSkin(), playerCompanionEntity);
      return;
    }

    // Validate skin type.
    SkinType skinType = message.getSkinType();
    if (skinType == null) {
      log.error("Invalid skin type {} for {} from {}", skinType, message, serverPlayer);
      return;
    }

    // Validate entity.
    PlayerCompanionEntity playerCompanionEntity = (PlayerCompanionEntity) entity;
    if (playerCompanionEntity == null) {
      log.error("Unable to get player companion entity for {} from {}", message, serverPlayer);
      return;
    }

    // Perform action.
    String skinURL = message.getSkinURL();
    UUID skinUUID = message.getSkinUUID();

    // Pre-process skin information, if needed.
    log.debug("Processing skin:{} uuid:{} url:{} type:{} model:{} for {} from {}", skin, skinUUID,
        skinURL, skinType, playerCompanionEntity.getSkinModel(), playerCompanionEntity,
        serverPlayer);

    switch (skinType) {
      case PLAYER_SKIN:
        playerCompanionEntity.setSkinType(skinType);
        playerCompanionEntity.setSkinURL(skinURL != null && !skinURL.isBlank() ? skinURL : "");
        if (skinUUID != null && !Constants.BLANK_UUID.equals(skinUUID)) {
          playerCompanionEntity.setSkinUUID(skinUUID);
        } else {
          UUID userUUID = PlayersUtils.getUserUUID(serverPlayer.getServer(), skin);
          if (!skin.equals(userUUID.toString())) {
            log.debug("Converted user {} to UUID {} ...", skin, userUUID);
          }
          playerCompanionEntity.setSkinUUID(userUUID);
        }
        break;
      case INSECURE_REMOTE_URL:
      case SECURE_REMOTE_URL:
        playerCompanionEntity.setSkinType(skinType);
        playerCompanionEntity.setSkinURL(skinURL != null && !skinURL.isBlank() ? skinURL : skin);
        playerCompanionEntity
            .setSkinUUID(skinUUID != null && !Constants.BLANK_UUID.equals(skinUUID) ? skinUUID
                : UUID.nameUUIDFromBytes(skin.getBytes()));
        break;
      default:
        log.error("Failed processing skin:{} uuid:{} url:{} type:{} for {} from {}", skin, skinUUID,
            skinURL, skinType, playerCompanionEntity, serverPlayer);
    }
  }

}
