package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


/** First screen of the application. Displayed after the application is created. */
public class TitleScreen implements Screen {
    final TGAA game;
    Texture bgTexture;
    private AssetManager manager;
    private Stage stage;
    private Skin skin;
    private Table mainTable;
    private Cell<Label> dynamicContentCell;
    private Label progressLabel;
    private boolean charged = false;
    private GameScreen gameScreen;


    public TitleScreen(TGAA game) {
        this.game = game;
        bgTexture = new Texture("TitleScreenBg.png");
        loadAssets();
        createUi();
        gameScreen = new GameScreen(game, manager);
    }

    public void loadAssets() {
        manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        manager.setLoader(TextureAtlas.class, new TextureAtlasLoader(new InternalFileHandleResolver()));

        manager.load("characters/player.png", Texture.class);
        manager.load("items/HealthPotion.png", Texture.class);

        manager.load("ui/ui.atlas", TextureAtlas.class);
        manager.load("rooms/props/atlas/props.atlas", TextureAtlas.class);
        manager.load("characters/animations/player/run/player_run.atlas", TextureAtlas.class);
        manager.load("characters/animations/player/idle/player_idle.atlas", TextureAtlas.class);

        String[] mapFiles = {
            "rooms/bigHouse.tmx"
        };

        for (String mapFile : mapFiles) {
            manager.load(mapFile, TiledMap.class);
        }
    }

    public void createUi() {
        this.skin = new Skin();
        skin.add("minecraft-small", game.minecraftFontSmall, BitmapFont.class);
        skin.add("minecraft-medium", game.minecraftFontMedium, BitmapFont.class);
        skin.add("minecraft-large", game.minecraftFontLarge, BitmapFont.class);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("minecraft-small");
        labelStyle.fontColor = Color.WHITE;
        skin.add("small-label", labelStyle);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("minecraft-medium");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default-label", labelStyle);

        labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("minecraft-large");
        labelStyle.fontColor = Color.WHITE;
        skin.add("large-label", labelStyle);

        this.stage = new Stage(game.viewport, game.batch);

        mainTable = new Table(skin);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        mainTable.add(new Label("Anduin", skin, "large-label"));
        mainTable.row();
        mainTable.add(new Label("Chargement des ressources...", skin, "default-label"));
        mainTable.row();
        progressLabel = new Label((int) (manager.getProgress()*100) + "%", skin, "small-label");
        mainTable.add(progressLabel);
    }

    @Override
    public void show() {
        // Prepare your screen here.
    }

    @Override
    public void render(float delta) {
        manager.update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.camera.update();
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.begin();

        game.batch.draw(bgTexture, 0, 0, worldWidth, worldHeight);

        game.batch.end();

        if (!charged) {
            progressLabel.setText((int) (manager.getProgress() * 100) + "%");
            if (1f-manager.getProgress() < 0.001f) {
                charged = true;
                gameScreen.prepare();
                progressLabel.setText("Appuyez sur ESPACE pour lancer le jeu");
            }
        }

        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (manager.update()) {
                game.setScreen(gameScreen);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        bgTexture.dispose();
    }
}
