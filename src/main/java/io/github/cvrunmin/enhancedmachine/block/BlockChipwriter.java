package io.github.cvrunmin.enhancedmachine.block;

import io.github.cvrunmin.enhancedmachine.inventory.ChipWriterContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.HorizontalFaceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockChipwriter extends HorizontalBlock {

    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private final AxisAlignedBB BASE_AABB = new AxisAlignedBB(1 / 16d, 0, 1 / 16d, 15 / 16d, 5 / 16d, 15 / 16d);
    private AxisAlignedBB HIGHLIGHT_AABB = new AxisAlignedBB(1 / 16d, 0, 1 / 16d, 15 / 16d, 14 / 16d, 15 / 16d);
    private AxisAlignedBB STAND_AABB = (new AxisAlignedBB(7 / 16d, 5 / 16d, 1 / 16d, 9 / 16d, 14 / 16d, 9 / 16d));
    private static final TranslationTextComponent CONTAINER_NAME = new TranslationTextComponent("container.chipwriter_title");

    private VoxelShape shape = VoxelShapes.or(Block.makeCuboidShape(1,0,1,15,5,15), Block.makeCuboidShape(4,5,1,12,16,7));
    private VoxelShape shape1 = VoxelShapes.or(Block.makeCuboidShape(1,0,1,15,5,15), Block.makeCuboidShape(4,5, 8, 12,16, 15));
    private VoxelShape shape2 = VoxelShapes.or(Block.makeCuboidShape(1,0,1,15,5,15), Block.makeCuboidShape(1,5, 4, 7,16, 12));
    private VoxelShape shape3 = VoxelShapes.or(Block.makeCuboidShape(1,0,1,15,5,15), Block.makeCuboidShape(8,5, 4, 15,16, 12));

    public BlockChipwriter() {
        super(Properties.create(Material.IRON));
        this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(HORIZONTAL_FACING);
        switch (direction){
            case NORTH:
                return shape;
            case SOUTH:
                return shape1;
            case WEST:
                return shape2;
            case EAST:
                return shape3;
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(HORIZONTAL_FACING);
        switch (direction){
            case NORTH:
                return shape;
            case SOUTH:
                return shape1;
            case WEST:
                return shape2;
            case EAST:
                return shape3;
        }
        return shape;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(state.getContainer(worldIn, pos));
            return ActionResultType.SUCCESS;
        }
    }

    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((windowId, playerInventory, playerEntity) -> new ChipWriterContainer(windowId, playerInventory, IWorldPosCallable.of(worldIn, pos)), CONTAINER_NAME);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
