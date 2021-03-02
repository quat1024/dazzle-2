package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.block.entity.DazzleBlockEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class FlareBlock extends Block implements BlockEntityProvider {
	public FlareBlock(DyeColor color, Settings settings) {
		super(settings);
		
		this.color = color;
	}
	
	public final DyeColor color;
	
	public static final VoxelShape OUTLINE = VoxelShapes.cuboid(3/16d, 3/16d, 3/16d, 13/16d, 15/16d, 13/16d);
	
	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return OUTLINE;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return VoxelShapes.empty();
	}
	
	@Override
	public @Nullable BlockEntity createBlockEntity(BlockView world) {
		return DazzleBlockEntityTypes.FLARE.instantiate();
	}
}
