package com.example.zoomtovimeo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class UploadVideoController {

    @PostMapping("/upload")
    public ResponseEntity<String> handleEntity() {
        try {
            String url = "http://localhost:3000/upload";
            String videoFilePath = "video.mp4"; // Substitua pelo caminho do seu vídeo

                byte[] videoBytes = Files.readAllBytes(Paths.get(videoFilePath));
                VideoUploader.uploadVideo(url, videoBytes);
            return ResponseEntity.ok("Vídeo enviado com sucesso para o servidor.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar o vídeo para o servidor.");
        }
    }
}
