package io.github.algorythmTTV.TGAA;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import io.github.algorythmTTV.TGAA.engine.Item;
import io.github.algorythmTTV.TGAA.engine.Room;
import io.github.algorythmTTV.TGAA.entities.AnimatedEntity;
import io.github.algorythmTTV.TGAA.entities.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GameScreen implements Screen {
    final TGAA game;
    private AssetManager manager;
    private Room currentRoom;
    private final Player player;
    private AnimatedEntity playerIdleAnimation;
    HashMap<String, Room> rooms;
    ArrayList<Room> loadedRooms;
    private List<Music> musicList;
    private Music currentMusic;
    private int musicIndex = 0;

    public GameScreen(TGAA game) {
        this.game = game;
        manager = new AssetManager();
        manager.load("characters/player.png", Texture.class);
        manager.load("items/HealthPotion.png", Texture.class);
        manager.finishLoading();

        rooms = new HashMap<>();
        loadedRooms = new ArrayList<>();

        currentRoom = new Room("sanctuaryMain", manager, new int[] {0,1,2,3,4,5,6}, new int[] {7,8});
        currentRoom.prepRoom();
        rooms.put("sanctuaryMain", currentRoom);
        rooms.put("sanctuaryEntrance", new Room("sanctuaryEntrance", manager, new int[] {0,1,2,3,4,5,6}, new int[] {7}));

        rooms.get("sanctuaryMain").addNeighbour(rooms.get("sanctuaryEntrance"));
        rooms.get("sanctuaryEntrance").addNeighbour(rooms.get("sanctuaryMain"));

        player = new Player(manager);

        musicList = new ArrayList<>();
        loadMusic();
        Collections.shuffle(musicList);
        currentMusic = musicList.get(0);
        currentMusic.play();
        //playerIdleAnimation = new AnimatedEntity("characters/animations/player/idle.png", 124, 124, 1, 12, 0.1f, 10, 10);
    }

    private void loadMusic() {
        FileHandle dirHandle = Gdx.files.internal("assets/sound/music/");
        FileHandle[] files = dirHandle.list();
        for (FileHandle file : files) {
            musicList.add(Gdx.audio.newMusic(file));
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
                room.dispose();
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
        input(v);

        music();

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

        game.batch.end();

        // playerIdleAnimation.update(v);
        // playerIdleAnimation.render(game.batch, 10, 10);
    }

    private void input(float delta) {
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

        String tp = currentRoom.teleport(playerSprite.getX(), playerSprite.getY(), playerSprite.getWidth(), playerSprite.getHeight());
        if (tp != null) {
            String direction = tp.split(" ")[0];
            String orientation = tp.split(" ")[1];
            changeRoom(direction);
            switch (orientation) {
                case "top":
                    player.getSprite().setY(400);
                    break;
                case "bottom":
                    player.getSprite().setY(0);
                    break;
                case "left":
                    player.getSprite().setX(0);
                    break;
                case "right":
                    player.getSprite().setX(750);
                    break;
            }
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
            game.font12.draw(game.batch, item + " x" + inv.get(item).size(), 10, 450-50*i);
            i++;
        }
    }

    private void music() {
        if (!currentMusic.isPlaying()) {
            if (musicIndex >= musicList.size()) {
                musicIndex = 0;
            }
            currentMusic = musicList.get(musicIndex);
            currentMusic.play();
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
    }
}
