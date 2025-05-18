package io.github.algorythmTTV.TGAA.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.algorythmTTV.TGAA.TGAA;
import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.engine.ItemList;
import io.github.algorythmTTV.TGAA.engine.Renderable;
import org.w3c.dom.Text;

public class Player extends IntelligentCharacter implements Renderable {
    private int money;
    private int weight;
    private int maxWeight;
    private int health;
    private int maxHealth;
    public Animation<TextureRegion> runningAnimation;
    public Animation<TextureRegion> idleAnimation;
    private float stateTime = 0f;
    private boolean isMoving = false;
    public boolean facingRight = true;

    public Player(AssetManager manager) {
        super("player", "The player", manager, 50f, 50f, 400, 50);
        money = 0;
        weight = 0;
        maxWeight = 300;
        TextureAtlas runningAtlas = manager.get("characters/animations/player/run/player_run.atlas");
        runningAnimation = new Animation<TextureRegion>(0.05f, runningAtlas.findRegions("frame"), Animation.PlayMode.LOOP);
        TextureAtlas idleAtlas = manager.get("characters/animations/player/idle/player_idle.atlas");
        idleAnimation = new Animation<TextureRegion>(0.1f, idleAtlas.findRegions("idle"), Animation.PlayMode.LOOP);
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    @Override
    public float getYSortValue() {
        return sprite.getY();
    }


    @Override
    public void render(Batch batch) { // Notez le paramètre Batch
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (isMoving && runningAnimation != null) {
            currentFrame = runningAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = idleAnimation.getKeyFrame(stateTime, true);
        }

        if (currentFrame == null) return;

        float drawX = sprite.getX();
        float drawWidth = sprite.getWidth();
        if (!facingRight) {
            drawX = sprite.getX() + sprite.getWidth();
            drawWidth = -sprite.getWidth();
        }
        batch.draw(currentFrame, drawX, sprite.getY(), drawWidth, sprite.getHeight());
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
