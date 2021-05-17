package agency.highlysuspect.dazzle2.block;

import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

public class ColorHolderBlock extends Block {
	public ColorHolderBlock(DyeColor color, Settings settings) {
		super(settings);
		this.color = color;
	}
	
	public final DyeColor color;
}
