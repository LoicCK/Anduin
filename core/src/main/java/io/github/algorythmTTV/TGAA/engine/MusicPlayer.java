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
    private int musicIndex = 0; // Sera utilisé pour la lecture séquentielle/aléatoire
    private float musicVolume = 0f;

    public MusicPlayer() {
        loadMusic();

        if (!musicList.isEmpty()) {
            Collections.shuffle(musicList);
            currentMusic = musicList.get(0);
        } else {
            Gdx.app.error("MusicPlayer_Constructor", "musicList is EMPTY");
        }
    }

    private void loadMusic() {
        String[] knownMusicFiles = {
//            "Boogie.mp3",
//            "Cruising.mp3",
//            "DeeperMeaning.mp3",
//            "Wet Hands.mp3",
//            "apart.mp3",
//            "breezy.mp3",
//            "escape-by-land-of-fire.mp3",
//            "look-inside-by-snoozybeats.mp3",
//            "tulips-by-snoozybeats.mp3",
            "symphonie des eclairs.mp3"
        };
        String musicFolderPath = "sound/music/";

        for (String fileName : knownMusicFiles) {
            if (fileName == null || fileName.trim().isEmpty()) {
                continue;
            }
            FileHandle musicFile = Gdx.files.internal(musicFolderPath + fileName.trim());

            if (musicFile.exists()) {
                try {
                    Music music = Gdx.audio.newMusic(musicFile);
                    if (music != null) {
                        musicList.add(music);
                        Gdx.app.log("MusicPlayer_loadMusic", "Successfully added: " + musicFile.name());
                    } else {
                        Gdx.app.error("MusicPlayer_loadMusic", "Gdx.audio.newMusic returned null for: " + musicFile.name());
                    }
                } catch (Exception e) {
                    Gdx.app.error("MusicPlayer_loadMusic", "Error loading music file: " + musicFile.name(), e);
                }
            } else {
                Gdx.app.error("MusicPlayer_loadMusic", "Predefined music file NOT FOUND in JAR: " + musicFile.path());
            }
        }

        if (musicList.isEmpty()) {
            Gdx.app.error("MusicPlayer_loadMusic", "No music files were successfully loaded from the predefined list!");
        } else {
            Gdx.app.log("MusicPlayer_loadMusic", "Total music files loaded: " + musicList.size());
        }
    }

    public void music() {
        if (musicList.isEmpty()) {
            return;
        }

        if (currentMusic == null && !musicList.isEmpty()) {
            currentMusic = musicList.get(musicIndex);
        }

        if (currentMusic != null && !currentMusic.isPlaying()) {
            musicIndex++;
            if (musicIndex >= musicList.size()) {
                musicIndex = 0;
            }
            currentMusic.dispose();
            currentMusic = musicList.get(musicIndex);
            currentMusic.setVolume(musicVolume);
            currentMusic.setLooping(false);
            currentMusic.play();
        }
    }

    public void changeVolume(final float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    public void dispose() {
        Gdx.app.log("MusicPlayer_dispose", "Disposing MusicPlayer resources...");
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
        for (Music music : musicList) {
            music.dispose();
        }
        musicList.clear();
        Gdx.app.log("MusicPlayer_dispose", "MusicPlayer resources disposed.");
    }
}
