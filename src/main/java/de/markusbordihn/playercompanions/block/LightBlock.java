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

package de.markusbordihn.playercompanions.block;

import java.util.Random;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import de.markusbordihn.playercompanions.Constants;

public class LightBlock extends Block {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Block state properties.
  public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
  public static final IntegerProperty AGE = BlockStateProperties.AGE_15;

  protected static final VoxelShape SHAPE_AABB = Block.box(7.5D, 7.5D, 7.5D, 8.5D, 8.5D, 8.5D);

  public static final int TICK_TTL = 30;
  public static final int UPDATE_TICK_TTL = 20;
  public static final int VERIFY_TICK_TTL = 100;

  public LightBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(
        this.stateDefinition.any().setValue(AGE, 0).setValue(EXTENDED, false));
  }

  public static int getLightLevel(BlockState blockState) {
    int age = blockState.getValue(AGE);
    return 15 - age;
  }

  public void rescheduleTick(Level level, BlockState blockState, BlockPos blockPos) {
    if (level.getBlockTicks().hasScheduledTick(blockPos, this)
        && Boolean.FALSE.equals(blockState.getValue(EXTENDED))) {
      level.setBlock(blockPos, blockState.setValue(EXTENDED, true).setValue(AGE, 0), 3);
    }
  }

  public void scheduleTick(Level level, BlockPos blockPos) {
    if (!level.getBlockTicks().hasScheduledTick(blockPos, this)) {
      level.scheduleTick(blockPos, this, TICK_TTL);
    }
  }

  public void scheduleUpdateTick(Level level, BlockPos blockPos) {
    if (!level.getBlockTicks().hasScheduledTick(blockPos, this)) {
      level.scheduleTick(blockPos, this, UPDATE_TICK_TTL);
    }
  }

  public void scheduleVerifyTick(Level level, BlockPos blockPos) {
    if (!level.getBlockTicks().hasScheduledTick(blockPos, this)) {
      level.scheduleTick(blockPos, this, VERIFY_TICK_TTL);
    }
  }

  public static void place(Level level, BlockPos blockPos) {
    if (level.isClientSide) {
      return;
    }

    BlockState blockState = level.getBlockState(blockPos);
    // Check above positions, if we can't place the block directly.
    if (!blockState.isAir() && !(blockState.getBlock() instanceof LightBlock)) {
      blockPos = blockPos.above();
      blockState = level.getBlockState(blockPos);
    }

    // Check alternative position, if needed.
    if (!blockState.isAir() && !(blockState.getBlock() instanceof LightBlock)) {
      if (canPlace(level, blockPos.above())) {
        blockPos = blockPos.above();
      } else if (canPlace(level, blockPos.north())) {
        blockPos = blockPos.north();
      } else if (canPlace(level, blockPos.east())) {
        blockPos = blockPos.east();
      } else if (canPlace(level, blockPos.south())) {
        blockPos = blockPos.south();
      } else if (canPlace(level, blockPos.west())) {
        blockPos = blockPos.west();
      }
    }

    // Final check, before we give up.
    blockState = level.getBlockState(blockPos);
    if (blockState.isAir()) {
      level.setBlock(blockPos, ModBlocks.LIGHT_BLOCK.get().defaultBlockState(), 3);
      if (level.getBlockState(blockPos).getBlock() instanceof LightBlock lightBlock) {
        lightBlock.scheduleTick(level, blockPos);
      }
    } else if (blockState.getBlock() instanceof LightBlock lightBlock) {
      // Avoid replacing existing block and just extend the removal tick instead.
      lightBlock.rescheduleTick(level, blockState, blockPos);
    }
  }

  private static boolean canPlace(Level level, BlockPos blockPos) {
    BlockState blockState = level.getBlockState(blockPos);
    return blockState.isAir() || blockState.getBlock() instanceof LightBlock;
  }

  /** @deprecated */
  @Deprecated
  @Override
  public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
      CollisionContext collisionContext) {
    return SHAPE_AABB;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockState) {
    blockState.add(AGE, EXTENDED);
  }

  @Override
  public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState,
      @Nullable LivingEntity placer, ItemStack itemStack) {
    scheduleTick(level, blockPos);
  }

  /** @deprecated */
  @Deprecated
  @Override
  public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, Random random) {
    // Ignore client side.
    if (level.isClientSide) {
      return;
    }

    // Ignore other blocks than LightBlock.
    Block block = blockState.getBlock();
    if (!(block instanceof LightBlock)) {
      return;
    }

    if (Boolean.TRUE.equals(blockState.getValue(EXTENDED))) {
      // Extend block removal tick, if block is on the same position.
      level.setBlock(blockPos, blockState.setValue(EXTENDED, false), 3);
      scheduleTick(level, blockPos);
    } else {
      // Fade out block and remove it, if it is not extended.
      int age = blockState.getValue(AGE);
      if (age >= 15) {
        level.removeBlock(blockPos, true);
      } else {
        level.setBlock(blockPos, blockState.setValue(AGE, age + 1), 3);
        scheduleUpdateTick(level, blockPos);
      }
    }
  }

  /** @deprecated */
  @Deprecated
  @Override
  public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos,
      Random random) {
    // Makes sure that the blocks is removed after a while, if it is not extended.
    int age = blockState.getValue(AGE);
    if (age <= 15 && !serverLevel.getBlockTicks().hasScheduledTick(blockPos, this)) {
      scheduleVerifyTick(serverLevel, blockPos);
    }
  }

}
