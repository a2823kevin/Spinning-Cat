package edu.proj;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class RequestSender {
    private static CloseableHttpClient client = HttpClientBuilder.create().build();

    public static void changeState(int doPlay, int playTime) throws IOException {
        HttpPost request = new HttpPost("http://localhost/settings/state");
        request.addHeader("content-type", "application/json");
        String content = String.format("{\"do_play\": %d, \"play_time\": %d}", doPlay, playTime);
        request.setEntity(new StringEntity(content));
        client.execute(request);
    }

    public static void changeModel(ArrayList<String> modelPath) throws IOException {
        HttpPost request = new HttpPost("http://localhost/settings/model");
        request.addHeader("content-type", "application/json");
        String content = String.format("{\"obj_path\": \"%s\", \"mtl_path\": \"%s\"}", modelPath.get(0), modelPath.get(1));
        request.setEntity(new StringEntity(content));
        client.execute(request);
    }

    public static void changeMusic(String musicPath) throws IOException {
        HttpPost request = new HttpPost("http://localhost/settings/music");
        request.addHeader("content-type", "application/json");
        String content = String.format("{\"snd_path\": \"%s\"}", musicPath);
        request.setEntity(new StringEntity(content));
        client.execute(request);
    }

    public static void changeRotationalSpeed(int rotationSpeed) throws IOException {
        HttpPost request = new HttpPost(String.format("http://localhost/settings/rotate/speed/%d", rotationSpeed));
        client.execute(request);
    }

    public static void changeRotationalDirection(int rotationalDirection) throws IOException {
        HttpPost request = new HttpPost(String.format("http://localhost/settings/rotate/direction/%d", rotationalDirection));
        client.execute(request);
    }

    public static void changeDisplayRange(double displayRange) throws IOException {
        HttpPost request = new HttpPost(String.format("http://localhost/settings/displayRange/%f", displayRange));
        client.execute(request);
    }

    public static void changeVolume(int volume) throws IOException {
        HttpPost request = new HttpPost(String.format("http://localhost/settings/volume/%d", volume));
        client.execute(request);
    }

    public static void changeCanvas(float ratio) throws IOException {
        HttpPost request = new HttpPost(String.format("http://localhost/settings/canvas/%f", ratio));
        client.execute(request);
    }

    public static void shutdownServer() throws IOException {
        HttpPost request = new HttpPost("http://localhost/shutdown");
        client.execute(request);
    }
}
