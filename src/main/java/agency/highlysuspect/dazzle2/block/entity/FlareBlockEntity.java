package agency.highlysuspect.dazzle2.block.entity;

import agency.highlysuspect.dazzle2.block.FlareBlock;
import agency.highlysuspect.dazzle2.etc.FlareParticleEffect;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class FlareBlockEntity extends BlockEntity implements Tickable {
	public FlareBlockEntity() {
		super(DazzleBlockEntityTypes.FLARE);
	}
	
	//TODO 1.17: client-side only ticking, this does not need to tick on the server
	
	@Override
	public void tick() {
		if(world == null || !world.isClient || world.getTime() % 5 != 0) return;
		
		Block block = getCachedState().getBlock();
		if(!(block instanceof FlareBlock)) return; //how
		int color = ((FlareBlock) block).getColor().getMaterialColor().color; //color
		
		world.addParticle(new FlareParticleEffect(color), true,pos.getX() + .5, pos.getY() + .2, pos.getZ() + .5, 0, 0, 0);
	}
}
