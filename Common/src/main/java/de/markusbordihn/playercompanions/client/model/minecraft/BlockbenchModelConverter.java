package de.markusbordihn.playercompanions.client.model.minecraft;

import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchElement;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchGroup;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchModel;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchOutlinerNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BlockbenchModelConverter {

  public static MinecraftModel convert(BlockbenchModel blockbenchModel) {
    Map<String, BlockbenchElement> elementMap = new HashMap<>();
    for (BlockbenchElement elem : blockbenchModel.getElements()) {
      elementMap.put(elem.getUuid(), elem);
    }

    Map<String, BlockbenchGroup> groupMap = new HashMap<>();
    for (BlockbenchGroup grp : blockbenchModel.getGroups()) {
      groupMap.put(grp.getUuid(), grp);
    }

    List<MinecraftPart> rootParts = new ArrayList<>();
    for (BlockbenchOutlinerNode node : blockbenchModel.getOutliner()) {
      MinecraftPart part = convertNode(node, elementMap, groupMap, null);
      if (part != null) {
        rootParts.add(part);
      }
    }

    return new MinecraftModel(
      blockbenchModel.getTextureWidth(), blockbenchModel.getTextureHeight(), rootParts);
  }

  private static MinecraftPart convertNode(
    BlockbenchOutlinerNode node,
    Map<String, BlockbenchElement> elementMap,
    Map<String, BlockbenchGroup> groupMap,
    BlockbenchGroup parentGroup) {

    String uuid = node.getUuid();
    if (uuid == null) {
      return null;
    }

    BlockbenchGroup group = groupMap.get(uuid);
    if (group == null) {
      return null;
    }

    float[] groupOrigin = group.getOrigin();
    float[] mcOffset;
    if (parentGroup == null) {
      mcOffset = new float[]{groupOrigin[0], 24.0f - groupOrigin[1], groupOrigin[2]};
    } else {
      float[] parentOrigin = parentGroup.getOrigin();
      mcOffset = new float[]{
        groupOrigin[0] - parentOrigin[0],
        parentOrigin[1] - groupOrigin[1],
        groupOrigin[2] - parentOrigin[2]
      };
    }

    float[] bbRotation = group.getRotation();
    float[] mcRotation = new float[]{
      (float) Math.toRadians(bbRotation[0]),
      (float) Math.toRadians(bbRotation[1]),
      (float) Math.toRadians(bbRotation[2])
    };

    MinecraftPart part = new MinecraftPart(group.getName(), mcOffset, mcRotation);

    Map<String, List<BlockbenchElement>> rotatedElementGroups = new LinkedHashMap<>();
    List<BlockbenchElement> nonRotatedElements = new ArrayList<>();

    for (String elemUuid : node.getChildElementUuids()) {
      BlockbenchElement element = elementMap.get(elemUuid);
      if (element != null) {
        if (element.hasRotation()) {
          String rotationKey = getRotationKey(element, parentGroup);
          rotatedElementGroups.computeIfAbsent(rotationKey, k -> new ArrayList<>()).add(element);
        } else {
          nonRotatedElements.add(element);
        }
      }
    }

    for (BlockbenchElement element : nonRotatedElements) {
      convertElementDirect(element, part, group);
    }

    List<Map.Entry<String, List<BlockbenchElement>>> sortedGroups = new ArrayList<>(
      rotatedElementGroups.entrySet());
    sortedGroups.sort((a, b) -> Float.compare(
      a.getValue().get(0).getOrigin()[1],
      b.getValue().get(0).getOrigin()[1]));

    Map<String, Integer> baseNameIndices = new HashMap<>();

    for (Map.Entry<String, List<BlockbenchElement>> entry : sortedGroups) {
      List<BlockbenchElement> rotatedGroup = entry.getValue();
      if (!rotatedGroup.isEmpty()) {
        BlockbenchElement firstElement = rotatedGroup.get(0);
        final String firstElementName = firstElement.getName();
        boolean allSameName = rotatedGroup.stream()
          .allMatch(e -> e.getName().equals(firstElementName));
        String baseName = allSameName ? firstElementName : group.getName();
        int childIndex = baseNameIndices.getOrDefault(baseName, 0) + 1;
        baseNameIndices.put(baseName, childIndex);
        convertRotatedElementGroup(rotatedGroup, part, group, childIndex, baseName);
      }
    }

    for (BlockbenchOutlinerNode childNode : node.getChildNodes()) {
      MinecraftPart childPart = convertNode(childNode, elementMap, groupMap, group);
      if (childPart != null) {
        part.addChild(childPart);
      }
    }

    return part;
  }

  private static String getRotationKey(BlockbenchElement element, BlockbenchGroup parentGroup) {
    float[] bbRotation = element.getRotation();
    float[] bbOrigin = element.getOrigin();
    float[] groupOrigin = parentGroup != null ? parentGroup.getOrigin() : new float[]{0, 0, 0};

    return String.format("%s_%.4f_%.4f_%.4f_%.4f_%.4f_%.4f",
      element.getName(),
      bbRotation[0], bbRotation[1], bbRotation[2],
      bbOrigin[0] - groupOrigin[0], bbOrigin[1] - groupOrigin[1], bbOrigin[2] - groupOrigin[2]);
  }

  private static void convertRotatedElementGroup(List<BlockbenchElement> elements,
    MinecraftPart parentPart, BlockbenchGroup parentGroup, int childIndex, String baseName) {
    if (elements.isEmpty()) {
      return;
    }

    BlockbenchElement firstElement = elements.get(0);
    float[] bbOrigin = firstElement.getOrigin();
    float[] bbRotation = firstElement.getRotation();
    float[] groupOrigin = parentGroup.getOrigin();

    float offsetX = -(bbOrigin[0] - groupOrigin[0]);
    float yRotRad = (float) Math.toRadians(bbRotation[1]);
    if (Math.abs(yRotRad + Math.PI) < 0.01f) {
      yRotRad = (float) Math.PI;
    }

    MinecraftPart childPart = new MinecraftPart(
      baseName + "_r" + childIndex,
      new float[]{offsetX, -(bbOrigin[1] - groupOrigin[1]), bbOrigin[2] - groupOrigin[2]},
      new float[]{
        -(float) Math.toRadians(bbRotation[0]),
        yRotRad,
        (float) Math.toRadians(bbRotation[2])
      }
    );

    for (BlockbenchElement element : elements) {
      float[] from = element.getFrom();
      float[] to = element.getTo();
      float[] elemOrigin = element.getOrigin();

      childPart.addCube(new MinecraftCube(
        element.getUvOffset(),
        new float[]{-(to[0] - elemOrigin[0]), -(to[1] - elemOrigin[1]), from[2] - elemOrigin[2]},
        new float[]{to[0] - from[0], to[1] - from[1], to[2] - from[2]},
        element.isMirrorUv()
      ));
    }

    parentPart.addChild(childPart);
  }

  private static void convertElementDirect(BlockbenchElement element, MinecraftPart parentPart,
    BlockbenchGroup parentGroup) {
    float[] from = element.getFrom();
    float[] to = element.getTo();
    float[] groupOrigin = parentGroup.getOrigin();

    parentPart.addCube(new MinecraftCube(
      element.getUvOffset(),
      new float[]{-(to[0] - groupOrigin[0]), -(to[1] - groupOrigin[1]), from[2] - groupOrigin[2]},
      new float[]{to[0] - from[0], to[1] - from[1], to[2] - from[2]},
      element.isMirrorUv()
    ));
  }
}
