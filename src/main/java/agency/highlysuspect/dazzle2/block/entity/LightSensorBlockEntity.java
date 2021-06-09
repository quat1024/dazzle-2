package agency.highlysuspect.dazzle2.block.entity;

import agency.highlysuspect.dazzle2.block.LightSensorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class LightSensorBlockEntity extends BlockEntity {
	public LightSensorBlockEntity(BlockPos pos, BlockState state) {
		super(DazzleBlockEntityTypes.LIGHT_SENSOR, pos, state);
	}
	
	private int light;
	private long lastCheck = -1; //not saved
	
	public static void tickServer(World world, BlockPos pos, BlockState state, LightSensorBlockEntity me) {
		//Prevent issues with like, hooking a light sensor up to an inverted lamp and causing a stack overflow lmao
		if(world.getTime() == me.lastCheck) return;
		me.lastCheck = world.getTime();
		
		Direction facing = state.get(LightSensorBlock.FACING);
		BlockPos posToCheck = pos.offset(facing);
		int lightNow = world.getLightLevel(LightType.BLOCK, posToCheck);
		
		if(me.light != lightNow) {
			me.light = lightNow;
			me.markDirty();
			
			BlockPos reversePos = pos.offset(facing.getOpposite());
			world.updateNeighbor(reversePos, state.getBlock(), pos);
			world.updateNeighborsExcept(reversePos, state.getBlock(), facing);
		}
	}
	
	public int getPower() {
		return light;
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		light = tag.getInt("light");
	}
	
	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		tag.putInt("light", light);
		return super.writeNbt(tag);
	}
}
