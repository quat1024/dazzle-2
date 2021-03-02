package agency.highlysuspect.dazzle2.mixin;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import agency.highlysuspect.dazzle2.block.LightSensorBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireBlockMixin {
	@Inject(
		method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
		at = @At("HEAD"),
		cancellable = true
	)
	private static void connectsTo(BlockState state, @Nullable Direction dir, CallbackInfoReturnable<Boolean> cir) {
		if(state.getBlock() == DazzleBlocks.LIGHT_SENSOR) {
			cir.setReturnValue(state.get(LightSensorBlock.FACING) == dir);
		}
	}
}
