package dev.mcmoded.wizardrygolems.golem;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

/// Каст заклинаний големом-миньоном. В оригинале (WinDanesz) големы были `ISpellCaster` с `EntityAIAttackSpell`;
/// голем Extra Golems 20.1 — один класс без этого интерфейса, поэтому здесь своя упрощённая копия логики
/// `AttackSpellGoal` Redux: подойти на дистанцию, кастовать случайное заклинание из списка, держать
/// непрерывные (луч, дыхание) до `continuousDuration` тиков, затем перезарядка.
public class GolemSpellGoal extends Goal {
    private final Mob golem;
    private final Supplier<List<Spell>> spells;
    private final double speed;
    private final float maxDistSq;
    private final int baseCooldown;
    private final int continuousDuration;
    private LivingEntity target;
    private int cooldown;
    private int seeTime;
    private Spell continuous;
    private int continuousTimer;

    public GolemSpellGoal(Mob golem, Supplier<List<Spell>> spells, double speed, float maxDist, int baseCooldown, int continuousDuration) {
        this.golem = golem;
        this.spells = spells;
        this.speed = speed;
        this.maxDistSq = maxDist * maxDist;
        this.baseCooldown = baseCooldown;
        this.continuousDuration = continuousDuration;
        this.cooldown = baseCooldown;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = golem.getTarget();
        if (t == null || !t.isAlive() || spells.get().isEmpty()) return false;
        target = t;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() || (continuousTimer > 0 && target != null && target.isAlive());
    }

    @Override
    public void stop() {
        if (continuous != null) {
            continuous.endCast(new EntityCastContext(golem.level(), golem, InteractionHand.MAIN_HAND,
                    continuousDuration - continuousTimer, target, new SpellModifiers()));
        }
        target = null;
        seeTime = 0;
        continuous = null;
        continuousTimer = 0;
        cooldown = baseCooldown;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (target == null) return;
        double distSq = golem.distanceToSqr(target);
        boolean visible = golem.getSensing().hasLineOfSight(target);
        seeTime = visible ? seeTime + 1 : 0;
        if (distSq <= maxDistSq && seeTime >= 5) golem.getNavigation().stop();
        else golem.getNavigation().moveTo(target, speed);
        golem.getLookControl().setLookAt(target, 30, 30);

        if (continuousTimer > 0) {
            continuousTimer--;
            int tick = continuousDuration - continuousTimer;
            EntityCastContext ctx = new EntityCastContext(golem.level(), golem, InteractionHand.MAIN_HAND, tick, target, new SpellModifiers());
            if (distSq > maxDistSq || !visible || !continuous.cast(ctx) || continuousTimer == 0) {
                continuous.endCast(ctx);
                cooldown = continuous.getCooldown() + baseCooldown;
                continuous = null;
                continuousTimer = 0;
            }
            return;
        }
        if (--cooldown > 0) return;
        if (distSq > maxDistSq || !visible) {
            cooldown = 10;
            return;
        }
        List<Spell> pool = new ArrayList<>(spells.get());
        while (!pool.isEmpty()) {
            Spell spell = pool.remove(golem.getRandom().nextInt(pool.size()));
            EntityCastContext ctx = new EntityCastContext(golem.level(), golem, InteractionHand.MAIN_HAND, 0, target, new SpellModifiers());
            if (!spell.canCastByEntity() || !spell.cast(ctx)) continue;
            if (spell.isInstantCast()) {
                cooldown = baseCooldown + spell.getCooldown();
            } else {
                continuous = spell;
                continuousTimer = continuousDuration - 1;
            }
            return;
        }
        cooldown = baseCooldown;
    }
}
