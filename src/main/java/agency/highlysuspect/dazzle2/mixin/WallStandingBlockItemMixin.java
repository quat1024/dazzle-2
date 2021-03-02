package agency.highlysuspect.dazzle2.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.WallStandingBlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WallStandingBlockItem.class)
public interface WallStandingBlockItemMixin {
	@Invoker("getPlacementState") BlockState funkyGetPlacementState(ItemPlacementContext context);
}
