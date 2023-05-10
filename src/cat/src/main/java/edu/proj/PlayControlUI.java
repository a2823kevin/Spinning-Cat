package edu.proj;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PlayControlUI extends AnchorPane{
    private VBox ctlComponent = new VBox(20);
    private Button floatingWindowButton = new Button("浮動視窗");

    public PlayControlUI(double scalingRatio, CatDB db) {
        //model
        VBox mdlDisplayUtil = new VBox(10);
        Label mdlDisplayLabel = new Label("模型顯示");
        mdlDisplayLabel.setFont(new Font(24));
        Label mdlChoiceLabel = new Label("模型選項");
        ArrayList<String> modelNames;
        try {
            modelNames = db.getModelNames();
            modelNames.add(0, "-");
        }
        catch (SQLException e) {
            modelNames = new ArrayList<String>();
            modelNames.add("-");
        }
        ChoiceBox<String> mdlChoice = new ChoiceBox<String>(FXCollections.observableArrayList(modelNames));
        mdlChoice.setValue("-");
        Label mdlRangeLabel = new Label("模型距離");
        Slider mdlRange = new Slider(1.3, 2.1, 0.1);
        mdlRange.setValue(1.7);
        mdlDisplayUtil.getChildren().addAll(mdlDisplayLabel, mdlChoiceLabel, mdlChoice, mdlRangeLabel, mdlRange);

        //model modify
        VBox mdlModifyUtil = new VBox(10);
        Label mdlSettingLabel = new Label("模型設定");
        mdlSettingLabel.setFont(new Font(24));
        Button mdlDeleteButton = new Button("刪除目前顯示的模型");
        Button mdlUploadButton = new Button("上傳模型");
        Button txtUploadButton= new Button("上傳材質");
        mdlModifyUtil.getChildren().addAll(mdlSettingLabel, mdlDeleteButton, mdlUploadButton, txtUploadButton);

        //music
        VBox mscPlayUtil = new VBox(10);
        Label mscPlayLabel = new Label("音樂播放");
        mscPlayLabel.setFont(new Font(24));
        Label mscChoiceLabel = new Label("音樂選項");
        ArrayList<String> musicNames;
        try {
            musicNames = db.getMusicNames();
            musicNames.add(0, "-");
        }
        catch (SQLException e) {
            musicNames = new ArrayList<String>();
            musicNames.add("-");
        }
        ChoiceBox<String> mscChoice = new ChoiceBox<String>(FXCollections.observableArrayList(musicNames));
        mscChoice.setValue("-");
        Label mscVolumeLabel = new Label("音量大小");
        Slider mscVolume = new Slider(0, 600, 1);
        mscPlayUtil.getChildren().addAll(mscPlayLabel, mscChoiceLabel, mscChoice, mscVolumeLabel, mscVolume);

        ctlComponent.getChildren().addAll(mdlDisplayUtil, mdlModifyUtil, mscPlayUtil);

        getChildren().addAll(ctlComponent, floatingWindowButton);
        setPosition(scalingRatio);

        //set event
        mdlChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                String modelName = mdlChoice.getItems().get((int)newValue);
                try {
                    RequestSender.changeModel(db.getModelPath(modelName));
                }
                catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        mdlRange.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                try {
                    RequestSender.changeDisplayRange((double)newValue);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mscChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                String musicName = mscChoice.getItems().get((int)newValue);
                try {
                    RequestSender.changeMusic(db.getMusicPath(musicName));
                }
                catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setPosition(double scalingRatio) {
        setTopAnchor(ctlComponent, 10*scalingRatio);
        setLeftAnchor(ctlComponent, 10*scalingRatio);
        setRightAnchor(ctlComponent, 10*scalingRatio);
        setBottomAnchor(floatingWindowButton, 10*scalingRatio);
        setLeftAnchor(floatingWindowButton, 10*scalingRatio);
        setRightAnchor(floatingWindowButton, 10*scalingRatio);
    }


    public JFXPanel toAwtComponent() {
        JFXPanel component = new JFXPanel();
        Scene s = new Scene(this);
        component.setScene(s);
        return component;
    }
}
