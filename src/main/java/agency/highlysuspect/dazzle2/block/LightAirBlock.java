package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.block.entity.DazzleBlockEntityTypes;
import agency.highlysuspect.dazzle2.block.entity.LightAirBlockEntity;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LightAirBlock extends Block implements BlockEntityProvider {
	public LightAirBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(LIGHT, 15));
	}
	
	public static final IntProperty LIGHT = IntProperty.of("light", 1, 15);
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(LIGHT));
	}
	
	public BlockState withLightLevel(int power) {
		if(power <= 0) {
			return Blocks.AIR.getDefaultState();
		} else return getDefaultState().with(LIGHT, power);
	}
	
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		check(world, pos);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		recursiveCheck(world, pos);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}
	
	private static boolean checking = false;
	private void recursiveCheck(World world, BlockPos pos) {
		if(checking) return;
		
		try {
			checking = true;
			
			Deque<BlockPos> positionsToScan = new ArrayDeque<>();
			positionsToScan.addLast(pos);
			Set<BlockPos> positionsToCheck = new HashSet<>();
			
			while(!positionsToScan.isEmpty()) {
				BlockPos scan = positionsToScan.removeFirst();
				positionsToScan.remove(scan);
				positionsToCheck.add(scan);
				
				for(Direction d : Direction.values()) {
					BlockPos hoo = scan.offset(d);
					BlockState state = world.getBlockState(hoo);
					if(state.isOf(DazzleBlocks.PROJECTED_LIGHT_PANEL) || state.isOf(DazzleBlocks.LIGHT_AIR)) {
						//                                     This sucks, it's a linear search
						if(!positionsToCheck.contains(hoo) && !positionsToScan.contains(hoo)) positionsToScan.add(hoo);
					}
				}
			}
			
			//and make them all check
			for(BlockPos hoo : positionsToCheck) {
				BlockState state = world.getBlockState(hoo);
				if(state.isOf(DazzleBlocks.PROJECTED_LIGHT_PANEL)) {
					DazzleBlocks.PROJECTED_LIGHT_PANEL.check(world, hoo, state);
				} else { //always a fellow projected light
					check(world, hoo);
				}
			}
			
		} finally {
			checking = false;
		}
	}
	
	private TriState check(World world, BlockPos pos) {
		LightAirBlockEntity be = DazzleBlockEntityTypes.LIGHT_AIR.get(world, pos);
		if(be != null) return be.check();
		else return TriState.DEFAULT;
	}
	
	@Override
	public @Nullable BlockEntity createBlockEntity(BlockView world) {
		return DazzleBlockEntityTypes.LIGHT_AIR.instantiate();
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return true;
	}
	
	public void placeWithOwner(World world, BlockPos pos, int level, BlockPos ownerPos) {
		world.setBlockState(pos, withLightLevel(level), 2 | 16); //don't emit block updates | update listeners
		LightAirBlockEntity be = DazzleBlockEntityTypes.LIGHT_AIR.get(world, pos);
		if(be != null) be.setOwner(ownerPos);
	}
}
