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

package de.markusbordihn.playercompanions.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.utils.PlayersUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModTextureManager {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String TEXTURE_PREFIX = Constants.MOD_ID + "_client_texture_";

  private static Path textureCachePath = null;
  private static HashMap<String, ResourceLocation> textureCache = new HashMap<>();

  protected ModTextureManager() {}

  private static ResourceLocation addTexture(String name, File file) {
    if (hasTexture(name)) {
      log.warn(
          "Texture with name {} already exists! Unable to add texture for file: {}", name, file);
      return getTexture(name);
    }
    Minecraft client = Minecraft.getInstance();
    TextureManager textureManager = client.getTextureManager();

    // Creative native image from file.
    NativeImage nativeImage = getNativeImage(file);
    if (nativeImage == null) {
      log.error("Unable to create native image for file {}.", file);
      return null;
    }

    // Creative dynamic texture from native image.
    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);

    // Register dynamic texture under resource location.
    String textureId = getId(name);
    ResourceLocation resourceLocation = textureManager.register(textureId, dynamicTexture);
    log.debug(
        "Registered image {} as texture {} with {} to texture Manager.",
        nativeImage,
        dynamicTexture,
        resourceLocation);

    // Store resource location by id into cache.
    textureCache.put(textureId, resourceLocation);
    return resourceLocation;
  }

  public static ResourceLocation addTexture(String remoteUrl) {
    if (!PlayersUtils.isValidUrl(remoteUrl)) {
      log.error("Texture URL {} is invalid!", remoteUrl);
      return null;
    }
    if (hasTexture(remoteUrl)) {
      log.debug("Texture with {} already exists, will used cached version instead!", remoteUrl);
      return getTexture(remoteUrl);
    }

    String fileName = getFileName(remoteUrl);
    File file = new File(getTextureCacheDirectoryString(), fileName);
    if (file.exists()) {
      log.debug("Found downloaded file in cache, will re-used file {} for {}", file, remoteUrl);
      return addTexture(remoteUrl, file);
    }

    // Download URL to memory
    BufferedImage image;
    try {
      image = ImageIO.read(new URL(remoteUrl));
    } catch (IllegalArgumentException | IOException exception) {
      log.error("Unable to load texture from {} because of:", remoteUrl, exception);
      return null;
    }

    // Verify the image data to make sure we got a valid image!
    if (image == null) {
      log.error("Unable to get any valid texture from {}!", remoteUrl);
      return null;
    } else if (image.getWidth() < 32
        || image.getHeight() < 32
        || image.getWidth() % 32 != 0
        || image.getHeight() % 32 != 0) {
      log.error(
          "Unable to get any valid texture from {}, got {}x{}!",
          remoteUrl,
          image.getWidth(),
          image.getHeight());
      return null;
    }

    // Storing file to cache.
    try {
      ImageIO.write(image, "png", file);
    } catch (IllegalArgumentException | IOException exception) {
      log.error("Unable to store texture from {} to {} because of:", remoteUrl, file, exception);
      return null;
    }

    // Adding file to texture manager.
    return addTexture(remoteUrl, file);
  }

  public static String getId(String name) {
    return TEXTURE_PREFIX + name.hashCode();
  }

  public static String getFileName(String name) {
    return String.format("%s.png", getId(name)).replace("-", "0");
  }

  public static boolean hasTexture(String name) {
    return textureCache.containsKey(getId(name));
  }

  public static ResourceLocation getTexture(String name) {
    return textureCache.get(getId(name));
  }

  public static String getTextureCacheDirectoryString() {
    return getTextureCacheDirectory().toString();
  }

  public static Path getTextureCacheDirectory() {
    if (textureCachePath == null) {
      Path cacheDirectory =
          Paths.get(FMLPaths.GAMEDIR.get().resolve(Constants.MOD_ID).toString(), "texture_cache");
      if (!cacheDirectory.toFile().exists()) {
        log.info("Creating texture cache directory at {}", cacheDirectory);
        FileUtils.getOrCreateDirectory(cacheDirectory, Constants.MOD_ID);
      }
      textureCachePath = cacheDirectory;
    }
    return textureCachePath;
  }

  public static NativeImage getNativeImage(File file) {
    NativeImage nativeImage;
    try {
      InputStream inputStream = new FileInputStream(file);
      nativeImage = NativeImage.read(inputStream);
      inputStream.close();
    } catch (Exception exception) {
      log.error("Unable to get native image for file {} because of:", file, exception);
      return null;
    }

    if (nativeImage.getWidth() == 64 && nativeImage.getHeight() == 32) {
      log.info("Processing legacy image {} from 64x32 to 64x64 ...", nativeImage);
      nativeImage = getNativeImageFromLegacyImage(nativeImage);
    }

    return nativeImage;
  }

  public static NativeImage getNativeImageFromLegacyImage(NativeImage legacyNativeImage) {
    NativeImage nativeImage = new NativeImage(64, 64, true);
    nativeImage.copyFrom(legacyNativeImage);
    legacyNativeImage.close();
    nativeImage.fillRect(0, 32, 64, 32, 0);
    nativeImage.copyRect(4, 16, 16, 32, 4, 4, true, false);
    nativeImage.copyRect(8, 16, 16, 32, 4, 4, true, false);
    nativeImage.copyRect(0, 20, 24, 32, 4, 12, true, false);
    nativeImage.copyRect(4, 20, 16, 32, 4, 12, true, false);
    nativeImage.copyRect(8, 20, 8, 32, 4, 12, true, false);
    nativeImage.copyRect(12, 20, 16, 32, 4, 12, true, false);
    nativeImage.copyRect(44, 16, -8, 32, 4, 4, true, false);
    nativeImage.copyRect(48, 16, -8, 32, 4, 4, true, false);
    nativeImage.copyRect(40, 20, 0, 32, 4, 12, true, false);
    nativeImage.copyRect(44, 20, -8, 32, 4, 12, true, false);
    nativeImage.copyRect(48, 20, -16, 32, 4, 12, true, false);
    nativeImage.copyRect(52, 20, -8, 32, 4, 12, true, false);
    return nativeImage;
  }
}
