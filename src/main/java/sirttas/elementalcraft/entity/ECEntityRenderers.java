package sirttas.elementalcraft.entity;


import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sirttas.elementalcraft.api.ElementalCraftApi;
import sirttas.elementalcraft.entity.projectile.FeatherSpikeRenderer;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ElementalCraftApi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ECEntityRenderers {

    private ECEntityRenderers() {}

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ECEntities.FEATHER_SPIKE.get(), FeatherSpikeRenderer::new);
        event.registerEntityRenderer(ECEntities.THROWN_ELEMENT_CRYSTAL.get(), ThrownItemRenderer::new);
    }
}
