package io.github.algorythmTTV.TGAA.engine;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedButton extends Button {
    private Animation<TextureRegionDrawable> hoverAnimation;
    private Animation<TextureRegionDrawable> releaseAnimation;
    private TextureRegionDrawable normalFrame;
    private float stateTime;
    private enum ButtonState {
        NORMAL,
        HOVER_START,
        HOVER_ANIMATING,
        HOVER_STEADY,
        RELEASE_START,
        RELEASE_ANIMATING
    }
    private ButtonState currentState = ButtonState.NORMAL;
    private TextureRegionDrawable currentFrame;

    public AnimatedButton(Skin skin, String name) {
        super();
        hoverAnimation = skin.get("monBoutonHoverAnim", Animation.class);
        releaseAnimation = skin.get("monBoutonReleaseAnim", Animation.class);
        normalFrame = skin.get("monBoutonNormalDrawable", TextureRegionDrawable.class);
    }

    public void startHoverAnimation() {
        if (currentState != ButtonState.HOVER_STEADY && currentState != ButtonState.HOVER_ANIMATING) {
            currentState = ButtonState.HOVER_START;
            stateTime = 0f;
        }
    }

    public void startReleaseAnimation() {
        if (currentState == ButtonState.HOVER_STEADY || currentState == ButtonState.HOVER_ANIMATING || currentState == ButtonState.HOVER_START) {
            currentState = ButtonState.RELEASE_START;
            stateTime = 0f;
        } else if (currentState == ButtonState.NORMAL) {
            currentFrame = normalFrame;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (currentState == ButtonState.HOVER_ANIMATING ||
            currentState == ButtonState.RELEASE_ANIMATING ||
            currentState == ButtonState.HOVER_START ||
            currentState == ButtonState.RELEASE_START) {
            stateTime += delta;
        }

        switch (currentState) {
            case NORMAL:
                currentFrame = normalFrame;
                break;

            case HOVER_START:
                currentFrame = hoverAnimation.getKeyFrame(stateTime);
                currentState = ButtonState.HOVER_ANIMATING;
                break;

            case HOVER_ANIMATING:
                currentFrame = hoverAnimation.getKeyFrame(stateTime);
                if (hoverAnimation.isAnimationFinished(stateTime)) {
                    currentState = ButtonState.HOVER_STEADY;
                }
                break;

            case HOVER_STEADY:
                currentFrame = hoverAnimation.getKeyFrame(stateTime);
                break;

            case RELEASE_START:
                currentFrame = releaseAnimation.getKeyFrame(stateTime);
                currentState = ButtonState.RELEASE_ANIMATING;
                break;

            case RELEASE_ANIMATING:
                currentFrame = releaseAnimation.getKeyFrame(stateTime);
                if (releaseAnimation.isAnimationFinished(stateTime)) {
                    currentState = ButtonState.NORMAL;
                    currentFrame = normalFrame;
                }
                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        currentFrame.draw(batch, getX(), getY(), getWidth(), getHeight());
    }
}
