/**
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FileUtils;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.utils.PlayersUtils;

@OnlyIn(Dist.CLIENT)
public class ModTextureManager {

  /**
   * The texture manager is taking care of the following use cases: 1. Texture as resource directly
   * inside the mod back 2. Texture as resource from Minecraft profile 3. Texture as resource from
   * 3rd party webpage like SKIndex, NovaSkin, Planet Minecraft
   */

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String TEXTURE_PREFIX = Constants.MOD_ID + "_client_texture_";

  private static Path textureCachePath = null;
  private static HashMap<String, ResourceLocation> textureCache = new HashMap<>();

  protected ModTextureManager() {}

  public static ResourceLocation addTexture(String name, File file) {
    String textureId = getId(name);
    Minecraft client = Minecraft.getInstance();
    TextureManager textureManager = client.getTextureManager();
    NativeImage nativeImage = getNativeImage(file);
    if (nativeImage == null) {
      log.error("Unable to create native image for file {}.", file);
      return null;
    }
    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
    ResourceLocation resourceLocation = textureManager.register(textureId, dynamicTexture);
    log.debug("Registered image {} as texture {} as {} to texture Manager.", nativeImage,
        dynamicTexture, resourceLocation);
    textureCache.put(textureId, resourceLocation);
    return resourceLocation;
  }

  public static ResourceLocation addTexture(String remoteUrl) {
    return addTexture(remoteUrl, remoteUrl);
  }

  public static ResourceLocation addTexture(String name, String remoteUrl) {
    if (hasTexture(name)) {
      log.error("Texture with name {} already exists!", name);
      return getTexture(name);
    }
    if (!PlayersUtils.isValidUrl(remoteUrl)) {
      log.error("Texture URL for name {} is invalid: {}", name, remoteUrl);
      return null;
    }

    String fileName = getFileName(name);
    Path cacheDirectory = getTextureCacheDirectory();
    File file = new File(cacheDirectory.toString(), fileName);
    if (file.exists()) {
      log.debug("Found downloaded file in cache, will re-used file {} for {}", file, name);
      return addTexture(name, file);
    }

    // Download URL to memory
    BufferedImage image;
    try {
      image = ImageIO.read(new URL(remoteUrl));
    } catch (IllegalArgumentException | IOException exception) {
      log.error("Unable to load texture {} from {} because of:", name, remoteUrl, exception);
      return null;
    }

    // Verify the image data to make sure we got a valid image!
    if (image == null) {
      log.error("Unable to get any valid texture from {}!", remoteUrl);
      return null;
    } else if (image.getWidth() < 32 || image.getHeight() < 32
        || image.getWidth() != image.getHeight()) {
      log.error("Unable to get any valid texture from {}, got {}x{}!", remoteUrl, image.getWidth(),
          image.getHeight());
      return null;
    }

    // Storing file to cache.
    try {
      ImageIO.write(image, "png", file);
    } catch (IllegalArgumentException | IOException exception) {
      log.error("Unable to store texture {} from {} to {} because of:", name, file, remoteUrl,
          exception);
      return null;
    }

    // Adding file to texture manager.
    return addTexture(name, file);
  }

  public static String getId(String name) {
    return TEXTURE_PREFIX + name.hashCode();
  }

  public static String getFileName(String name) {
    return String.format("%s.png", name.hashCode());
  }

  public static boolean hasTexture(String name) {
    String textureId = getId(name);
    return textureCache.containsKey(textureId);
  }

  public static ResourceLocation getTexture(String name) {
    String textureId = getId(name);
    return textureCache.get(textureId);
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
    try {
      InputStream inputStream = new FileInputStream(file);
      NativeImage nativeImage = NativeImage.read(inputStream);
      inputStream.close();
      return nativeImage;
    } catch (Exception exception) {
      log.error("Unable to get native image for file {} because of:", file, exception);
      return null;
    }
  }

}
