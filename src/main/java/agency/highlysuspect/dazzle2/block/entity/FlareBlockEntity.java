package agency.highlysuspect.dazzle2.block.entity;

import agency.highlysuspect.dazzle2.block.FlareBlock;
import agency.highlysuspect.dazzle2.etc.FlareParticleEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlareBlockEntity extends BlockEntity {
	public FlareBlockEntity(BlockPos pos, BlockState state) {
		super(DazzleBlockEntityTypes.FLARE, pos, state);
	}
	
	//TODO 1.17: client-side only ticking, this does not need to tick on the server
	
	public static void tickClient(World world, BlockPos pos, BlockState state, FlareBlockEntity me) {
		if(world == null || !world.isClient || world.getTime() % 5 != 0) return;
		
		Block block = state.getBlock();
		if(!(block instanceof FlareBlock)) return; //how
		int color = ((FlareBlock) block).getColor().getMapColor().color; //color
		
		world.addParticle(new FlareParticleEffect(color), true,pos.getX() + .5, pos.getY() + .2, pos.getZ() + .5, 0, 0, 0);
	}
}
