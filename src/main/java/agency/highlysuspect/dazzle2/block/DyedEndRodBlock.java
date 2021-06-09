package agency.highlysuspect.dazzle2.block;

import agency.highlysuspect.dazzle2.etc.DyedEndRodParticleEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndRodBlock;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class DyedEndRodBlock extends EndRodBlock implements ColorHolderBlock {
	public DyedEndRodBlock(DyeColor color, Settings settings) {
		super(settings);
		this.color = color;
	}
	
	private final DyeColor color;
	
	@Override
	public DyeColor getColor() {
		return color;
	}
	
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		//Copypaste from super, of course
		
		Direction direction = state.get(FACING);
		double d = (double)pos.getX() + 0.55D - (double)(random.nextFloat() * 0.1F);
		double e = (double)pos.getY() + 0.55D - (double)(random.nextFloat() * 0.1F);
		double f = (double)pos.getZ() + 0.55D - (double)(random.nextFloat() * 0.1F);
		double g = (0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
		if (random.nextInt(5) == 0) {
			//Aaand change the particle effect
			world.addParticle(new DyedEndRodParticleEffect(color.getMapColor().color), d + (double)direction.getOffsetX() * g, e + (double)direction.getOffsetY() * g, f + (double)direction.getOffsetZ() * g, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D, random.nextGaussian() * 0.005D);
		}
	}
}
