package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.mixin.WallStandingBlockItemMixin;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Optional;

public class Junk {
	public static Optional<Direction> whatWouldTorchusDo(ItemPlacementContext ctx) {
		WallStandingBlockItem wsbi = (WallStandingBlockItem) Items.TORCH;
		BlockState torchState = ((WallStandingBlockItemMixin) wsbi).funkyGetPlacementState(ctx);
		
		if(torchState == null) return Optional.empty();
		
		if(torchState.isOf(Blocks.WALL_TORCH)) {
			return Optional.of(torchState.get(WallTorchBlock.FACING));
		} else if(torchState.isOf(Blocks.TORCH)) {
			return Optional.of(Direction.DOWN);
		} else return Optional.empty();
	}
	
	public static String prettyPrintDyeColor(DyeColor color, boolean murica) {
		String nameLowercase = color.getName().replace('_', ' ');
		if(!murica) nameLowercase = nameLowercase.replaceAll("gray", "grey");
		return WordUtils.capitalizeFully(nameLowercase);
	}
	
	public static float rangeRemap(float value, float low1, float high1, float low2, float high2) {
		float value2 = MathHelper.clamp(value, low1, high1);
		return low2 + (value2 - low1) * (high2 - low2) / (high1 - low1);
	}
}
