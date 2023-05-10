package edu.proj;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RequestSender {
    static HttpURLConnection connection;
    static URL url;

    public static void changeState(int doPlay, int playTime) throws IOException {
        url = new URL(String.format("http://localhost/change_settings?type=%s&do_play=%d&play_time=%d",
                            "state", doPlay, playTime));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream();
    }

    public static void changeModel(ArrayList<String> modelPath) throws IOException {
        url = new URL(String.format("http://localhost/change_settings?type=%s&obj_path=%s&mtl_path=%s",
                            "model", modelPath.get(0), modelPath.get(1)));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream();
    }

    public static void changeMusic(String musicPath) throws IOException {
        url = new URL(String.format("http://localhost/change_settings?type=%s&snd_path=%s",
                            "music", musicPath));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream();
    }

    public static void changeRotationalSpeed(int rotationSpeed) throws IOException {
        url = new URL(String.format("http://localhost/change_settings?type=%s&value=%d",
                            "rotational_speed", rotationSpeed));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream();
    }

    public static void changeRotationalDirection(int rotationalDirection) throws IOException {
        url = new URL(String.format("http://localhost/change_settings?type=%s&value=%d",
                            "rotational_direction", rotationalDirection));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream();
    }

    public static void changeDisplayRange(double displayRange) throws IOException {
        url = new URL(String.format("http://localhost/change_settings?type=%s&value=%f",
                            "display_range", displayRange));
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.getInputStream();
    }
}
