package io.github.algorythmTTV.TGAA.engine.items;

import io.github.algorythmTTV.TGAA.entities.Player;

public class HealthPotion extends Consumable{
    public HealthPotion() {
        super("Health Potion", 5);
    }

    @Override
    public void use(Player player) {
        player.addHealth(15);
    }
}
