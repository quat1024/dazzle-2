package agency.highlysuspect.dazzle2.mixin;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AreaEffectCloudEntity.class)
public class AreaEffectCloudEntityMixin {
	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;tick()V",
			shift = At.Shift.AFTER
		)
	)
	private void whatTicking(CallbackInfo ci) {
		World world = ((AreaEffectCloudEntity) (Object) this).world;
		if(world == null) return; //lol
		
		Box box = ((AreaEffectCloudEntity) (Object) this).getBoundingBox();
		
		BlockPos.stream(box).forEach(p -> {
			if(world.getBlockState(p).isOf(Blocks.TORCH) || world.getBlockState(p).isOf(Blocks.WALL_TORCH)) { //TODO: block tag
				world.setBlockState(p, DazzleBlocks.PLACEABLE_HIDDEN_LIGHT.withLightLevel(14));
			}
		});
	}
}
