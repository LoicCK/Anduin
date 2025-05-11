package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.engine.MusicPlayer;
import io.github.algorythmTTV.TGAA.engine.Room;
import io.github.algorythmTTV.TGAA.entities.Player;
import java.util.ArrayList;
import java.util.HashMap;

public class GameScreen implements Screen {
    final TGAA game;
    private AssetManager manager;
    private Room currentRoom;
    private final Player player;
    HashMap<String, Room> rooms;
    ArrayList<Room> loadedRooms;
    private MusicPlayer musicPlayer;
    private GameState currentState;

    public GameScreen(TGAA game) {
        this.game = game;
        manager = new AssetManager();
        manager.load("characters/player.png", Texture.class);
        manager.load("items/HealthPotion.png", Texture.class);
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));

        rooms = new HashMap<>();
        loadedRooms = new ArrayList<>();

        String[] mapFiles = {
            "rooms/sanctuaryMain.tmx",
            "rooms/sanctuaryEntrance.tmx"
        };

        for (String mapFile : mapFiles) {
            manager.load(mapFile, TiledMap.class);
        }
        manager.finishLoading();

        currentRoom = new Room("sanctuaryMain", manager, new int[] {0,1,2,3,4,5,6}, new int[] {7,8});
        currentRoom.prepRoom();
        rooms.put("sanctuaryMain", currentRoom);
        rooms.put("sanctuaryEntrance", new Room("sanctuaryEntrance", manager, new int[] {0,1,2,3,4,5,6}, new int[] {7}));

        rooms.get("sanctuaryMain").addNeighbour(rooms.get("sanctuaryEntrance"));
        rooms.get("sanctuaryEntrance").addNeighbour(rooms.get("sanctuaryMain"));

        player = new Player(manager);

        musicPlayer = new MusicPlayer();

        currentState = GameState.RUNNING;
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
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        currentRoom.render(game, false);

        game.batch.begin();
        player.render(game);
        game.batch.end();

        currentRoom.render(game, true);

        game.batch.begin();

        currentRoom.renderItems(player.getSprite().getX(), player.getSprite().getY(), game);

        renderInv();

        if (currentState == GameState.PAUSED) {
            float textWidth = game.font12.draw(game.batch, "PAUSED", 0, 0).width;
            float textX = (game.viewport.getWorldWidth() - textWidth) / 2;
            float textY = game.viewport.getWorldHeight() / 2;

            game.font12.draw(game.batch, "PAUSED", textX, textY);
        }

        game.batch.end();
    }

    private void input(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == GameState.RUNNING) {
                currentState = GameState.PAUSED;
            } else if (currentState == GameState.PAUSED) {
                currentState = GameState.RUNNING;
            }
        }

        if (currentState == GameState.RUNNING) {
            float speed = 100f;

            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                speed = 150f;
            }

            float moveSpeed = delta * speed;
            float newX, newY;
            Sprite playerSprite = player.getSprite();

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                newX = playerSprite.getX() + moveSpeed;
                if (!currentRoom.collidesWith(newX, playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight())) {
                    playerSprite.translateX(moveSpeed);
                }
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                newX = playerSprite.getX() - moveSpeed;
                if (!currentRoom.collidesWith(newX, playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight())) {
                    playerSprite.translateX(-moveSpeed);
                }
            }

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                newY = playerSprite.getY() - moveSpeed;
                if (!currentRoom.collidesWith(playerSprite.getX(), newY, playerSprite.getWidth(), playerSprite.getHeight())) {
                    playerSprite.translateY(-moveSpeed);
                }
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                newY = playerSprite.getY() + moveSpeed;
                if (!currentRoom.collidesWith(playerSprite.getX(), newY, playerSprite.getWidth(), playerSprite.getHeight())) {
                    playerSprite.translateY(moveSpeed);
                }
            }

            Object[] tp = currentRoom.teleport(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());
            if (tp != null) {
                String destination = (String) tp[0];
                int x = (int) tp[1];
                int y = (int) tp[2];
                changeRoom(destination);
                playerSprite.setPosition(x, y);
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
    }

    private void renderInv() {
        int i = 1;
        HashMap<String, ArrayList<Item>> inv = player.getInventory().getMap();
        for (String item: inv.keySet()) {
            game.font12.draw(game.batch, item + " x" + inv.get(item).size(), 10, 450-50*i);
            i++;
        }
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

        for (Room room : rooms.values()) {
            room.dispose();
        }
    }
}
