package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.block.entity.DazzleBlockEntityTypes;
import agency.highlysuspect.dazzle2.block.entity.LightSensorBlockEntity;
import agency.highlysuspect.dazzle2.etc.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LightSensorBlock extends Block implements BlockEntityProvider {
	public LightSensorBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}
	
	//The "face" side, redstone signal is emitted from the other side.
	public static final DirectionProperty FACING = Properties.FACING;
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return DazzleBlockEntityTypes.LIGHT_SENSOR.instantiate(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		if(!world.isClient) return Util.castTicker(type, DazzleBlockEntityTypes.LIGHT_SENSOR, LightSensorBlockEntity::tickServer);
		else return null;
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerLookDirection());
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING));
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return power(state, world, pos, direction);
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return power(state, world, pos, direction);
	}
	
	protected int power(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if(state.get(FACING) != direction) return 0;
		
		LightSensorBlockEntity be = DazzleBlockEntityTypes.LIGHT_SENSOR.get(world, pos);
		return be == null ? 0 : be.getPower();
	}
}
