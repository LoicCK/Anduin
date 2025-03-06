package io.github.algorythmTTV.TGAA.entities;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;

public class AnimatedEntity {
    private Animation<TextureRegion> animation;
    private Texture spriteSheet;
    private float stateTime;
    private float frameDuration;
    private int frameWidth, frameHeight, rows, cols;

    public AnimatedEntity(String spriteSheetPath, int frameWidth, int frameHeight, int rows, int cols, float frameDuration, int targetWidth, int targetHeight) {
        Texture originalTexture = new Texture(spriteSheetPath);
        this.spriteSheet = resizeTexture(originalTexture, targetWidth, targetHeight);
        originalTexture.dispose(); // Libérer la texture originale

        this.frameWidth = targetWidth / cols;  // Adapter la taille des frames
        this.frameHeight = targetHeight / rows;
        this.rows = rows;
        this.cols = cols;
        this.frameDuration = frameDuration;
        this.stateTime = 0;

        createAnimation();
    }

    private void createAnimation() {
        TextureRegion[][] tmpFrames = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        TextureRegion[] animationFrames = new TextureRegion[rows * cols];

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                animationFrames[index++] = tmpFrames[i][j];
            }
        }

        animation = new Animation<>(frameDuration, animationFrames);
        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private Texture resizeTexture(Texture texture, int targetWidth, int targetHeight) {
        texture.getTextureData().prepare();
        Pixmap originalPixmap = texture.getTextureData().consumePixmap();
        Pixmap resizedPixmap = new Pixmap(targetWidth, targetHeight, originalPixmap.getFormat());

        resizedPixmap.setFilter(Pixmap.Filter.BiLinear);
        resizedPixmap.drawPixmap(originalPixmap,
            0, 0, originalPixmap.getWidth(), originalPixmap.getHeight(),
            0, 0, targetWidth, targetHeight);

        Texture resizedTexture = new Texture(resizedPixmap);
        originalPixmap.dispose();
        resizedPixmap.dispose();
        return resizedTexture;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void render(SpriteBatch batch, float x, float y) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.begin();
        batch.draw(currentFrame, x, y);
        batch.end();
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
