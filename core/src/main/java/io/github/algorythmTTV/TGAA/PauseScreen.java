package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.algorythmTTV.TGAA.ui.Options;

public class PauseScreen implements Screen {
    final private TGAA game;
    final private GameScreen gameScreen;
    final private Stage stage;
    final private Skin skin;
    private Texture pausedGameTexture;
    private TextureRegion backgroundRegion;
    private ShapeRenderer shapeRenderer;
    private AssetManager assetManager;
    TextButton resumeButton;
    TextButton optionsButton;
    TextButton quitButton;
    private Table mainTable;
    private Table menuButtonsTable;
    private Table optionsUITable;
    private Cell<Table> dynamicContentCell;
    private Options optionsManager;

    public PauseScreen(TGAA game, GameScreen gameScreen, Texture capturedTexture, AssetManager assetManager) {
        this.game = game;

        this.gameScreen = gameScreen;

        this.stage = new Stage(game.viewport, game.batch);
        this.skin = new Skin();

        this.assetManager = assetManager;

        createUI();

        this.shapeRenderer = new ShapeRenderer();

        pausedGameTexture = capturedTexture;
        backgroundRegion = new TextureRegion(this.pausedGameTexture);
        backgroundRegion.flip(false, true);
    }

    private void createUI() {
        float buttonWidth = 144f;
        float buttonHeight = 48f;
        float buttonPadding = 5f;

        TextureAtlas uiAtlas = assetManager.get("ui/ui.atlas", TextureAtlas.class);

//        float frameDuration = 0.1f;
//
//        TextureRegionDrawable frame1 = new TextureRegionDrawable(uiAtlas.findRegion("button_anim", 1));
//        TextureRegionDrawable frame2 = new TextureRegionDrawable(uiAtlas.findRegion("button_anim", 2));
//        TextureRegionDrawable frame3 = new TextureRegionDrawable(uiAtlas.findRegion("button_anim", 3));
//        TextureRegionDrawable frame4 = new TextureRegionDrawable(uiAtlas.findRegion("button_anim", 4));
//
//        skin.add("monBoutonNormalDrawable", frame1, TextureRegionDrawable.class);
//
//        Array<TextureRegionDrawable> hoverFrames = new Array<>();
//        hoverFrames.add(frame1);
//        hoverFrames.add(frame2);
//        hoverFrames.add(frame3);
//        hoverFrames.add(frame4);
//        Animation<TextureRegionDrawable> hoverAnimation = new Animation<>(frameDuration, hoverFrames, Animation.PlayMode.NORMAL);
//        skin.add("monBoutonHoverAnim", hoverAnimation, Animation.class);
//
//        Array<TextureRegionDrawable> releaseFrames = new Array<>();
//        releaseFrames.add(frame4);
//        releaseFrames.add(frame3);
//        releaseFrames.add(frame2);
//        releaseFrames.add(frame1);
//        Animation<TextureRegionDrawable> releaseAnimation = new Animation<>(frameDuration, releaseFrames, Animation.PlayMode.NORMAL);
//        skin.add("monBoutonReleaseAnim", releaseAnimation, Animation.class);

        NinePatch boutonNinePatch = uiAtlas.createPatch("banner");
        NinePatchDrawable boutonDrawable = new NinePatchDrawable(boutonNinePatch);
        skin.add("fondBoutonParchemin", boutonDrawable, Drawable.class);

        NinePatch fondGroupeBoutonsNinePatch = uiAtlas.createPatch("menu_bg");
        NinePatchDrawable fondGroupeBoutonsDrawable = new NinePatchDrawable(fondGroupeBoutonsNinePatch);
        skin.add("fondMenuPrincipal", fondGroupeBoutonsDrawable, Drawable.class);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(Color.valueOf("fcb484")));
        pixmap.fill();
        Texture colorTexture = new Texture(pixmap);
        TextureRegionDrawable fondUniDrawable = new TextureRegionDrawable(new TextureRegion(colorTexture));
        skin.add("fondMenu", fondUniDrawable, Drawable.class);

        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fill();
        colorTexture = new Texture(pixmap);
        pixmap.dispose();
        fondUniDrawable = new TextureRegionDrawable(new TextureRegion(colorTexture));
        skin.add("fondGris", fondUniDrawable, Drawable.class);

        skin.add("minecraft-small", game.minecraftFontSmall, BitmapFont.class);
        skin.add("minecraft-medium", game.minecraftFontMedium, BitmapFont.class);
        skin.add("minecraft-large", game.minecraftFontLarge, BitmapFont.class);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("minecraft-small");
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = skin.getDrawable("fondBoutonParchemin");
        buttonStyle.over = skin.getDrawable("fondBoutonParchemin");
        buttonStyle.down = skin.getDrawable("fondBoutonParchemin");
        skin.add("default-button", buttonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("minecraft-small");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default-label", labelStyle);

        mainTable = new Table(skin);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        resumeButton = new TextButton("Reprendre", skin, "default-button");
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen);
            }
        });

        optionsButton = new TextButton("Options", skin, "default-button");
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (dynamicContentCell != null && optionsUITable != null) {
                    dynamicContentCell.setActor(optionsUITable);
                }
            }
        });

        quitButton = new TextButton("Quit", skin, "default-button");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        //mainTable.setDebug(true);

        menuButtonsTable = new Table(skin);
        menuButtonsTable.setBackground(skin.getDrawable("fondMenuPrincipal"));
        float paddingInterieurFond = 25f;
        menuButtonsTable.pad(paddingInterieurFond);

        menuButtonsTable.add(resumeButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        menuButtonsTable.row();
        menuButtonsTable.add(optionsButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        menuButtonsTable.row();
        menuButtonsTable.add(quitButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);


        this.optionsManager = new Options(assetManager.get("ui/ui.atlas", TextureAtlas.class), skin, gameScreen.getKeyManager(), stage);
        this.optionsUITable = this.optionsManager.getOptionsUITable();

        TextButton optionsReturnButton = this.optionsManager.getReturnButton();

        if (optionsReturnButton != null) {
            optionsReturnButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (dynamicContentCell != null && menuButtonsTable != null) {
                        dynamicContentCell.setActor(menuButtonsTable);
                    }
                }
            });
        }

        this.dynamicContentCell = this.mainTable.add(this.menuButtonsTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.stage);
        Gdx.input.setCursorCatched(false);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void render(float delta) {
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.camera.combined);

        game.batch.begin();
        game.batch.draw(backgroundRegion, 0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        game.batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(game.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0, 0, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
        shapeRenderer.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
