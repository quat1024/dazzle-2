package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.block.entity.DazzleBlockEntityTypes;
import agency.highlysuspect.dazzle2.block.entity.LightAirBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ProjectedLightPanelBlock extends Block {
	public ProjectedLightPanelBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(POWER, 0));
	}
	
	public static final DirectionProperty FACING = Properties.FACING;
	public static final IntProperty POWER = IntProperty.of("power", 0, 15);
	
	public static final int BEAM_SEGMENT_LENGTH = 3;
	public static final int MAX_BEAM_LENGTH = BEAM_SEGMENT_LENGTH * 15;
	private static final double THICKNESS = 2 / 16d;
	private static final VoxelShape[] SHAPES = new VoxelShape[]{
		VoxelShapes.cuboid(0, 1 - THICKNESS, 0, 1, 1, 1),
		VoxelShapes.cuboid(0, 0, 0, 1, THICKNESS, 1),
		VoxelShapes.cuboid(0, 0, 1 - THICKNESS, 1, 1, 1),
		VoxelShapes.cuboid(0, 0, 0, 1, 1, THICKNESS),
		VoxelShapes.cuboid(1 - THICKNESS, 0, 0, 1, 1, 1),
		VoxelShapes.cuboid(0, 0, 0, THICKNESS, 1, 1)
	};
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState()
			.with(FACING, ctx.getSide())
			.with(POWER, ctx.getWorld().getReceivedRedstonePower(ctx.getBlockPos()));
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		placeLightBlocks(world, pos, state, state.get(POWER));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING, POWER));
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		int worldPower = world.getReceivedRedstonePower(pos);
		int currentPower = state.get(POWER);
		if(worldPower != currentPower) {
			world.setBlockState(pos, state.with(POWER, worldPower), 2);
		}
		
		//always place the light blocks (e.g. what if a block is removed and replaced far ahead, so i didn't get an update)
		placeLightBlocks(world, pos, state, worldPower);
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		//If it's replaced with another panel state, that's ok
		if(newState.isOf(state.getBlock())) return;
		
		//Clean up any outstanding light blocks
		Direction facing = state.get(FACING);
		for(int i = 1; i < MAX_BEAM_LENGTH; i++) {
			BlockPos p = pos.offset(facing, i);
			LightAirBlockEntity be = DazzleBlockEntityTypes.LIGHT_AIR.get(world, p);
			if(be == null) continue;
			if(be.belongsTo(pos)) world.setBlockState(p, Blocks.AIR.getDefaultState());
		}
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPES[state.get(FACING).ordinal()];
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		placeLightBlocks(world, pos, state, state.get(POWER));
	}
	
	public void check(World world, BlockPos pos, BlockState state) {
		placeLightBlocks(world, pos, state, state.get(POWER));
	}

	public void placeLightBlocks(World world, BlockPos panelPos, BlockState state, int panelPower) {
		ItemPlacementContext automatically = new AutomaticItemPlacementContext(world, panelPos, Direction.UP, ItemStack.EMPTY, Direction.UP);
		
		Direction facing = state.get(FACING);
		boolean hitWall = false;
		for(int i = 1; i < MAX_BEAM_LENGTH; i++) {
			BlockPos offsetPos = panelPos.offset(facing, i);
			BlockState stateThere = world.getBlockState(offsetPos);
			if(stateThere.isAir() || stateThere.isOf(DazzleBlocks.LIGHT_AIR) || stateThere.canReplace(automatically)) {
				setOrBreakLight(world, panelPos, offsetPos, hitWall ? 0 : MathHelper.clamp(panelPower - ((i - 1) / BEAM_SEGMENT_LENGTH), 0, 15));
			} else {
				hitWall = true;
			}
		}
	}
	
	private void setOrBreakLight(World world, BlockPos panelPos, BlockPos pos, int level) {
		LightAirBlockEntity be = DazzleBlockEntityTypes.LIGHT_AIR.get(world, pos);
		if(be != null && !be.belongsTo(panelPos)) {
			int lightLevelThere = world.getBlockState(pos).get(LightAirBlock.LIGHT);
			if(lightLevelThere > level) return; //Don't bother
		}
		
		//this method handles the case of placing a light source emitting zero light = placing air
		DazzleBlocks.LIGHT_AIR.placeWithOwner(world, pos, level, panelPos);
	}
}
