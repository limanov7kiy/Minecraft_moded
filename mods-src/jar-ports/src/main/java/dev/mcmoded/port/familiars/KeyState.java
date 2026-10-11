package dev.mcmoded.port.familiars;

import net.minecraft.client.KeyMapping;

/** Переходник (только клиент): io.redspace.ironsspellbooks.player.KeyState удалён из Iron's Spells 3.16. */
public class KeyState {
    private final KeyMapping key;
    private boolean held, wasHeld;

    public KeyState(KeyMapping key) { this.key = key; }

    public void update() { wasHeld = held; held = key.isDown(); }

    public boolean wasPressed() { return held && !wasHeld; }

    public boolean wasReleased() { return !held && wasHeld; }

    public boolean isHeld() { return held; }
}
