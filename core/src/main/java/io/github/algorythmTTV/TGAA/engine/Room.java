package io.github.algorythmTTV.TGAA.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.algorythmTTV.TGAA.GameScreen;
import io.github.algorythmTTV.TGAA.TGAA;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import io.github.algorythmTTV.TGAA.entities.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Room {
    private final String name;
    private TiledMap map;
    private TiledMapRenderer renderer;
    private MapObjects collisionsObjects;
    private HashMap<Rectangle, MapProperties> collisionRects;
    private MapObjects teleportersObjects;
    private HashMap<Rectangle, MapProperties> teleporterRects;
    private MapObjects itemsObjects;
    private HashMap<Rectangle, MapProperties> itemsRects;
    int[] bottom;
    int[] top;
    static boolean canTeleport = true;
    ArrayList<Room> neighbours;
    AssetManager manager;
    private Array<SortableMapObject> sortableMapObjects;
    private static final String SORTABLE_OBJECT_LAYER_NAME = "DynamicObjects";

    public Room(String name, AssetManager manager, int[] bottom, int[] top) {
        this.name = name;
        this.bottom = bottom;
        this.top = top;
        neighbours = new ArrayList<Room>();
        this.manager = manager;
        this.sortableMapObjects = new Array<>();
    }

    public void prepRoom() {
        map = this.manager.get("rooms/" + name + ".tmx", TiledMap.class);
        renderer = new OrthogonalTiledMapRenderer(map, 1/2.4f);

        loadSortableObjects();

        collisionsObjects = map.getLayers().get("Collisions").getObjects();
        collisionRects = getObjectsRects(collisionsObjects);

        teleportersObjects = map.getLayers().get("Teleports").getObjects();
        teleporterRects = getObjectsRects(teleportersObjects);

        itemsObjects = map.getLayers().get("Items").getObjects();
        itemsRects = getObjectsRects(itemsObjects);
    }

    private void loadSortableObjects() {
        sortableMapObjects.clear();

        MapLayer objectLayer = map.getLayers().get(SORTABLE_OBJECT_LAYER_NAME);
        if (objectLayer != null) {
            for (MapObject mapObj : objectLayer.getObjects()) {
                SortableMapObject sortableObj = new SortableMapObject(mapObj);
                sortableMapObjects.add(sortableObj);
            }
        } else {
            Gdx.app.error("Room", "Couche d'objets triables '" + SORTABLE_OBJECT_LAYER_NAME + "' non trouvée dans la carte '" + this.name + "'.");
        }
    }

    public Array<SortableMapObject> getSortableMapObjects() {
        return sortableMapObjects;
    }

    public void addNeighbour(Room room) {
        neighbours.add(room);
    }

    public ArrayList<Room> getNeighbours() {
        return neighbours;
    }

    private HashMap<Rectangle, MapProperties> getObjectsRects(MapObjects objects) {
        List<Rectangle> rectseze = new ArrayList<>();
        HashMap<Rectangle, MapProperties> rects = new HashMap<>();
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                rects.put(new Rectangle(
                    rect.x / 2.4f,
                    rect.y / 2.4f,
                    rect.width / 2.4f,
                    rect.height / 2.4f
                ), object.getProperties());


            }
        }

        return rects;
    }

    public void renderBackground(TGAA game) {
        renderer.setView(game.camera);
        game.batch.begin();
        renderer.render(this.bottom);
        game.batch.end();
    }

    public void renderForegroundLayers(TGAA game) {
        renderer.setView(game.camera);
        game.batch.begin();
        renderer.render(this.top);
        game.batch.end();
    }

    public ArrayList<Renderable> getDynamicTileObjects() {
        ArrayList<Renderable> objects = new ArrayList<>();



        return objects;
    }

    public boolean collidesWith(float x, float y, float width, float height) {
        Rectangle objectRect = new Rectangle(x, y, width, height);

        for (Rectangle collisionRect : collisionRects.keySet()) {
            if (collisionRect.overlaps(objectRect)) {
                return true;
            }
        }

        return false;
    }

    public Object[] teleport(float x, float y, float width, float height) {
        Rectangle objectRect = new Rectangle(x, y, width, height);
        boolean inTpRect = false;
        for (Rectangle teleportRect : teleporterRects.keySet()) {
            if (teleportRect.overlaps(objectRect)) {
                if (canTeleport) {
                    MapProperties properties = teleporterRects.get(teleportRect);
                    canTeleport = false;
                    return new Object[] {properties.get("destination"), properties.get("x"), properties.get("y")};
                }
                else {
                    inTpRect = true;
                }
            }
        }
        if (!inTpRect) {
            canTeleport = true;
        }
        return null;
    }

    public Item takeItem(float x, float y, float width, float height) {
        Rectangle objectRect = new Rectangle(x, y, width, height);

        for (Rectangle itemRect : itemsRects.keySet()) {
            if (itemRect.overlaps(objectRect)) {
                return (Item) ItemFactory.createItem(itemsRects.get(itemRect).get("name").toString());
            }
        }
        return null;
    }

    public void renderItems(float x, float y, TGAA game) {
        for (Rectangle itemRect : itemsRects.keySet()) {
            float distance = Vector2.dst(x, y, itemRect.x, itemRect.y);
            if (distance < 100) {
                game.minecraftFontSmall.draw(game.batch, itemsRects.get(itemRect).get("description").toString(), itemRect.x, itemRect.y);
            }
        }
    }

    private void unloadGraphics() {
        if (renderer != null) {
            renderer = null;
        }
        // ... (nettoyage des autres ressources si Room les possède directement)
        collisionsObjects = null;
        collisionRects = null;
        teleportersObjects = null;
        teleporterRects = null;
        itemsObjects = null;
        itemsRects = null;
        sortableMapObjects.clear();
    }

    public void dispose() {
        unloadGraphics();
    }
}

