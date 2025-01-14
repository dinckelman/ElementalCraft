package sirttas.elementalcraft.block.instrument.io.purifier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import sirttas.elementalcraft.ElementalCraft;
import sirttas.elementalcraft.block.entity.ECBlockEntityTypes;
import sirttas.elementalcraft.block.instrument.io.AbstractIOInstrumentBlockEntity;
import sirttas.elementalcraft.config.ECConfig;
import sirttas.elementalcraft.recipe.instrument.io.IPurifierRecipe;

import javax.annotation.Nonnull;

public class PurifierBlockEntity extends AbstractIOInstrumentBlockEntity<PurifierBlockEntity, IPurifierRecipe> {

	private final PurifierContainer inventory;

	public PurifierBlockEntity(BlockPos pos, BlockState state) {
		super(ECBlockEntityTypes.PURIFIER, pos, state, null, ECConfig.COMMON.purifierTransferSpeed.get(), ECConfig.COMMON.purifierMaxRunes.get());
		inventory = new PurifierContainer(this::setChanged);
	}

	@Nonnull
    @Override
	protected @NotNull IItemHandler createHandler() {
		return new SidedInvWrapper(inventory, null);
	}

	@Override
	protected IPurifierRecipe lookupRecipe() {
		ItemStack input = inventory.getItem(0);

		if (!input.isEmpty()) {
			IPurifierRecipe recipe = ElementalCraft.PURE_ORE_MANAGER.getRecipes(input);

			if (recipe != null && recipe.matches(this)) {
				return recipe;
			}
		}
		return null;
	}

	@Nonnull
    @Override
	public @NotNull Container getInventory() {
		return inventory;
	}
}
