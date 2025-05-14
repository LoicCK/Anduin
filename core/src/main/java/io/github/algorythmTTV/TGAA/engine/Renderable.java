package io.github.algorythmTTV.TGAA.engine;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Renderable {
    float getYSortValue();
    void render(Batch batch);
}
