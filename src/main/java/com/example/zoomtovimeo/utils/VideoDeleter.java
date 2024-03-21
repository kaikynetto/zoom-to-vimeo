package com.example.zoomtovimeo.utils;
import java.io.File;
public class VideoDeleter {

    public static void deleteVideo(String filePath) {
        File file = new File(filePath);
        
        // Verifica se o arquivo existe
        if (file.exists()) {
            // Tenta deletar o arquivo
            if (file.delete()) {
                System.out.println("Arquivo deletado com sucesso.");
            } else {
                System.out.println("Falha ao deletar o arquivo.");
            }
        } else {
            System.out.println("Arquivo não encontrado.");
        }
    }

    public static void main(String[] args) {
        String videoFilePath = "caminho/do/seu/video.mp4"; // Substitua pelo caminho do seu vídeo
        deleteVideo(videoFilePath);
    }
}
