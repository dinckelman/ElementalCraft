package sirttas.elementalcraft.jewel;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import sirttas.elementalcraft.api.element.ElementType;

import java.util.List;

public class HawkJewel extends Jewel {

    public static final String NAME = "hawk";

    public HawkJewel() {
        super(ElementType.AIR, 200);
        this.ticking = false;
    }

    @Override
    public void appendHoverText(List<Component> tooltip) {
        tooltip.add(new TranslatableComponent("tooltip.elementalcraft.hawk").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(tooltip);
    }
}
