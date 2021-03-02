package agency.highlysuspect.dazzle2;

import agency.highlysuspect.dazzle2.mixin.WallStandingBlockItemMixin;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class Junk {
	public static Identifier mapPath(Identifier id, UnaryOperator<String> mapper) {
		return new Identifier(id.getNamespace(), mapper.apply(id.getPath()));
	}
	
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
}
