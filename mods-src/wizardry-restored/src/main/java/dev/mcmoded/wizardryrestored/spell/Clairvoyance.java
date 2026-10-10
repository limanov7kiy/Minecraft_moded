package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import dev.mcmoded.wizardryrestored.util.SpellStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/// Ясновидение: с Shift — запомнить блок под взглядом; обычное сотворение — проложить к нему путь (штатный поиск
/// пути существ, до 64 блоков; дальше — к ближайшей достижимой точке) и 90 секунд показывать его искрами
/// только заклинателю. Если путь не найден — прямая линия в сторону метки.
public class Clairvoyance extends Spell {
    public static final String MARK = "clairvoyance_mark";
    public static final String MARK_DIM = "clairvoyance_dim";
    private record Trail(UUID player, ResourceKey<Level> dim, List<Vec3> points, long expiresAt) {
    }

    private static final List<Trail> TRAILS = new ArrayList<>();

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        CompoundTag data = SpellStates.data(caster);
        if (caster.isShiftKeyDown()) {
            HitResult hit = RayTracer.standardBlockRayTrace(server, caster, property(DefaultProperties.RANGE), false, true, false);
            if (!(hit instanceof BlockHitResult b) || hit.getType() != HitResult.Type.BLOCK) return false;
            data.putLong(MARK, b.getBlockPos().relative(b.getDirection()).asLong());
            data.putString(MARK_DIM, server.dimension().location().toString());
            caster.displayClientMessage(Component.translatable("spell.wizardry_restored.clairvoyance.marked"), true);
            this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }
        if (!data.contains(MARK) || !server.dimension().location().toString().equals(data.getString(MARK_DIM))) {
            caster.displayClientMessage(Component.translatable("spell.wizardry_restored.clairvoyance.no_mark"), true);
            return false;
        }
        BlockPos target = BlockPos.of(data.getLong(MARK));
        List<Vec3> points = findPath(server, caster, target);
        int duration = (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        TRAILS.removeIf(t -> t.player().equals(caster.getUUID()));
        TRAILS.add(new Trail(caster.getUUID(), server.dimension(), points, server.getGameTime() + duration));
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    private static List<Vec3> findPath(ServerLevel server, Player caster, BlockPos target) {
        List<Vec3> points = new ArrayList<>();
        Zombie probe = EntityType.ZOMBIE.create(server);
        if (probe != null) {
            probe.moveTo(caster.getX(), caster.getY(), caster.getZ());
            probe.setOnGround(true);
            var range = probe.getAttribute(Attributes.FOLLOW_RANGE);
            if (range != null) range.setBaseValue(64);
            GroundPathNavigation nav = new GroundPathNavigation(probe, server);
            nav.setCanOpenDoors(true);
            Path path = nav.createPath(target, 1);
            if (path != null) for (int i = 0; i < path.getNodeCount(); i++) points.add(Vec3.atBottomCenterOf(path.getNodePos(i)));
            probe.discard();
        }
        if (points.size() < 2) {
            points.clear();
            Vec3 from = caster.position(), to = Vec3.atBottomCenterOf(target);
            double len = Math.min(64, from.distanceTo(to));
            Vec3 dir = to.subtract(from).normalize();
            for (double d = 0; d <= len; d += 1.5) points.add(from.add(dir.scale(d)));
        }
        return points;
    }

    /// Длина показываемого пути для игрока (для серверных тестов), 0 — пути нет.
    public static int trailLength(Player player) {
        return TRAILS.stream().filter(t -> t.player().equals(player.getUUID())).mapToInt(t -> t.points().size()).findFirst().orElse(0);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || TRAILS.isEmpty() || event.getServer().getTickCount() % 10 != 0) return;
        for (Iterator<Trail> it = TRAILS.iterator(); it.hasNext(); ) {
            Trail t = it.next();
            ServerLevel level = event.getServer().getLevel(t.dim());
            ServerPlayer player = event.getServer().getPlayerList().getPlayer(t.player());
            if (level == null || level.getGameTime() >= t.expiresAt()) {
                it.remove();
                continue;
            }
            if (player == null || player.level() != level) continue;
            for (Vec3 p : t.points())
                if (p.distanceToSqr(player.position()) < 48 * 48)
                    level.sendParticles(player, ParticleTypes.END_ROD, true, p.x, p.y + 0.3, p.z, 1, 0, 0.1, 0, 0);
        }
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellTypes.UTILITY, SpellAction.POINT, 20, 0, 60)
                .add(DefaultProperties.RANGE, 10f)
                .add(DefaultProperties.DURATION, 1800)
                .build();
    }
}
