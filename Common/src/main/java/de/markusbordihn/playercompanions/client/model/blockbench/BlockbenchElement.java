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

package de.markusbordihn.playercompanions.client.model.blockbench;

public class BlockbenchElement {

  private final String uuid;
  private final String name;
  private final float[] from;
  private final float[] to;
  private final int[] uvOffset;
  private final float[] origin;
  private final float[] rotation;
  private final boolean mirrorUv;

  public BlockbenchElement(
    String uuid,
    String name,
    float[] from,
    float[] to,
    int[] uvOffset,
    float[] origin,
    float[] rotation,
    boolean mirrorUv) {
    this.uuid = uuid;
    this.name = name;
    this.from = from;
    this.to = to;
    this.uvOffset = uvOffset;
    this.origin = origin;
    this.rotation = rotation;
    this.mirrorUv = mirrorUv;
  }

  public String getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public float[] getFrom() {
    return from;
  }

  public float[] getTo() {
    return to;
  }

  public int[] getUvOffset() {
    return uvOffset;
  }

  public float[] getOrigin() {
    return origin;
  }

  public float[] getRotation() {
    return rotation;
  }

  public boolean isMirrorUv() {
    return mirrorUv;
  }

  public boolean hasRotation() {
    return rotation != null && (rotation[0] != 0 || rotation[1] != 0 || rotation[2] != 0);
  }
}
