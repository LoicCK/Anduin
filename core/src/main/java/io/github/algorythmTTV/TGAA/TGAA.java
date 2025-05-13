package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class TGAA extends Game {

    public SpriteBatch batch;
    public FitViewport viewport;
    public OrthographicCamera camera;
    public BitmapFont minecraftFontSmall;
    public BitmapFont minecraftFontMedium;
    public BitmapFont minecraftFontLarge;
    public static final String FONT_CHARACTERS = FreeTypeFontGenerator.DEFAULT_CHARS;


    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 450, camera); // Dimensions exemples
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Minecraft.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.characters = FONT_CHARACTERS;
        parameter.color = Color.WHITE;
        parameter.kerning = true;
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        parameter.hinting = FreeTypeFontGenerator.Hinting.None;
        parameter.size = 16;
        minecraftFontSmall = generator.generateFont(parameter);
        parameter.size = 24;
        minecraftFontMedium = generator.generateFont(parameter);
        parameter.size = 32;
        minecraftFontLarge = generator.generateFont(parameter);
        generator.dispose();
        this.setScreen(new TitleScreen(this)); // Ou l'écran de ton choix
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        if (minecraftFontSmall != null) minecraftFontSmall.dispose();
        if (minecraftFontMedium != null) minecraftFontMedium.dispose();
        if (minecraftFontLarge != null) minecraftFontLarge.dispose();
    }
}
