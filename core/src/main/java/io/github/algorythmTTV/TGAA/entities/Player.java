package io.github.algorythmTTV.TGAA.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.algorythmTTV.TGAA.TGAA;
import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.engine.ItemList;

public class Player extends IntelligentCharacter {
    private int money;
    private int weight;
    private int maxWeight;
    private int health;
    private int maxHealth;
    public Animation<TextureRegion> runningAnimation;
    private float stateTime = 0f;
    private boolean isMoving = false;
    public boolean facingRight = true;

    public Player(AssetManager manager) {
        super("player", "The player", manager, 50f, 50f, 200, 400, manager.get("characters/animations/player/run/player_run.atlas"));
        money = 0;
        weight = 0;
        maxWeight = 300;
        runningAnimation = new Animation<TextureRegion>(0.05f, atlas.findRegions("frame"), Animation.PlayMode.LOOP);
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    @Override
    public void render(TGAA game) {
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion currentFrame;

        if (isMoving && runningAnimation != null) {
            currentFrame = runningAnimation.getKeyFrame(stateTime, true);
        } else {
            if (atlas != null && atlas.findRegion("frame", 1) != null) {
                currentFrame = atlas.findRegion("frame", 1);
            } else if (texture != null) {
                game.batch.draw(texture, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
                return;
            } else {
                return;
            }
        }

        if (currentFrame == null) return;

        float drawX = sprite.getX();
        float drawWidth = sprite.getWidth();

        if (!facingRight) {
            drawX = sprite.getX() + sprite.getWidth();
            drawWidth = -sprite.getWidth();
        }

        game.batch.draw(currentFrame, drawX, sprite.getY(), drawWidth, sprite.getHeight());
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
