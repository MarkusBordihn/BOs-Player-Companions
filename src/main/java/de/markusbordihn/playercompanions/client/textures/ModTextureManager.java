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
import java.util.regex.Pattern;
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

@OnlyIn(Dist.CLIENT)
public class ModTextureManager {

  /**
   * The texture manager is taking care of the following use cases:
   * 1. Texture as resource directly inside the mod back
   * 2. Texture as resource from Minecraft profile
   * 3. Texture as resource from 3rd party webpage like SKIndex, NovaSkin, Planet Minecraft
   */

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String URL_REGEX = "^(((https?)://)"
      + "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])+)" + "([).!';/?:,][[:blank:]])?$";
  private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
  private static final String TEXTURE_PREFIX = Constants.MOD_ID + "_client_texture_";

  private static Path TEXTURE_CACHE_PATH = null;
  private static HashMap<String, ResourceLocation> textureCache =
      new HashMap<String, ResourceLocation>();

  protected ModTextureManager() {}

  public static void addTexture(String name, File file) {
    String textureId = getId(name);
    log.info("Adding Texture {} with {} as {} ...", name, file, textureId);
    textureCache.computeIfAbsent(name, (key) -> {
      return null;
    });
    Minecraft client = Minecraft.getInstance();
    TextureManager textureManager = client.getTextureManager();
    NativeImage nativeImage = getNativeImage(file);
    if (nativeImage == null) {
      return;
    }
    log.info("Native Image: {}", nativeImage);
    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
    log.info("Dynamic Texture: {}", dynamicTexture);
    ResourceLocation resourceLocation =
        textureManager.register(textureId, dynamicTexture);
    log.info("Add Texture as {} to texture Manager.", resourceLocation);
    textureCache.put(textureId, resourceLocation);
  }

  public static void addTexture(String name, String remoteUrl) {
    if (textureCache.containsKey(name)) {
      log.error("Texture with name {} already exists!", name);
      return;
    }
    if (!URL_PATTERN.matcher(remoteUrl).matches()) {
      log.error("Texture URL for name {} is invalid: {}", name, remoteUrl);
      return;
    }
    log.info("Loading texture {} from {} ...", name, remoteUrl);

    // Download URL to memory
    BufferedImage image;
    try {
      image = ImageIO.read(new URL(remoteUrl));
    } catch (IllegalArgumentException | IOException exception) {
      log.error("Unable to load texture {} from {} because of:", name, remoteUrl, exception);
      return;
    }

    // Verify the image data to make sure we got a valid image!
    if (image == null) {
      log.error("Unable to get any valid texture from {}!", remoteUrl);
      return;
    } else if (image.getWidth() < 32 || image.getHeight() < 32
        || image.getWidth() != image.getHeight()) {
      log.error("Unable to get any valid texture from {}, got {}x{}!", remoteUrl, image.getWidth(),
          image.getHeight());
      return;
    }

    // Storing file to cache.
    Path cacheDirectory = getTextureCacheDirectory();
    File file = new File(cacheDirectory.toString(), String.format("%s.png", name));
    try {
      ImageIO.write(image, "png", file);
    } catch (IllegalArgumentException | IOException exception) {
      log.error("Unable to store texture {} from {} to {} because of:", name, file, remoteUrl,
          exception);
      return;
    }

    // Adding file to texture manager.
    addTexture(name, file);
  }

  public static String getId(String name) {
    return TEXTURE_PREFIX + name;
  }

  public static ResourceLocation getTexture(String name) {
    String textureId = getId(name);
    log.info("getTexture: {}", textureId);
    return textureCache.get(textureId);
  }

  public static Path getTextureCacheDirectory() {
    if (TEXTURE_CACHE_PATH == null) {
      Path cacheDirectory =
          Paths.get(FMLPaths.GAMEDIR.get().resolve(Constants.MOD_ID).toString(), "texture_cache");
      if (!cacheDirectory.toFile().exists()) {
        log.info("Creating texture cache directory at {}", cacheDirectory);
        FileUtils.getOrCreateDirectory(cacheDirectory, Constants.MOD_ID);
      }
      TEXTURE_CACHE_PATH = cacheDirectory;
    }
    return TEXTURE_CACHE_PATH;
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
