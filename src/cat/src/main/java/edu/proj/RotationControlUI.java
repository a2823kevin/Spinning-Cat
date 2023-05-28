package edu.proj;

import java.io.IOException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class RotationControlUI extends AnchorPane{
    private HBox ctlComponent = new HBox(20);
    private Button activateButton = new Button("啟動");
    private Thread countdownThread = null;

    public RotationControlUI(double scalingRatio, CatDB db) {
        //rotational speed
        HBox rotationalSpeed = new HBox(10);
        Label rsLabel = new Label("旋轉速度");
        Slider rsSlider = new Slider(1, 300, 1);
        rsSlider.setValue(60);
        Label rsUnit = new Label("60 rpm");
        rotationalSpeed.getChildren().addAll(rsLabel, rsSlider, rsUnit);
        rotationalSpeed.setAlignment(Pos.CENTER);

        //rotational direction
        HBox rotationalDirection = new HBox(10);
        ToggleGroup rdToggleGroup = new ToggleGroup();
        RadioButton rdButtonClockwise = new RadioButton("順時針");
        RadioButton rdButtonCounterclockwise = new RadioButton("逆時針");
        rdButtonClockwise.setToggleGroup(rdToggleGroup);
        rdButtonCounterclockwise.setToggleGroup(rdToggleGroup);
        rdButtonClockwise.setSelected(true);
        rotationalDirection.getChildren().addAll(rdButtonClockwise, rdButtonCounterclockwise);
        rotationalDirection.setAlignment(Pos.CENTER);

        //rotate time
        HBox rotateTime = new HBox(10);
        Label rtLabel = new Label("旋轉時間");
        Spinner<Number> rtSpinner = new Spinner<Number>(0, Integer.MAX_VALUE, 0);
        Label rtUnit = new Label("分鐘");
        rotateTime.getChildren().addAll(rtLabel, rtSpinner, rtUnit);
        rotateTime.setAlignment(Pos.CENTER);
        ctlComponent.getChildren().addAll(rotationalSpeed, rotationalDirection, rotateTime);

        getChildren().addAll(ctlComponent, activateButton);
        setPosition(scalingRatio);

        //set event
        rsSlider.valueProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                try {
                    int val = (int)Math.round(newValue.doubleValue());
                    rsUnit.setText(String.format("%d rpm", val));
                    RequestSender.changeRotationalSpeed(val);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        rdToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableToggle, Toggle oldToggle, Toggle newToggle) {
                RadioButton rb = (RadioButton) newToggle;
                try {
                    if (rb.getText()=="順時針") {
                            RequestSender.changeRotationalDirection(-1);
                    }
                    else {
                        RequestSender.changeRotationalDirection(1);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        activateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                String state = activateButton.getText();
                int doPlay = 0;
                int playTime = (int)rtSpinner.getValue();
                if (state.equals("啟動")) {
                    activateButton.setText("停止");
                    doPlay = 1;

                    if (countdownThread!=null) {
                        countdownThread.interrupt();
                    }
                    if (playTime>0) {
                        countdownThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(playTime*60000);
                                    countdownThread = null;
                                    Platform.runLater(()->{activateButton.setText("啟動");});
                                }
                                catch (InterruptedException e) {}
                            }
                        });
                        countdownThread.start();
                    }
                }
                else {
                    activateButton.setText("啟動");
                    if (countdownThread!=null) {
                        countdownThread.interrupt();
                        countdownThread = null;
                    }
                }
                
                try {
                    RequestSender.changeState(doPlay, playTime);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        });
    }

    private void setPosition(double scalingRatio) {
        setTopAnchor(ctlComponent, 10*scalingRatio);
        setLeftAnchor(ctlComponent, 10*scalingRatio);
        setBottomAnchor(ctlComponent, 10*scalingRatio);
        setTopAnchor(activateButton, 10*scalingRatio);
        setRightAnchor(activateButton, 10*scalingRatio);
        setBottomAnchor(activateButton, 10*scalingRatio);
    }

    public JFXPanel toAwtComponent() {
        JFXPanel component = new JFXPanel();
        Scene s = new Scene(this);
        component.setScene(s);
        return component;
    }
}
