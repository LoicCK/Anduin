package io.github.algorythmTTV.TGAA.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MusicPlayer {
    private List<Music> musicList = new ArrayList<>();
    private Music currentMusic;
    private int musicIndex = 0;
    private float musicVolume = 0f;

    public MusicPlayer() {
        loadMusic();
        Collections.shuffle(musicList);
        currentMusic = musicList.getFirst();
    }

    private void loadMusic() {
        FileHandle dirHandle = Gdx.files.internal("assets/sound/music/");
        FileHandle[] files = dirHandle.list();
        for (FileHandle file : files) {
            musicList.add(Gdx.audio.newMusic(file));
        }
    }

    public void music() {
        if (!currentMusic.isPlaying()) {
            if (musicIndex >= musicList.size()) {
                musicIndex = 0;
            }
            currentMusic = musicList.get(musicIndex);
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
        }
    }

    public void changeVolume(final float volume) {
        musicVolume = volume;
    }
}
