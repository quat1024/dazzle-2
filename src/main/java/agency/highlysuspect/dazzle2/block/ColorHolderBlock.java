package agency.highlysuspect.dazzle2.block;

import net.minecraft.block.Block;
import net.minecraft.util.DyeColor;

// Some block that has 16 colors.
public interface ColorHolderBlock {
	DyeColor getColor();
	
	// Can be implemented if your block doesn't extend anything else.
	class Simple extends Block implements ColorHolderBlock {
		public Simple(DyeColor color, Settings settings) {
			super(settings);
			this.color = color;
		}
		
		private final DyeColor color;
		
		@Override
		public DyeColor getColor() {
			return color;
		}
	}
}
