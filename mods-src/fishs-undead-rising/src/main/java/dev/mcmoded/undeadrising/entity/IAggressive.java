package dev.mcmoded.undeadrising.entity;

/// Таймер атаки для анимации удара в моделях.
/// Оригинал: Fish's Undead Rising (Fish), `entities/IAggressive`.
public interface IAggressive {
    int getAttackTimer();

    void setAttackTimer(int ticks);
}
