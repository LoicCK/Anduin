package io.github.algorythmTTV.TGAA.engine;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import io.github.algorythmTTV.TGAA.TGAA;

public class Room {
    private String name;
    private TiledMap map;
    private TiledMapRenderer renderer;

    public Room(String name, AssetManager manager) {
        this.name = name;
        map = new TmxMapLoader().load("rooms/" + name + ".tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1/24f);
    }

    public void render (TGAA game, float wWidth, float wHeight) {
        renderer.setView(game.camera);
        renderer.render();
    }
}
