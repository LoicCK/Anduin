package io.github.algorythmTTV.TGAA.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;

import java.util.HashMap;
import java.util.Map;

public class KeyManager {
    private Map<Keys, Integer> keys;
    Preferences savedKeys = Gdx.app.getPreferences("savedKeys");

    public KeyManager() {
        keys = new HashMap<Keys, Integer>();
        updateKeys();
    }

    public void updateKeys() {
        int left = savedKeys.getInteger("goLeft", Input.Keys.A);
        keys.put(Keys.GOLEFT, left);
        int right = savedKeys.getInteger("goRight", Input.Keys.D);
        keys.put(Keys.GORIGHT, right);
        int up = savedKeys.getInteger("goUp", Input.Keys.W);
        keys.put(Keys.GOUP, up);
        int down = savedKeys.getInteger("goDown", Input.Keys.S);
        keys.put(Keys.GODOWN, down);
        int sprint = savedKeys.getInteger("sprint", Input.Keys.SHIFT_LEFT);
        keys.put(Keys.SPRINT, sprint);
    }

    public Integer getKey(Keys key) {
        return keys.get(key);
    }

    public void setKey(Keys key, Integer value) {
        keys.put(key, value);
        savedKeys.flush();
    }

    public Map<Keys, Integer> getKeys() {
        return keys;
    }
}
