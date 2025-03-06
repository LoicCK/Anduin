package io.github.algorythmTTV.TGAA.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import io.github.algorythmTTV.TGAA.TGAA;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private TiledMap map;
    private TiledMapRenderer renderer;
    private MapObjects objects;
    private List<Rectangle> collisionRects;

    public Room(String name, AssetManager manager) {
        this.name = name;
        map = new TmxMapLoader().load("rooms/" + name + ".tmx");
        collisionRects = new ArrayList<>();
        objects = map.getLayers().get("Collisions").getObjects();
        renderer = new OrthogonalTiledMapRenderer(map, 1/24f);
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                collisionRects.add(new Rectangle(
                    rect.x / 24f,
                    rect.y / 24f,
                    rect.width / 24f,
                    rect.height / 24f
                ));
            }
        }
    }

    public void render (TGAA game, int[] layers) {
        renderer.setView(game.camera);
        renderer.render(layers);
    }

    // Add this method to Room.java
    public boolean collidesWith(float x, float y, float width, float height) {
        Rectangle objectRect = new Rectangle(x, y, width, height);

        for (Rectangle collisionRect : collisionRects) {
            if (collisionRect.overlaps(objectRect)) {
                return true;
            }
        }

        return false;
    }
}
