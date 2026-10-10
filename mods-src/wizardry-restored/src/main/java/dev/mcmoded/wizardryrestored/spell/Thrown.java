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
import dev.mcmoded.wizardryrestored.entity.LightningDiscEntity;
import dev.mcmoded.wizardryrestored.entity.LightningHammerEntity;
import dev.mcmoded.wizardryrestored.entity.ModEntities;
import dev.mcmoded.wizardryrestored.entity.ThornsEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Заклинания этапа 3, создающие сущности не на месте заклинателя: молниевый диск, молот молний, лес шипов.
public final class Thrown {
    private Thrown() {
    }

    /// Молниевый диск: летит туда, куда смотрит заклинатель, и сам доворачивает к врагам.
    public static class LightningDisc extends Spell {
        @Override
        public boolean cast(PlayerCastContext ctx) {
            Player caster = ctx.caster();
            if (ctx.world() instanceof ServerLevel server) {
                LightningDiscEntity disc = new LightningDiscEntity(ModEntities.LIGHTNING_DISC.get(), server);
                Vec3 look = caster.getLookAngle();
                disc.setPos(caster.getEyePosition(1).add(look).subtract(0, 0.3, 0));
                disc.setVelocity(look.scale(property(DefaultProperties.SPEED)));
                Constructs.configure(disc, ctx, property(DefaultProperties.DURATION));
                server.addFreshEntity(disc);
            }
            this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }

        @Override
        protected SpellProperties properties() {
            return SpellProperties.builder()
                    .assignBaseProperties(SpellTiers.ADVANCED, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT, 25, 0, 60)
                    .add(DefaultProperties.DAMAGE, 8f)
                    .add(DefaultProperties.SPEED, 0.9f)
                    .add(DefaultProperties.DURATION, 60)
                    .build();
        }
    }

    /// Молот молний: падает с неба в точку взгляда (нужно открытое место над ней).
    public static class LightningHammer extends Spell {
        @Override
        public boolean cast(PlayerCastContext ctx) {
            Player caster = ctx.caster();
            float range = ctx.modifiers().get(SpellModifiers.RANGE, property(DefaultProperties.RANGE));
            HitResult hit = RayTracer.standardBlockRayTrace(ctx.world(), caster, range, false, true, false);
            if (!(hit instanceof BlockHitResult b) || hit.getType() != HitResult.Type.BLOCK) return false;
            BlockPos ground = b.getBlockPos();
            int top = 0;
            while (top < 15 && ctx.world().isEmptyBlock(ground.above(top + 1))) top++;
            if (top < 3) return false;
            if (ctx.world() instanceof ServerLevel server) {
                LightningHammerEntity hammer = new LightningHammerEntity(ModEntities.LIGHTNING_HAMMER.get(), server);
                hammer.setPos(ground.getX() + 0.5, ground.getY() + 1 + top - 1.7, ground.getZ() + 0.5);
                hammer.setDeltaMovement(0, -1, 0);
                Constructs.configure(hammer, ctx, (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION)));
                server.addFreshEntity(hammer);
            }
            this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }

        @Override
        protected SpellProperties properties() {
            return SpellProperties.builder()
                    .assignBaseProperties(SpellTiers.MASTER, Elements.LIGHTNING, SpellTypes.ATTACK, SpellAction.POINT_UP, 100, 20, 300)
                    .add(DefaultProperties.RANGE, 20f)
                    .add(DefaultProperties.DAMAGE, 10f)
                    .add(DefaultProperties.EFFECT_RADIUS, 5)
                    .add(DefaultProperties.DURATION, 600)
                    .add(LightningHammerEntity.SECONDARY_DAMAGE)
                    .build();
        }
    }

    /// Лес шипов: два кольца колючих кустов вокруг заклинателя.
    public static class ForestOfThorns extends Spell {
        @Override
        public boolean cast(PlayerCastContext ctx) {
            Player caster = ctx.caster();
            if (ctx.world() instanceof ServerLevel server) {
                int lifetime = (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
                float radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
                int placed = 0;
                for (double r : new double[]{radius * 0.6, radius}) {
                    int n = (int) Math.round(r * 2.2);
                    for (int i = 0; i < n; i++) {
                        double a = 2 * Math.PI * i / n + r;
                        Double y = floor(server, caster.getX() + Math.cos(a) * r, caster.getY(), caster.getZ() + Math.sin(a) * r);
                        if (y == null) continue;
                        ThornsEntity thorns = new ThornsEntity(ModEntities.THORNS.get(), server);
                        thorns.setPos(caster.getX() + Math.cos(a) * r, y, caster.getZ() + Math.sin(a) * r);
                        Constructs.configure(thorns, ctx, lifetime);
                        server.addFreshEntity(thorns);
                        server.sendParticles(ParticleTypes.COMPOSTER, thorns.getX(), y + 0.5, thorns.getZ(), 5, 0.3, 0.3, 0.3, 0);
                        placed++;
                    }
                }
                if (placed == 0) return false;
            }
            this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
            return true;
        }

        /// Верх твёрдого блока под точкой (в пределах ±3 по высоте от заклинателя) с воздухом над ним.
        private static Double floor(ServerLevel level, double x, double y, double z) {
            BlockPos base = BlockPos.containing(x, y, z);
            for (int dy = 2; dy >= -3; dy--) {
                BlockPos p = base.offset(0, dy, 0);
                if (level.getBlockState(p.below()).isFaceSturdy(level, p.below(), net.minecraft.core.Direction.UP)
                        && level.getBlockState(p).getCollisionShape(level, p).isEmpty()) return (double) p.getY();
            }
            return null;
        }

        @Override
        protected SpellProperties properties() {
            return SpellProperties.builder()
                    .assignBaseProperties(SpellTiers.MASTER, Elements.EARTH, SpellTypes.ATTACK, SpellAction.POINT_DOWN, 75, 20, 300)
                    .add(DefaultProperties.EFFECT_RADIUS, 4)
                    .add(DefaultProperties.DURATION, 400)
                    .add(DefaultProperties.DAMAGE, 3f)
                    .build();
        }
    }
}
