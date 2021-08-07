package sirttas.elementalcraft.block.shrine.ore;

import java.util.Optional;
import java.util.stream.IntStream;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.elementalcraft.api.ElementalCraftApi;
import sirttas.elementalcraft.api.element.ElementType;
import sirttas.elementalcraft.block.shrine.AbstractShrineBlockEntity;
import sirttas.elementalcraft.block.shrine.upgrade.ShrineUpgrades;
import sirttas.elementalcraft.config.ECConfig;
import sirttas.elementalcraft.loot.LootHelper;

public class OreShrineBlockEntity extends AbstractShrineBlockEntity {

	@ObjectHolder(ElementalCraftApi.MODID + ":" + OreShrineBlock.NAME) public static final BlockEntityType<OreShrineBlockEntity> TYPE = null;

	private static final Properties PROPERTIES = Properties.create(ElementType.EARTH).periode(ECConfig.COMMON.oreShrinePeriode.get()).consumeAmount(ECConfig.COMMON.oreShrineConsumeAmount.get())
			.range(ECConfig.COMMON.oreShrineRange.get()).capacity(ECConfig.COMMON.shrinesCapacity.get() * 10);

	public OreShrineBlockEntity(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, PROPERTIES);
	}

	private Optional<BlockPos> findOre() {
		int range = getIntegerRange();

		return IntStream.range(-range, range + 1)
				.mapToObj(x -> IntStream.range(-range, range + 1).mapToObj(z -> IntStream.range(0, worldPosition.getY() + 1).mapToObj(y -> new BlockPos(worldPosition.getX() + x, y, worldPosition.getZ() + z))))
				.flatMap(s -> s.flatMap(s2 -> s2)).filter(p -> Tags.Blocks.ORES.contains(level.getBlockState(p).getBlock())).findAny();

	}

	private boolean isSilkTouch() {
		return this.hasUpgrade(ShrineUpgrades.SILK_TOUCH.get());
	}

	private int getFortuneLevel() {
		return this.getUpgradeCount(ShrineUpgrades.FORTUNE.get());
	}

	@Override
	public AABB getRangeBoundingBox() {
		int range = getIntegerRange();

		return new AABB(this.getBlockPos()).inflate(range, 0, range).move(0, -1, 0).expandTowards(0, 1D - worldPosition.getY(), 0);
	}


	@Override
	protected boolean doPeriode() {
		if (level instanceof ServerLevel && !level.isClientSide) {
			return findOre().map(p -> {
				int fortune = getFortuneLevel();

				if (fortune > 0) {
					ItemStack pickaxe = new ItemStack(Items.NETHERITE_PICKAXE);

					pickaxe.enchant(Enchantments.BLOCK_FORTUNE, fortune);
					LootHelper.getDrops((ServerLevel) this.level, p, pickaxe).forEach(s -> Block.popResource(this.level, this.worldPosition.above(), s));
				} else {
					LootHelper.getDrops((ServerLevel) this.level, p, isSilkTouch()).forEach(s -> Block.popResource(this.level, this.worldPosition.above(), s));
				}
				this.level.setBlockAndUpdate(p, Blocks.STONE.defaultBlockState());
				return true;
			}).orElse(false);
		}
		return false;
	}
}