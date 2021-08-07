package sirttas.elementalcraft.block.pureinfuser.pedestal;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.elementalcraft.api.ElementalCraftApi;
import sirttas.elementalcraft.api.element.ElementType;
import sirttas.elementalcraft.api.element.IElementTypeProvider;
import sirttas.elementalcraft.api.element.storage.single.ISingleElementStorage;
import sirttas.elementalcraft.api.element.storage.single.SingleElementStorage;
import sirttas.elementalcraft.api.rune.handler.RuneHandler;
import sirttas.elementalcraft.block.entity.AbstractIERBlockEntity;
import sirttas.elementalcraft.block.pureinfuser.PureInfuserBlockEntity;
import sirttas.elementalcraft.config.ECConfig;
import sirttas.elementalcraft.inventory.SingleItemInventory;

public class PedestalBlockEntity extends AbstractIERBlockEntity implements IElementTypeProvider {

	@ObjectHolder(ElementalCraftApi.MODID + ":" + PedestalBlock.NAME) public static final BlockEntityType<PedestalBlockEntity> TYPE = null;
	private final SingleItemInventory inventory;
	private final SingleElementStorage elementStorage;
	private final RuneHandler runeHandler;

	public PedestalBlockEntity(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		inventory = new SingleItemInventory(this::setChanged);
		elementStorage = new ElementStorageRenderer(ElementType.getElementType(state), this::setChanged);
		runeHandler = new RuneHandler(ECConfig.COMMON.pedestalMaxRunes.get());
	}

	public Direction getPureInfuserDirection() {
		return Stream.of(Direction.values()).filter(d -> d.getAxis().getPlane() == Direction.Plane.HORIZONTAL)
				.filter(d -> this.getLevel().getBlockEntity(worldPosition.relative(d, 3)) instanceof PureInfuserBlockEntity)
				.findAny().orElse(Direction.UP);
	}

	@Override
	public ElementType getElementType() {
		return elementStorage.getElementType();
	}

	@Override
	public Container getInventory() {
		return inventory;
	}

	public ItemStack getItem() {
		return inventory.getItem(0);
	}

	@Override
	public ISingleElementStorage getElementStorage() {
		return elementStorage;
	}

	@Override
	@Nonnull
	public RuneHandler getRuneHandler() {
		return runeHandler;
	}

}