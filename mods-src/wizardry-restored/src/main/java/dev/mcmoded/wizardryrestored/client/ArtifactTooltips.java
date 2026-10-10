package dev.mcmoded.wizardryrestored.client;

import com.binaris.wizardry.setup.registries.EBItems;
import dev.mcmoded.wizardryrestored.WizardryRestored;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Supplier;

/// Redux 0.8.9 дописывает к «нереализованным» артефактам красную строку «This item is not working…». Для тех,
/// что оживлены дополнением (`util/Artifacts` и встроенные в заклинания эффекты), эту строку убираем.
@Mod.EventBusSubscriber(modid = WizardryRestored.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ArtifactTooltips {
    private static final String PREFIX = "This item is not working";
    private static final List<Supplier<? extends Item>> IMPLEMENTED = List.of(
            EBItems.AMULET_AUTO_SHIELD, EBItems.CHARM_ABSEILING, EBItems.CHARM_BLACK_HOLE, EBItems.CHARM_LIGHT,
            EBItems.CHARM_SILK_TOUCH, EBItems.CHARM_STOP_TIME, EBItems.CHARM_TRANSPORTATION, EBItems.AMULET_ANCHORING,
            EBItems.AMULET_RESURRECTION, EBItems.RING_DEFENDER, EBItems.RING_DISINTEGRATION, EBItems.RING_HAMMER,
            EBItems.RING_INTERDICTION, EBItems.RING_MIND_CONTROL, EBItems.RING_STORMCLOUD, EBItems.LIGHTNING_HAMMER,
            EBItems.AMULET_FIRE_CLOAKING, EBItems.AMULET_FROST_WARDING, EBItems.AMULET_BANISHING);

    private ArtifactTooltips() {
    }

    @SubscribeEvent
    public static void onGather(RenderTooltipEvent.GatherComponents event) {
        if (IMPLEMENTED.stream().noneMatch(s -> event.getItemStack().is(s.get()))) return;
        event.getTooltipElements().removeIf(e -> e.left().map(t -> t.getString().startsWith(PREFIX)).orElse(false));
    }
}
