package sirttas.elementalcraft.block.shrine.enderlock;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sirttas.elementalcraft.api.element.ElementType;
import sirttas.elementalcraft.block.shape.ECShapes;
import sirttas.elementalcraft.block.shrine.AbstractPylonShrineBlock;
import sirttas.elementalcraft.block.shrine.AbstractShrineBlockEntity;
import sirttas.elementalcraft.particle.ParticleHelper;

public class EnderLockShrineBlock extends AbstractPylonShrineBlock {

	public static final String NAME = "enderlockshrine";

	private static final VoxelShape BASE = Block.box(6D, 12D, 6D, 10D, 16D, 10D);

	private static final VoxelShape IRON_NORTH = Block.box(7D, 12D, 5D, 9D, 14D, 6D);
	private static final VoxelShape IRON_SOUTH = Block.box(7D, 12D, 10D, 9D, 14D, 11D);
	private static final VoxelShape IRON_EAST = Block.box(10D, 12D, 7D, 11D, 14D, 9D);
	private static final VoxelShape IRON_WEST = Block.box(5D, 12D, 7D, 6D, 14D, 9D);

	private static final VoxelShape UPPER_BASE = Block.box(6D, 0D, 6D, 10D, 15D, 10D);
	private static final VoxelShape UPPER_RING_1 = Block.box(5D, 5D, 5D, 11D, 7D, 11D);
	private static final VoxelShape UPPER_RING_2 = Block.box(5D, 8D, 5D, 11D, 10D, 11D);
	private static final VoxelShape UPPER_RING_3 = Block.box(5D, 11D, 5D, 11D, 13D, 11D);

	private static final VoxelShape LOWER_SHAPE = Shapes.or(ECShapes.SHRINE_SHAPE, BASE, IRON_NORTH, IRON_SOUTH, IRON_EAST, IRON_WEST);
	private static final VoxelShape UPPER_SHAPE = Shapes.or(UPPER_BASE, UPPER_RING_1, UPPER_RING_2, UPPER_RING_3);

	public EnderLockShrineBlock() {
		super(ElementType.WATER);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new EnderLockShrineBlockEntity(pos, state) : null;
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createShrineTicker(level, type, EnderLockShrineBlockEntity.TYPE);
	}
	
	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void doAnimateTick(AbstractShrineBlockEntity shrine, BlockState state, Level world, BlockPos pos, Random rand) {
		ParticleHelper.createEnderParticle(world, Vec3.atLowerCornerOf(pos), 8 + rand.nextInt(5), rand);
	}
}