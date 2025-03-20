package io.github.algorythmTTV.TGAA.entities;

import com.badlogic.gdx.assets.AssetManager;
import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.engine.ItemList;

public class Player extends IntelligentCharacter {
    private int money;
    private int weight;
    private int maxWeight;
    private int health;
    private int maxHealth;

    public Player(AssetManager manager) {
        super("player", "The player", manager, 25f, 50f, 200, 200);
        money = 0;
        weight = 0;
        maxWeight = 300;
    }

    public ItemList getInventory() {
        return inventory;
    }

    public void addHealth(int health) {
        this.health += health;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    public void removeHealth(int health) {
        this.health -= health;
        if (this.health <= 0) {
            this.health = 0;
        }
    }

    @Override
    public boolean addItem(Item item) {
        if (item.getWeight() < maxWeight - weight) {
            inventory.addItem(item);
            weight += item.getWeight();
            return true;
        }
        return false;
    }

    @Override
    public Item takeItem(String name) {
        Item item = super.takeItem(name);
        if (item != null) {
            weight -= item.getWeight();
        }
        return item;
    }
}
