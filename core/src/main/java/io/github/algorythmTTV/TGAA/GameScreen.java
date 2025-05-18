package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.engine.MusicPlayer;
import io.github.algorythmTTV.TGAA.engine.Renderable;
import io.github.algorythmTTV.TGAA.engine.Room;
import io.github.algorythmTTV.TGAA.entities.Player;
import io.github.algorythmTTV.TGAA.settings.KeyManager;
import io.github.algorythmTTV.TGAA.settings.Keys;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.IntStream;

public class GameScreen implements Screen {
    final TGAA game;
    private AssetManager manager;
    private Room currentRoom;
    private Player player;
    HashMap<String, Room> rooms;
    ArrayList<Room> loadedRooms;
    private MusicPlayer musicPlayer;
    private FrameBuffer pauseFbo;
    private Texture lastFrameTexture;
    private KeyManager keyManager = new KeyManager();
    private com.badlogic.gdx.utils.Array<Renderable> dynamicObjects;
    private boolean needSorting = true;

    public GameScreen(TGAA game, AssetManager assetManager) {
        this.game = game;
        manager = assetManager;

        rooms = new HashMap<>();
        loadedRooms = new ArrayList<>();

        currentRoom = new Room("bigHouse", manager, new int[] {0,1,2}, new int[] {4});
        rooms.put("bigHouse", currentRoom);

        musicPlayer = new MusicPlayer();

        Gdx.input.setCursorCatched(true);

        pauseFbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        dynamicObjects = new com.badlogic.gdx.utils.Array<Renderable>();
    }

    public void prepare() {
        currentRoom.prepRoom();

        player = new Player(manager);

        loadDynamicObjects();

        dynamicObjects.sort(Comparator.comparingDouble(Renderable::getYSortValue));
        dynamicObjects.reverse();
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }

    public void loadDynamicObjects() {
        dynamicObjects.clear();
        dynamicObjects.add(player);
        if (currentRoom.getSortableMapObjects() != null) {
            dynamicObjects.addAll(currentRoom.getSortableMapObjects());
        }
    }

    public void changeRoom(String roomName) {
        currentRoom = rooms.get(roomName);
        if (!loadedRooms.contains(currentRoom)) {
            currentRoom.prepRoom();
        }
        loadedRooms.remove(currentRoom);
        for (Room room : loadedRooms) {
            if (!currentRoom.getNeighbours().contains(room)) {
                manager.unload("rooms/" + roomName + ".tmx");
                loadedRooms.remove(room);
            }
        }
        for (Room room : currentRoom.getNeighbours()) {
            if (!loadedRooms.contains(room)) {
                loadedRooms.add(room);
            }
        }

        loadDynamicObjects();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float v) {
        manager.update();

        input(v);

        musicPlayer.music();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawGameScene();
    }

    private void drawGameScene() {
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        currentRoom.renderBackground(game);

        if (needSorting) {
            dynamicObjects.sort(Comparator.comparingDouble(Renderable::getYSortValue));
            dynamicObjects.reverse();
            needSorting = false;
        }

        game.batch.begin();

        for (Renderable renderable : dynamicObjects) {
            renderable.render(game.batch);
        }

        game.batch.end();

        currentRoom.renderForegroundLayers(game);

        //System.out.println(Gdx.graphics.getFramesPerSecond());
//        currentRoom.renderItems(player.getSprite().getX(), player.getSprite().getY(), game);
//        renderInv();
    }

    private void input(float delta) {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Texture capturedTexture = captureFrame();
            game.setScreen(new PauseScreen(game, this, capturedTexture, manager));
        }

        float speed = 100f;

        if (Gdx.input.isKeyPressed(keyManager.getKey(Keys.SPRINT))) {
            speed = 150f;
        }

        float moveSpeed = delta * speed;
        float newX, newY;
        Sprite playerSprite = player.getSprite();
        boolean isNowMoving = false;
        needSorting = false;

        if (Gdx.input.isKeyPressed(keyManager.getKey(Keys.GORIGHT))) {
            newX = playerSprite.getX() + moveSpeed;
            if (!currentRoom.collidesWith(newX+5, playerSprite.getY()-10, playerSprite.getWidth()-5, 15)) {
                playerSprite.translateX(moveSpeed);
                player.facingRight = true;
                isNowMoving = true;
                needSorting = true;
            }
        } else if (Gdx.input.isKeyPressed(keyManager.getKey(Keys.GOLEFT))) {
            newX = playerSprite.getX() - moveSpeed;
            if (!currentRoom.collidesWith(newX+5, playerSprite.getY()-10, playerSprite.getWidth()-5, 15)) {
                playerSprite.translateX(-moveSpeed);
                player.facingRight = false;
                isNowMoving = true;
                needSorting = true;
            }
        }

        if (Gdx.input.isKeyPressed(keyManager.getKey(Keys.GODOWN))) {
            newY = playerSprite.getY() - moveSpeed;
            if (!currentRoom.collidesWith(playerSprite.getX()+5, newY-10, playerSprite.getWidth()-5, 15)) {
                playerSprite.translateY(-moveSpeed);
                isNowMoving = true;
                needSorting = true;
            }
        } else if (Gdx.input.isKeyPressed(keyManager.getKey(Keys.GOUP))) {
            newY = playerSprite.getY() + moveSpeed;
            if (!currentRoom.collidesWith(playerSprite.getX()+5, newY-10, playerSprite.getWidth()-5, 15)) {
                playerSprite.translateY(moveSpeed);
                isNowMoving = true;
                needSorting = true;
            }
        }

        player.setMoving(isNowMoving);

        Object[] tp = currentRoom.teleport(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());
        if (tp != null) {
            String destination = (String) tp[0];
            int x = (int) tp[1];
            int y = (int) tp[2];
            changeRoom(destination);
            playerSprite.setPosition(x, y);
            needSorting = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Item item = currentRoom.takeItem(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());
            if (item != null) {
                if (!player.addItem(item)) {
                    item = null;
                }
            }
        }
    }

    private void renderInv() {
        int i = 1;
        HashMap<String, ArrayList<Item>> inv = player.getInventory().getMap();
        for (String item: inv.keySet()) {
            game.minecraftFontSmall.draw(game.batch, item + " x" + inv.get(item).size(), 10, 450-50*i);
            i++;
        }
    }

    private Texture captureFrame() {
        pauseFbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawGameScene();
        pauseFbo.end();
        lastFrameTexture = pauseFbo.getColorBufferTexture();
        return lastFrameTexture;
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        manager.dispose();

        musicPlayer.dispose();

        for (Room room : rooms.values()) {
            room.dispose();
        }

        if (pauseFbo != null) pauseFbo.dispose();
    }
}
