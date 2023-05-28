package edu.proj;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.MtlWriter;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class PlayControlUI extends AnchorPane{
    private VBox ctlComponent = new VBox(20);
    private Button floatingWindowButton = new Button("浮動視窗");
    private FileChooser fileChooser = new FileChooser();
    private InputStream fis;
    private OutputStream fos;

    public PlayControlUI(double scalingRatio, CatDB db, SceneMain scene) {
        //model
        VBox mdlDisplayUtil = new VBox(10);
        Label mdlDisplayLabel = new Label("模型顯示");
        mdlDisplayLabel.setFont(new Font(24));
        Label mdlChoiceLabel = new Label("模型選項");

        ChoiceBox<String> mdlChoice = new ChoiceBox<String>();
        mdlChoice.setValue("-");
        updateModelChoice(mdlChoice, db);
        Label mdlRangeLabel = new Label("模型距離");
        Slider mdlRange = new Slider(1.3, 2.1, 0.1);
        mdlRange.setValue(1.7);
        mdlDisplayUtil.getChildren().addAll(mdlDisplayLabel, mdlChoiceLabel, mdlChoice, mdlRangeLabel, mdlRange);

        //model modify
        VBox mdlModifyUtil = new VBox(10);
        Label mdlSettingLabel = new Label("模型設定");
        mdlSettingLabel.setFont(new Font(24));
        Button mdlDeleteButton = new Button("刪除目前選擇的模型");
        HBox mdlUploadHbox = new HBox(3);
        Button mdlUploadButton = new Button("上傳模型");
        Button txtUploadButton= new Button("上傳材質");
        mdlUploadHbox.getChildren().addAll(mdlUploadButton, txtUploadButton);
        mdlModifyUtil.getChildren().addAll(mdlSettingLabel, mdlDeleteButton, mdlUploadHbox);

        //music
        VBox mscPlayUtil = new VBox(10);
        Label mscPlayLabel = new Label("音樂播放");
        mscPlayLabel.setFont(new Font(24));
        Label mscChoiceLabel = new Label("音樂選項");

        ChoiceBox<String> mscChoice = new ChoiceBox<String>();
        mscChoice.setValue("-");
        updateMusicChoice(mscChoice, db);
        Label mscVolumeLabel = new Label("音量大小");
        Slider mscVolume = new Slider(0, 100, 1);
        mscVolume.setValue(100);
        mscPlayUtil.getChildren().addAll(mscPlayLabel, mscChoiceLabel, mscChoice, mscVolumeLabel, mscVolume);

        //music modify
        VBox mscModifyUtil = new VBox(10);
        Label mscSettingLabel = new Label("音樂設定");
        mscSettingLabel.setFont(new Font(24));
        Button mscDeleteButton = new Button("刪除目前選擇的音樂");
        Button mscUploadButton = new Button("上傳音樂");
        mscModifyUtil.getChildren().addAll(mscSettingLabel, mscDeleteButton, mscUploadButton);

        //combine
        ctlComponent.getChildren().addAll(mdlDisplayUtil, mdlModifyUtil, mscPlayUtil, mscModifyUtil);
        getChildren().addAll(ctlComponent, floatingWindowButton);
        setPosition(scalingRatio);

        //set event
        mdlChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (newValue.intValue()<0) {
                    return;
                }
                String modelName = mdlChoice.getItems().get(newValue.intValue());
                try {
                    RequestSender.changeModel(db.getModelPath(modelName));
                }
                catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        mdlRange.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                try {
                    RequestSender.changeDisplayRange(newValue.doubleValue());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mdlDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                String modelName = mdlChoice.getValue();
                if (modelName.equals("-")) {
                    return;
                }

                Dialog<String> dialog = new Dialog<String>();
                dialog.setTitle("刪除模型");
                dialog.setContentText(String.format("確定刪除模型: 「%s」?", modelName));
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("是", ButtonData.OK_DONE));
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("否", ButtonData.CANCEL_CLOSE));
                String result = dialog.showAndWait().toString();
                if (result.contains("OK_DONE")) {
                    try {
                        db.deletetModel(modelName);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    mdlChoice.setValue("-");
                    updateModelChoice(mdlChoice, db);
                }
            }
        });

        mdlUploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new ExtensionFilter("3D model files", "*.obj"));
                File modelFile = fileChooser.showOpenDialog(mdlUploadButton.getScene().getWindow());
                if (modelFile==null) {
                    return;
                }

                String modelPath = modelFile.toString();
                String modelName = modelFile.getName();
                String modelFolderPath = modelPath.substring(0, modelPath.length()-modelName.length());
                String modelType = FilenameUtils.getExtension(modelName);

                switch (modelType) {
                    case "obj":
                        try {
                            fis = FileUtils.openInputStream(modelFile);
                            Obj obj = ObjReader.read(fis);
                            fis.close();

                            List<String> mtlFiles = obj.getMtlFileNames();
                            ListIterator<String> mtlIt = mtlFiles.listIterator();
                            List<String> newMtlFiles = new ArrayList<String>();
                            while (mtlIt.hasNext()) {
                                String materialPath = mtlIt.next();
                                File mtlFile = new File(materialPath);
                                if (mtlFile.isAbsolute()==false) {
                                    mtlFile = new File(modelFolderPath+materialPath);
                                }
                                fis = FileUtils.openInputStream(mtlFile);
                                List<Mtl> mtls = MtlReader.read(fis);
                                fis.close();
                                ListIterator<Mtl> it = mtls.listIterator();
                                while (it.hasNext()) {
                                    modifyMtlMapPath(it.next());
                                }

                                materialPath = "../assets/model/" + FilenameUtils.getName(materialPath);
                                fos = FileUtils.openOutputStream(new File(materialPath));
                                MtlWriter.write(mtls, fos);
                                fos.close();
                                newMtlFiles.add(FilenameUtils.getName(materialPath));
                            }
                            obj.setMtlFileNames(newMtlFiles);
                            fos = FileUtils.openOutputStream(new File("../assets/model/"+modelName));
                            ObjWriter.write(obj, fos);
                            fos.close();
                            if (newMtlFiles.size()>0) {
                                db.insertModel(modelName.substring(0, modelName.length()-modelType.length()-1), "model/"+modelName, "model/"+newMtlFiles.get(0));
                            }
                            else {
                                db.insertModel(modelName.substring(0, modelName.length()-modelType.length()-1), "model/"+modelName, "-");
                            }
                            updateModelChoice(mdlChoice, db);
                        }
                        catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }
            }
        });

        txtUploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new ExtensionFilter("image files", "*.jpg", "*.jpeg", "*.png", "*.bmp"));
                List<File> textureFiles = fileChooser.showOpenMultipleDialog(txtUploadButton.getScene().getWindow());
                if (textureFiles==null) {
                    return;
                }

                ListIterator<File> it = textureFiles.listIterator();
                while (it.hasNext()) {
                    File textureFile = it.next();
                    try {
                        fos = FileUtils.openOutputStream(new File("../assets/texture/"+textureFile.getName()));
                        fos.write(FileUtils.readFileToByteArray(textureFile));
                        fos.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mscChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                if (newValue.intValue()<0) {
                    return;
                }
                String musicName = mscChoice.getItems().get(newValue.intValue());
                try {
                    RequestSender.changeMusic(db.getMusicPath(musicName));
                }
                catch (IOException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        mscVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                int volume = newValue.intValue();
                try {
                    RequestSender.changeVolume(volume);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mscDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                String musicName = mscChoice.getValue();
                if (musicName.equals("-")) {
                    return;
                }

                Dialog<String> dialog = new Dialog<String>();
                dialog.setTitle("刪除音樂");
                dialog.setContentText(String.format("確定刪除音樂: 「%s」?", musicName));
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("是", ButtonData.OK_DONE));
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("否", ButtonData.CANCEL_CLOSE));
                String result = dialog.showAndWait().toString();
                if (result.contains("OK_DONE")) {
                    try {
                        db.deletetMusic(musicName);;
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    mscChoice.setValue("-");
                    updateMusicChoice(mscChoice, db);
                }
            }
        });

        mscUploadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new ExtensionFilter("sound files", "*.mp3", "*.wav", "*.ogg"));
                File soundFile = fileChooser.showOpenDialog(mdlUploadButton.getScene().getWindow());
                if (soundFile==null) {
                    return;
                }

                String soundName = soundFile.getName();
                String soundType = FileNameUtils.getExtension(soundName);
                
                try {
                    fos = FileUtils.openOutputStream(new File("../assets/sound/"+soundFile.getName()));
                    fos.write(FileUtils.readFileToByteArray(soundFile));
                    fos.close();
                    db.insertMusic(soundName.substring(0, soundName.length()-soundType.length()-1), "music/"+soundName);
                    updateMusicChoice(mscChoice, db);
                }
                catch (IOException | SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        floatingWindowButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                try {
                    float ratio = 0.3f;
                    RequestSender.changeCanvas(ratio);
                    scene.setVisible(false);
                    new ScenceFloatingWindow(scene, ratio);
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

    private void modifyMtlMapPath(Mtl mtl) {
        if (mtl.getMapD()!=null) {
            mtl.setMapD("texture/"+FilenameUtils.getName(mtl.getMapD()));
        }
        if (mtl.getMapKa()!=null) {
            mtl.setMapKa("texture/"+FilenameUtils.getName(mtl.getMapKa()));
        }
        if (mtl.getMapKd()!=null) {
            mtl.setMapKd("texture/"+FilenameUtils.getName(mtl.getMapKd()));
        }
        if (mtl.getMapKe()!=null) {
            mtl.setMapKe("texture/"+FilenameUtils.getName(mtl.getMapKe()));
        }
        if (mtl.getMapKs()!=null) {
            mtl.setMapKs("texture/"+FilenameUtils.getName(mtl.getMapKs()));
        }
        if (mtl.getMapNs()!=null) {
            mtl.setMapNs("texture/"+FilenameUtils.getName(mtl.getMapNs()));
        }
        if (mtl.getMapPm()!=null) {
            mtl.setMapPm("texture/"+FilenameUtils.getName(mtl.getMapPm()));
        }
        if (mtl.getMapPr()!=null) {
            mtl.setMapPr("texture/"+FilenameUtils.getName(mtl.getMapPr()));
        }
        if (mtl.getMapPs()!=null) {
            mtl.setMapPs("texture/"+FilenameUtils.getName(mtl.getMapPs()));
        }
    }

    private void updateModelChoice(ChoiceBox<String> modelChoice, CatDB db) {
        String selectedValue = modelChoice.getValue();
        ArrayList<String> modelNames;
        try {
            modelNames = db.getModelNames();
            modelNames.add(0, "-");
        }
        catch (SQLException e) {
            modelNames = new ArrayList<String>();
            modelNames.add("-");
        }
        modelChoice.setItems(FXCollections.observableArrayList(modelNames));
        modelChoice.setValue(selectedValue);
    }

    private void updateMusicChoice(ChoiceBox<String> musicChoice, CatDB db) {
        String selectedValue = musicChoice.getValue();
        ArrayList<String> musicNames;
        try {
            musicNames = db.getMusicNames();
            musicNames.add(0, "-");
        }
        catch (SQLException e) {
            musicNames = new ArrayList<String>();
            musicNames.add("-");
        }
        musicChoice.setItems(FXCollections.observableArrayList(musicNames));
        musicChoice.setValue(selectedValue);
    }
}
