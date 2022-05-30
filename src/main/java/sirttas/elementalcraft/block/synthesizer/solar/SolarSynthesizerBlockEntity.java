package sirttas.elementalcraft.block.synthesizer.solar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.elementalcraft.api.ElementalCraftApi;
import sirttas.elementalcraft.api.element.storage.CapabilityElementStorage;
import sirttas.elementalcraft.api.element.storage.single.ISingleElementStorage;
import sirttas.elementalcraft.api.name.ECNames;
import sirttas.elementalcraft.api.rune.handler.CapabilityRuneHandler;
import sirttas.elementalcraft.api.rune.handler.IRuneHandler;
import sirttas.elementalcraft.api.rune.handler.RuneHandler;
import sirttas.elementalcraft.block.container.IContainerTopBlockEntity;
import sirttas.elementalcraft.block.entity.AbstractECContainerBlockEntity;
import sirttas.elementalcraft.config.ECConfig;
import sirttas.elementalcraft.container.SingleItemContainer;
import sirttas.elementalcraft.item.elemental.LenseItem;
import sirttas.elementalcraft.particle.ParticleHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class SolarSynthesizerBlockEntity extends AbstractECContainerBlockEntity implements IContainerTopBlockEntity {

	@ObjectHolder(ElementalCraftApi.MODID + ":" + SolarSynthesizerBlock.NAME) public static final BlockEntityType<SolarSynthesizerBlockEntity> TYPE = null;

	private final SingleItemContainer inventory;
	private final RuneHandler runeHandler;
	private boolean working;

	public SolarSynthesizerBlockEntity(BlockPos pos, BlockState state) {
		this(TYPE, pos, state);
	}

	protected SolarSynthesizerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
		inventory = new SingleItemContainer(this::setChanged);
		runeHandler = new RuneHandler(ECConfig.COMMON.solarSythesizerMaxRunes.get());
		working = false;
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SolarSynthesizerBlockEntity solarSynthesizer) {
		if (level.dimensionType().hasSkyLight() && level.canSeeSky(pos) && level.isDay()) {
			var synthesized = solarSynthesizer.handleSynthesis(ECConfig.COMMON.solarSythesizerLenseElementMultiplier.get());

			if (synthesized > 0) {
				solarSynthesizer.breakLense(level, pos);
			}
		}
	}

	protected void breakLense(Level level, BlockPos pos) {
		ItemStack stack = inventory.getItem(0);

		if (!stack.isEmpty() && stack.getDamageValue() >= stack.getMaxDamage()) {
			Vec3 position = Vec3.atCenterOf(pos).add(0, 6.5 / 16, 0);

			inventory.setItem(0, ItemStack.EMPTY);
			level.playLocalSound(position.x(), position.y(), position.z(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.8F, 0.8F + level.random.nextFloat() * 0.4F, false);
			ParticleHelper.createItemBreakParticle(level, position, level.random, stack, 3);
			setChanged();
		}
	}

	protected int handleSynthesis(float amount) {
		ISingleElementStorage container = getContainer();

		if (container != null) {
			int synthesized = getElementStorage()
					.map(storage -> runeHandler.handleElementTransfer(storage, container, amount))
					.orElse(0);
			var hasSynthesized = synthesized > 0;
			
			if (hasSynthesized || working) {
				working = hasSynthesized;
				setChanged();
			}
			return synthesized;
		}
		return 0;
	}

	public boolean isWorking() {
		return working;
	}

	@Override
	public void load(@Nonnull CompoundTag compound) {
		super.load(compound);
		if (compound.contains(ECNames.RUNE_HANDLER)) {
			IRuneHandler.readNBT(runeHandler, compound.getList(ECNames.RUNE_HANDLER, 8));
		}
		working = compound.getBoolean(ECNames.WORKING);
	}

	@Override
	public void saveAdditional(@Nonnull CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put(ECNames.RUNE_HANDLER, IRuneHandler.writeNBT(runeHandler));
		compound.putBoolean(ECNames.WORKING, working);
	}

	@Override
	@Nonnull
	public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
		if (!this.remove) {
			if (cap == CapabilityElementStorage.ELEMENT_STORAGE_CAPABILITY) {
				return getElementStorage(ECConfig.COMMON.solarSythesizerLenseElementMultiplier.get());
			} else if (cap == CapabilityRuneHandler.RUNE_HANDLE_CAPABILITY) {
				return LazyOptional.of(runeHandler != null ? () -> runeHandler : null).cast();
			}
		}
		return super.getCapability(cap, side);
	}

	@Nonnull
	protected  <U> LazyOptional<U> getElementStorage(int multiplier) {
		var item = getInventory().getItem(0);

		if (item.getItem() instanceof LenseItem lenseItem) {
			return LazyOptional.of(() -> lenseItem.getStorage(item, multiplier)).cast();
		}
		return CapabilityElementStorage.get(item).cast();
	}

	@Nonnull
    @Override
	public Container getInventory() {
		return inventory;
	}

	public Optional<ISingleElementStorage> getElementStorage() {
		return CapabilityElementStorage.get(this)
				.filter(ISingleElementStorage.class::isInstance)
				.map(ISingleElementStorage.class::cast);
	}

	public RuneHandler getRuneHandler() {
		return runeHandler;
	}
}