package agency.highlysuspect.dazzle2.block;

import net.minecraft.block.EndRodBlock;
import net.minecraft.util.DyeColor;

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
}
