package io.github.algorythmTTV.TGAA.engine.items;

import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.entities.Player;

abstract public class Consumable extends Item {
    public Consumable(String name, int weight) {
        super(name, weight);
    }

    abstract public void use(Player player);
}
