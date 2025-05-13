package io.github.algorythmTTV.TGAA.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Popup {
    private Stage stage;
    private Label titleLabel;
    private Label textLabel;
    private TextButton closeButton;
    private Table mainTable;

    public Popup(Stage stage, Skin skin, String title, String text) {
        this.stage = stage;

        mainTable = new Table();
        mainTable.pad(25f);
        mainTable.setBackground(skin.getDrawable("fondGris"));

        titleLabel = new Label(title, skin, "default-label");
        textLabel = new Label(text, skin, "default-label");

        closeButton = new TextButton("Fermer", skin, "default-button");
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                destroy();
            }
        });

        mainTable.add(titleLabel);
        mainTable.row();
        mainTable.add(textLabel);
        mainTable.row();
        mainTable.add(closeButton);

        mainTable.pack();

        mainTable.setPosition(
            (stage.getWidth() - this.mainTable.getWidth()) / 2,
            (stage.getHeight() - this.mainTable.getHeight()) / 2
        );

        stage.addActor(mainTable);
    }

    public void destroy(){
        if (this.mainTable != null) {
            this.mainTable.addAction(Actions.removeActor());
        }
        this.mainTable = null;
        this.titleLabel = null;
        this.textLabel = null;
        this.closeButton = null;
        this.stage = null;
    }
}
