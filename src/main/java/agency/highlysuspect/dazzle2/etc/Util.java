package agency.highlysuspect.dazzle2.etc;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class Util {
	//Copypaste of the thing in BlockWithEntity
	@Nullable
	public static <A extends BlockEntity, B extends BlockEntity> BlockEntityTicker<B> castTicker(BlockEntityType<B> givenType, BlockEntityType<A> expectedType, BlockEntityTicker<? super A> ticker) {
		//noinspection unchecked
		return expectedType == givenType ? (BlockEntityTicker<B>) ticker : null;
	}
}
