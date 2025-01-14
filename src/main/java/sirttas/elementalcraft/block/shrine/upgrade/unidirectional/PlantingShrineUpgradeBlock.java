package sirttas.elementalcraft.block.shrine.upgrade.unidirectional;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sirttas.elementalcraft.block.shrine.upgrade.AbstractShrineUpgradeBlock;
import sirttas.elementalcraft.block.shrine.upgrade.ShrineUpgrades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PlantingShrineUpgradeBlock extends AbstractShrineUpgradeBlock {

	public static final String NAME = "shrine_upgrade_planting";

	private static final VoxelShape BASE_1 = Block.box(6D, 14D, 6D, 10D, 16D, 10D);
	private static final VoxelShape BASE_2 = Block.box(2D, 12D, 2D, 14D, 14D, 14D);
	private static final VoxelShape PIPE_1 = Block.box(13D, 11D, 1D, 15D, 18D, 3D);
	private static final VoxelShape PIPE_2 = Block.box(13D, 11D, 13D, 15D, 18D, 15D);
	private static final VoxelShape PIPE_3 = Block.box(1D, 11D, 13D, 3D, 18D, 15D);
	private static final VoxelShape PIPE_4 = Block.box(1D, 11D, 1D, 3D, 18D, 3D);

	private static final VoxelShape SHAPE = Shapes.or(BASE_1, BASE_2, PIPE_1, PIPE_2, PIPE_3, PIPE_4);

	public PlantingShrineUpgradeBlock() {
		super(ShrineUpgrades.PLANTING);
	}
	
	@Override
	public Direction getFacing(BlockState state) {
		return Direction.UP;
	}

	@Nonnull
    @Override
	@Deprecated
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return SHAPE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.elementalcraft.planting").withStyle(ChatFormatting.BLUE));
		super.appendHoverText(stack, worldIn, tooltip, flag);
	}
}
