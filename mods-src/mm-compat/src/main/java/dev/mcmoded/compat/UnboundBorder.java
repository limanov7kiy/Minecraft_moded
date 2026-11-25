package dev.mcmoded.compat;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

/// Unbound: граница мира не действует в измерениях мода (R-020).
///
/// В 1.20.1 граница мира одна на все измерения. Граница обычного мира 2000×2000 действовала и в `unbound:*`,
/// где палубы кораблей лежат с шагом 4096 блоков, а космос — на огромных координатах: в выживании там наносился
/// урон границы, растущий с расстоянием за ней, — мгновенная смерть при входе в звездолёт (в креативе граница
/// не бьёт, поэтому в нём всё работало). Здесь в `unbound:*` урон границы не наносится, а сама граница
/// возвращается к стандартной — у клиента не рисуется стена на прежнем месте. Обычный мир и остальные измерения не трогаем.
public final class UnboundBorder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String NAMESPACE = "unbound";
    /// Граница измерения — как у нового по умолчанию: центр (0; 0), стандартные урон и предупреждение, полный размер.
    private static void toDefault(WorldBorder border) {
        border.setCenter(0.0, 0.0);
        border.setDamagePerBlock(0.2);
        border.setDamageSafeZone(5.0);
        border.setWarningBlocks(5);
        border.setWarningTime(15);
        border.setSize(WorldBorder.MAX_SIZE);
    }

    private UnboundBorder() {
    }

    private static boolean inUnbound(Entity entity) {
        return entity.level() instanceof ServerLevel level && NAMESPACE.equals(level.dimension().location().getNamespace());
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        if (source.is(DamageTypes.OUTSIDE_BORDER) && inUnbound(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    /// После старта, когда все измерения созданы: `unbound:*` — стандартная граница.
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        int reset = 0;
        for (ServerLevel level : event.getServer().getAllLevels()) {
            if (NAMESPACE.equals(level.dimension().location().getNamespace())) {
                toDefault(level.getWorldBorder());
                reset++;
            }
        }

        LOGGER.info("mm_compat: граница мира в unbound:* не действует: {} измерений переведены на стандартную границу", reset);
    }
}
