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

package de.markusbordihn.playercompanions.client.gui;

import com.mojang.blaze3d.platform.Window;

import net.minecraft.client.Minecraft;

public class PositionManager {

  private static int HOTBAR_RIGHT = 90;
  private static int HOTBAR_LEFT = -90;

  private Window window;
  private int guiScaledWidth;
  private int guiScaledHeight;
  private int width = 100;
  private int height = 100;
  private PositionPoint position = new PositionPoint(0, 0);

  protected PositionManager() {}

  public void setInstance(Minecraft minecraft) {
    this.window = minecraft.getWindow();
    updateWindow();
  }

  public PositionPoint getTopLeft() {
    return new PositionPoint(0, 0);
  }

  public PositionPoint getTopRight() {
    return new PositionPoint(this.guiScaledWidth - this.width, 0);
  }

  public PositionPoint getBottomLeft() {
    return new PositionPoint(0, this.guiScaledHeight - this.height);
  }

  public PositionPoint getBottomRight() {
    return new PositionPoint(this.guiScaledWidth - this.width, this.guiScaledHeight - this.height);
  }

  public PositionPoint getHotbarLeft() {
    return new PositionPoint(this.guiScaledWidth / 2 + HOTBAR_LEFT - this.width,
        this.guiScaledHeight - this.height);
  }

  public PositionPoint getHotbarRight() {
    return new PositionPoint(this.guiScaledWidth / 2 + HOTBAR_RIGHT,
        this.guiScaledHeight - this.height);
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public PositionPoint getPosition() {
    return position;
  }

  public int getPositionX() {
    return position.getX();
  }

  public int getPositionY() {
    return position.getY();
  }

  public void setPosition(PositionPoint position) {
    this.position = position;
  }

  public void updateWindow() {
    if (window == null) {
      return;
    }
    int currentGuiScaledWidth = window.getGuiScaledWidth();
    int currentGuiScaleHeight = window.getGuiScaledHeight();
    if (guiScaledWidth == currentGuiScaledWidth && guiScaledHeight == currentGuiScaleHeight) {
      return;
    }
    guiScaledWidth = currentGuiScaledWidth;
    guiScaledHeight = currentGuiScaleHeight;
  }

}
