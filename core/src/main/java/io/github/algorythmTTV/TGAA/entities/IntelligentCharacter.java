package io.github.algorythmTTV.TGAA.entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.algorythmTTV.TGAA.TGAA;
import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.engine.ItemList;

public class IntelligentCharacter {
    protected String name;
    protected String description;
    protected ItemList inventory;
    protected Texture texture;
    protected Sprite sprite;
    TextureAtlas atlas;

    public IntelligentCharacter(String name, String description, AssetManager manager, float width, float height, float x, float y, TextureAtlas atlas) {
        this.name = name;
        this.description = description;
        inventory = new ItemList();
        texture = manager.get("characters/" + name + ".png", Texture.class);
        sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setPosition(x, y);
        this.atlas = atlas;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void render(TGAA game) {
        sprite.draw(game.batch);
    }

    public boolean addItem (Item item) {
        inventory.addItem(item);
        return true;
    }

    public Item takeItem(String name) {
        return inventory.takeItem(name);
    }
}
