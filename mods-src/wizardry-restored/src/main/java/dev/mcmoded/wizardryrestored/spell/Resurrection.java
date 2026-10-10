package dev.mcmoded.wizardryrestored.spell;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;

/// Воскрешение: погибший игрок (любой — в совместной игре все свои; ещё не нажавший «Возродиться», чьё тело рядом) встаёт на ноги возле
/// заклинателя с половиной здоровья — штатным возрождением сервера, так что вещи остаются там, где выпали.
public class Resurrection extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();
        if (!(ctx.world() instanceof ServerLevel server)) return true;
        double r = property(DefaultProperties.RANGE);
        ServerPlayer dead = server.getServer().getPlayerList().getPlayers().stream()
                .filter(p -> p != caster && p.isDeadOrDying() && p.level() == server && p.distanceToSqr(caster) <= r * r)
                .min(Comparator.comparingDouble(p -> p.distanceToSqr(caster))).orElse(null);
        if (dead == null) {
            caster.displayClientMessage(Component.translatable("spell.wizardry_restored.resurrection.nobody"), true);
            return false;
        }
        ServerPlayer revived = server.getServer().getPlayerList().respawn(dead, false);
        revived.teleportTo(server, caster.getX(), caster.getY(), caster.getZ(), caster.getYRot(), 0);
        revived.setHealth(revived.getMaxHealth() / 2);
        server.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, revived.getX(), revived.getY() + 1, revived.getZ(), 60, 0.4, 0.8, 0.4, 0.3);
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.HEALING, SpellTypes.UTILITY, SpellAction.POINT_UP, 150, 40, 600)
                .add(DefaultProperties.RANGE, 32f)
                .build();
    }
}
