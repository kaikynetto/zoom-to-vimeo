package com.example.zoomtovimeo.utils;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class VimeoUploader {

    private static final String CLIENT_ID = "seu_client_id";
    private static final String CLIENT_SECRET = "seu_client_secret";
    private static final String ACCESS_TOKEN_URL = "https://api.vimeo.com/oauth/authorize/client";
    private static final String SCOPE = "public private";
    private static final String BASE_URL = "https://api.vimeo.com";

    public static String getAccessToken() throws IOException {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("scope", SCOPE)
                .build();

        Request request = new Request.Builder()
                .url(ACCESS_TOKEN_URL)
                .header("Authorization", Credentials.basic(CLIENT_ID, CLIENT_SECRET))
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("access_token", response.body().string());
            return jsonObject.toString();
        }
    }

    public static String uploadVideo(File videoFile) throws IOException {
        String accessToken = getAccessToken();
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file_data", videoFile.getName(), RequestBody.create(MediaType.parse("video/*"), videoFile))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/me/videos")
                .header("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }

    // public static void main(String[] args) {
    //     try {
    //         File videoFile = new File("caminho_para_o_video.mp4");
    //         String response = uploadVideo(videoFile);
    //         System.out.println(response);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}
