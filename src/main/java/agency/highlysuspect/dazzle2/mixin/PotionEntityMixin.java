package agency.highlysuspect.dazzle2.mixin;

import agency.highlysuspect.dazzle2.block.DazzleBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public class PotionEntityMixin {
	@Inject(
		method = "applySplashPotion",
		at = @At("HEAD")
	)
	private void whenApplyingSplashPotion(List<StatusEffectInstance> effects, Entity what, CallbackInfo ci) {
		for(StatusEffectInstance inst : effects) {
			if(inst.getEffectType() == StatusEffects.INVISIBILITY) {
				World world = ((PotionEntity) (Object) this).world;
				Box box = ((PotionEntity) (Object) this).getBoundingBox().expand(4, 2, 4);
				BlockPos.stream(box).forEach(p -> {
					if(world.getBlockState(p).isOf(Blocks.TORCH) || world.getBlockState(p).isOf(Blocks.WALL_TORCH)) { //TODO: block tag
						world.setBlockState(p, DazzleBlocks.PLACEABLE_HIDDEN_LIGHT.withLightLevel(14));
					}
				});
			}
		}
	}
}
