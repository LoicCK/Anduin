package io.github.algorythmTTV.TGAA.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;

/*
IMPORTANT
classe de https://github.com/dingjibang/GDX-RPG utilisant du code de https://github.com/LaurenzGebauer/ElementTD
 */

public class SortableMapObject implements Renderable {
    private TextureRegion textureRegion;

    private float x;
    private float y;
    private float width;
    private float height;
    private float originX;
    private float originY;
    private float scaleX = 1f;
    private float scaleY = 1f;
    private float rotation = 0f;

    private static final float SCALE = 0.4166f;

    private final float ySortValue;

    public SortableMapObject(MapObject mapObject) {
        boolean objectProcessed = false;

        if (mapObject instanceof TextureMapObject) {
            TextureMapObject textureObject = (TextureMapObject) mapObject;
            this.textureRegion = textureObject.getTextureRegion();

            if (this.textureRegion != null) {
                this.x = textureObject.getX()*SCALE;
                this.y = textureObject.getY()*SCALE;
                this.originX = textureObject.getOriginX()*SCALE;
                this.originY = textureObject.getOriginY()*SCALE;
                this.scaleX = textureObject.getScaleX()*SCALE;
                this.scaleY = textureObject.getScaleY()*SCALE;
                this.rotation = textureObject.getRotation();

                this.width = this.textureRegion.getRegionWidth();
                this.height = this.textureRegion.getRegionHeight();

                objectProcessed = true;
            } else {
                Gdx.app.error("SortableMapObject", "TextureMapObject '" + mapObject.getName() + "' n'a pas de TextureRegion.");
            }
        }

        if (!objectProcessed) {
            this.x = mapObject.getProperties().get("x", 0f, Float.class);
            this.y = mapObject.getProperties().get("y", 0f, Float.class);

            this.width = mapObject.getProperties().get("width", 0f, Float.class);
            this.height = mapObject.getProperties().get("height", 0f, Float.class);

            this.scaleX = mapObject.getProperties().get("scaleX", 1f, Float.class);
            this.scaleY = mapObject.getProperties().get("scaleY", 1f, Float.class);
            this.rotation = mapObject.getProperties().get("rotation", 0f, Float.class);
            this.originX = this.width / 2f;
            this.originY = this.height / 2f;
        }

        Gdx.app.log("SortableMapObject", "x: " + this.x + ", y: " + this.y);
        this.ySortValue = this.y;
    }

    @Override
    public float getYSortValue() {
        return ySortValue;
    }

    @Override
    public void render(Batch batch) {
        if (textureRegion != null) {
            batch.draw(textureRegion,
                x, y,
                originX, originY,
                width, height,
                scaleX, scaleY,
                rotation);
        }
    }
}
