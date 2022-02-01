package de.markusbordihn.playercompanions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CompanionGhostBlock extends Block {

  protected static final VoxelShape SHAPE_AABB = Block.box(1.0, 1.0, 6.0, 15.0, 16.0, 9.0);

  public CompanionGhostBlock(BlockBehaviour.Properties properties) {
    super(properties);
  }

  @Override
  public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
      CollisionContext collisionContext) {
    return SHAPE_AABB;
  }

}
