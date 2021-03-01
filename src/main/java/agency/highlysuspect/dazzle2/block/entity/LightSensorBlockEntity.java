package agency.highlysuspect.dazzle2.block.entity;

import agency.highlysuspect.dazzle2.Init;
import agency.highlysuspect.dazzle2.block.LightSensorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;

public class LightSensorBlockEntity extends BlockEntity implements Tickable {
	public LightSensorBlockEntity() {
		super(DazzleBlockEntityTypes.LIGHT_SENSOR);
	}
	
	private int light;
	private long lastCheck = -1; //not saved
	
	@Override
	public void tick() {
		if(world == null || world.isClient) return;
		
		//Prevent issues with like, hooking a light sensor up to an inverted lamp and causing a stack overflow lmao
		if(world.getTime() == lastCheck) return;
		lastCheck = world.getTime();
		
		BlockState state = getCachedState();
		Direction facing = state.get(LightSensorBlock.FACING);
		BlockPos posToCheck = pos.offset(facing);
		int lightNow = world.getLightLevel(LightType.BLOCK, posToCheck);
		
		if(light != lightNow) {
			light = lightNow;
			markDirty();
			
			BlockPos reversePos = pos.offset(facing.getOpposite());
			world.updateNeighbor(reversePos, state.getBlock(), pos);
			world.updateNeighborsExcept(reversePos, state.getBlock(), facing);
		}
	}
	
	public int getPower() {
		return light;
	}
	
	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		light = tag.getInt("light");
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("light", light);
		return super.toTag(tag);
	}
}
