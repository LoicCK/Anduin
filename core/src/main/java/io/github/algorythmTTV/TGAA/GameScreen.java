package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.algorythmTTV.TGAA.engine.Room;
import io.github.algorythmTTV.TGAA.entities.IntelligentCharacter;

public class GameScreen implements Screen {
    final TGAA game;
    private AssetManager manager;
    private Room currentRoom;
    private IntelligentCharacter player;

    public GameScreen(TGAA game) {
        this.game = game;
        manager = new AssetManager();
        manager.load("characters/player.png", Texture.class);
        manager.finishLoading();
        currentRoom = new Room("test", manager);
        player = new IntelligentCharacter("player", "", manager, 2.5f, 5f, 20, 20);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        input(v);
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        currentRoom.render(game, worldWidth, worldHeight);
        player.render(game);

        game.batch.end();
    }

    private void input(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.getSprite().translateX(delta*15f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.getSprite().translateX(-delta*15f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.getSprite().translateY(-delta*15f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.getSprite().translateY(delta*15f);
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
