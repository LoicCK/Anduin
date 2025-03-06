package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.algorythmTTV.TGAA.engine.Room;
import io.github.algorythmTTV.TGAA.entities.AnimatedEntity;
import io.github.algorythmTTV.TGAA.entities.IntelligentCharacter;

public class GameScreen implements Screen {
    final TGAA game;
    private AssetManager manager;
    private Room currentRoom;
    private IntelligentCharacter player;
    private AnimatedEntity playerIdleAnimation;

    public GameScreen(TGAA game) {
        this.game = game;
        manager = new AssetManager();
        manager.load("characters/player.png", Texture.class);
        manager.finishLoading();
        currentRoom = new Room("test", manager);
        player = new IntelligentCharacter("player", "", manager, 2.5f, 5f, 20, 20);
        playerIdleAnimation = new AnimatedEntity("characters/animations/player/idle.png", 124, 124, 1, 12, 0.1f, 10, 10);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        input(v);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        currentRoom.render(game, new int[]{0, 1, 2 ,3 ,4});
        player.render(game);

        game.batch.end();

        game.batch.begin();

        currentRoom.render(game, new int[]{5, 6});

        game.batch.end();

        playerIdleAnimation.update(v);
        playerIdleAnimation.render(game.batch, 10, 10);
    }

    private void input(float delta) {
        float moveSpeed = delta * 15f;
        float newX, newY;
        Sprite playerSprite = player.getSprite();

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX = playerSprite.getX() + moveSpeed;
            if (!currentRoom.collidesWith(newX, playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight())) {
                playerSprite.translateX(moveSpeed);
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX = playerSprite.getX() - moveSpeed;
            if (!currentRoom.collidesWith(newX, playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight())) {
                playerSprite.translateX(-moveSpeed);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY = playerSprite.getY() - moveSpeed;
            if (!currentRoom.collidesWith(playerSprite.getX(), newY, playerSprite.getWidth(), playerSprite.getHeight())) {
                playerSprite.translateY(-moveSpeed);
            }
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY = playerSprite.getY() + moveSpeed;
            if (!currentRoom.collidesWith(playerSprite.getX(), newY, playerSprite.getWidth(), playerSprite.getHeight())) {
                playerSprite.translateY(moveSpeed);
            }
        }
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
