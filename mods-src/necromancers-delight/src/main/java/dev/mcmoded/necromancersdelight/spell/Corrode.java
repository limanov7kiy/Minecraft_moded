package dev.mcmoded.necromancersdelight.spell;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.RaySpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

/// Corrode (оригинал — WinDanesz): луч накладывает Corroded Fish's Undead Rising (+10% входящего урона за уровень).
/// В оригинале — бессрочно (описание: 20 с); здесь — `EFFECT_DURATION` 400 т. из json оригинала. Сила растёт с силой заклинания.
public class Corrode extends RaySpell {
    public Corrode() {
        this.particleSpacing(0.3);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult hit, Vec3 origin) {
        if (!(hit.getEntity() instanceof LivingEntity target)) return false;
        if (ctx.world().isClientSide) return true;
        MobEffect corroded = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("mod_lavacow", "corroded"));
        if (corroded == null) return false;
        int bonus = (int) ((ctx.modifiers().get(SpellModifiers.POTENCY) - 1) / 0.4f); // как getStandardBonusAmplifier
        int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        target.addEffect(new MobEffectInstance(corroded, duration, property(DefaultProperties.EFFECT_STRENGTH) + Math.max(0, bonus)));
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult hit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.SNEEZE, x, y, z, vx, vy, vz);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.EARTH, SpellTypes.ATTACK, SpellAction.POINT, 30, 10, 20)
                .add(DefaultProperties.RANGE, 6f)
                .add(DefaultProperties.EFFECT_DURATION, 400)
                .add(DefaultProperties.EFFECT_STRENGTH, 0)
                .build();
    }
}
