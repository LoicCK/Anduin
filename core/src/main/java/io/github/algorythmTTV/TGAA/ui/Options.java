package io.github.algorythmTTV.TGAA.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.algorythmTTV.TGAA.settings.KeyManager;
import io.github.algorythmTTV.TGAA.settings.Keys;
import com.badlogic.gdx.Input;

import java.util.HashMap;
import java.util.Map;

public class Options {
    final private TextureAtlas uiAtlas;
    final private Skin skin;
    Table mainTable;
    Table keysTable;
    Table visualsTable;
    Table soundTable;
    TextButton keysButton;
    TextButton visualsButton;
    TextButton soundButton;
    TextButton returnButton;
    private Cell<Table> dynamicContentCell;
    KeyManager keyManager;
    private boolean waitingForKey = false;
    private Keys keyToModify;
    private TextButton buttonToModify;
    private InputListener keyboardListener;
    private Stage stage;

    public Options(TextureAtlas uiAtlas, Skin skin, KeyManager keyManager, Stage stage) {
        this.uiAtlas = uiAtlas;
        this.skin = skin;
        this.keyManager = keyManager;
        this.stage = stage;

        mainTable = new Table(skin);

        keysButton = new TextButton("Touches", skin, "default-button");
        keysButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (dynamicContentCell != null) {
                    dynamicContentCell.setActor(keysTable);
                }
            }
        });

        visualsButton = new TextButton("Graphiques", skin, "default-button");
        visualsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (dynamicContentCell != null) {
                    dynamicContentCell.setActor(visualsTable);
                }
            }
        });

        soundButton = new TextButton("Audio", skin, "default-button");
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (dynamicContentCell != null) {
                    dynamicContentCell.setActor(soundTable);
                }
            }
        });

        Table menusTable = new Table(skin);
        menusTable.add(keysButton);
        menusTable.add(visualsButton);
        menusTable.add(soundButton);
        mainTable.add(menusTable);
        mainTable.row();

        createKeysTable();
        createVisualsTable();
        createSoundTable();
        this.dynamicContentCell = mainTable.add(keysTable);
        mainTable.row();

        returnButton = new TextButton("Retour", skin, "default-button");
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                return;
            }
        });
        mainTable.add(returnButton);
        mainTable.setBackground(skin.getDrawable("fondMenu"));

        keyboardListener = new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (waitingForKey) {
                        managePressedKey(keycode, stage);

                        event.stop();
                        return true;
                    }
                    return false;
                }
            };
    }

    private void createKeysTable() {
        keysTable = new Table(skin);

        TextureRegion keyBg = uiAtlas.findRegion("square");
        skin.add("keyButtonBg", keyBg, TextureRegion.class);

        TextButton.TextButtonStyle keyStyle = new TextButton.TextButtonStyle();
        keyStyle.font = skin.getFont("minecraft-small");
        keyStyle.fontColor = Color.WHITE;
        keyStyle.up = skin.getDrawable("keyButtonBg");
        keyStyle.over = skin.getDrawable("keyButtonBg");
        keyStyle.down = skin.getDrawable("keyButtonBg");
        skin.add("key-button", keyStyle);

        Label movementLabel = new Label("Mouvements", skin, "default-label");
        Table movementTable = new Table(skin);
        Label goUpLabel = new Label("Go up", skin, "default-label");
        TextButton goUpButton = createKeyButton(Keys.GOUP);
        Label goDownLabel = new Label("Go down", skin, "default-label");
        TextButton goDownButton = createKeyButton(Keys.GODOWN);
        Label goRightLabel = new Label("Go right", skin, "default-label");
        TextButton goRightButton = createKeyButton(Keys.GORIGHT);
        Label goLeftLabel = new Label("Go left", skin, "default-label");
        TextButton goLeftButton = createKeyButton(Keys.GOLEFT);
        Label sprintLabel = new Label("Sprint", skin, "default-label");
        TextButton sprintButton = createKeyButton(Keys.SPRINT);

        movementTable.add(goUpLabel).padRight(10f);
        movementTable.add(goUpButton);
        movementTable.row();
        movementTable.add(goDownLabel).padRight(10f);
        movementTable.add(goDownButton);
        movementTable.row();
        movementTable.add(goRightLabel).padRight(10f);
        movementTable.add(goRightButton);
        movementTable.row();
        movementTable.add(goLeftLabel).padRight(10f);
        movementTable.add(goLeftButton);
        movementTable.row();
        movementTable.add(sprintLabel).padRight(10f);
        movementTable.add(sprintButton);

        keysTable.add(movementLabel);
        keysTable.row();
        keysTable.add(movementTable);
    }

    private TextButton createKeyButton(Keys key) {
        TextButton button = new TextButton(Input.Keys.toString(keyManager.getKey(key)), skin, "key-button");
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!waitingForKey) {
                    waitingForKey = true;
                    keyToModify = key;
                    buttonToModify = (TextButton)event.getListenerActor();
                    buttonToModify.setText("Appuyez...");

                    stage.addCaptureListener(keyboardListener);
                    stage.setKeyboardFocus(buttonToModify);

                } else if (buttonToModify == event.getListenerActor()) {
                    abortKeyModifying(stage);
                }
            }
        });
        return button;
    }

    private void managePressedKey(int keycode, Stage stage) {
        if (waitingForKey && keyToModify != null && buttonToModify != null) {
            if (keycode == Input.Keys.ESCAPE) {
                buttonToModify.setText(Input.Keys.toString(keyManager.getKey(keyToModify)));
            } else {
                boolean conflit = false;
                Map<Keys, Integer> keys = keyManager.getKeys();
                if (keys.values().contains(keycode)) {
                    conflit = true;
                    new Popup(stage, skin, "", "Key " + Input.Keys.toString(keycode) + " already assigned");
                }
                if (!conflit) {
                    keyManager.setKey(keyToModify, keycode);
                    buttonToModify.setText(Input.Keys.toString(keycode));
                }
                else {
                    buttonToModify.setText(Input.Keys.toString(keyManager.getKey(keyToModify)));
                }
            }

            waitingForKey = false;
            keyToModify = null;

            if (stage != null) {
                stage.removeListener(keyboardListener);
                stage.setKeyboardFocus(null);
            }
        }
    }

    private void abortKeyModifying(Stage stage) {
        if (waitingForKey && buttonToModify != null) {
            buttonToModify.setText(Input.Keys.toString(keyManager.getKey(keyToModify)));
            waitingForKey = false;
            keysTable = null;

            if (stage != null) {
                stage.removeListener(keyboardListener);
                stage.setKeyboardFocus(null);
            }
        }
    }

    private void createVisualsTable() {
        visualsTable = new Table(skin);
    }

    private void createSoundTable() {
        soundTable = new Table(skin);
    }

    public Table getOptionsUITable() {
        return mainTable;
    }

    public TextButton getReturnButton() {
        return returnButton;
    }
}
