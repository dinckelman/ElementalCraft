package sirttas.elementalcraft.spell.air;

import java.util.Comparator;

import com.google.common.collect.Multimap;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import sirttas.elementalcraft.ElementType;
import sirttas.elementalcraft.config.ECConfig;
import sirttas.elementalcraft.spell.IEntityCastedSpell;
import sirttas.elementalcraft.spell.ISelfCastedSpell;
import sirttas.elementalcraft.spell.Spell;

public class SpellEnderStrike extends Spell implements ISelfCastedSpell, IEntityCastedSpell {

	public static final String NAME = "ender_strike";


	public SpellEnderStrike() {
		super(Properties.create(Spell.Type.COMBAT).elementType(ElementType.AIR).cooldown(ECConfig.CONFIG.enderStrikeCooldown.get()).consumeAmount(ECConfig.CONFIG.enderStrikeConsumeAmount.get()));
	}

	@Override
	public ActionResultType castOnEntity(Entity sender, Entity target) {
		if (sender instanceof LivingEntity) {
			LivingEntity livingSender = (LivingEntity) sender;
			Vec3d newPos = target.getPositionVector().add(target.getLookVec().inverse().normalize());

			if (livingSender.attemptTeleport(newPos.x, newPos.y + 0.5F, newPos.z, true)) {
				livingSender.lookAt(EntityAnchorArgument.Type.EYES, target.getPositionVec());
				livingSender.getEntityWorld().playSound(null, livingSender.prevPosX, livingSender.prevPosY, livingSender.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
						livingSender.getSoundCategory(), 1.0F, 1.0F);
				livingSender.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
				if (livingSender instanceof PlayerEntity) {
					PlayerEntity playerSender = (PlayerEntity) livingSender;

					playerSender.attackTargetEntityWithCurrentItem(target);
					playerSender.resetCooldown();
					DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
						if (playerSender instanceof ClientPlayerEntity) {
							((ClientPlayerEntity) playerSender).swingArm(Hand.MAIN_HAND);
						}
					});
				} else {
					livingSender.attackEntityAsMob(target);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public ActionResultType castOnSelf(Entity sender) {
		Vec3d pos = sender.getPositionVector();

		return sender.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(ECConfig.CONFIG.enderStrikeRange.get())).stream()
				.filter(IMob.class::isInstance).sorted(Comparator.comparingDouble(e -> pos.distanceTo(e.getPositionVec()))).findFirst().map(e -> castOnEntity(sender, e)).orElse(ActionResultType.PASS);
	}

	@Override
	public Multimap<String, AttributeModifier> getOnUseAttributeModifiers() {
		Multimap<String, AttributeModifier> multimap = super.getOnUseAttributeModifiers();

		multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
		multimap.put(PlayerEntity.REACH_DISTANCE.getName(), new AttributeModifier(REACH_DISTANCE_MODIFIER, "Reach distance modifier", 5.0D, AttributeModifier.Operation.ADDITION));
		return multimap;
	}
}