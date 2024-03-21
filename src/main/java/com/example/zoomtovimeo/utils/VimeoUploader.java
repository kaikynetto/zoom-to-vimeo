package com.example.zoomtovimeo.utils;

import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class VimeoUploader {

    private static final String ACCESS_TOKEN = "079613e742f627c6133686dce66cecb5";

    public static String uploadVideo(File videoFile) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Construindo a solicitação Multipart para enviar o arquivo de vídeo
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file_data", videoFile.getName(), RequestBody.create(MediaType.parse("video/*"), videoFile))
                .build();

                System.out.println(videoFile.getName());
                System.out.println(requestBody);

        // Construindo a solicitação para enviar o vídeo para o Vimeo
        Request request = new Request.Builder()
                .url("https://api.vimeo.com/me/videos")
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }
}
