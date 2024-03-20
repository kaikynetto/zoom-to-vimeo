package com.example.zoomtovimeo.utils;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VideoDownloader {

    public static void downloadVideo(String downloadUrl, String saveFilePath) throws IOException {
        // Criando uma URL a partir do link de download
        URL url = new URL(downloadUrl);

        // Abrindo uma conexão para a URL e abrindo um fluxo de entrada
        try (InputStream in = url.openStream()) {
            // Criando o caminho do arquivo onde o vídeo será salvo
            Path savePath = Paths.get(saveFilePath);

            // Copiando os bytes do fluxo de entrada para o arquivo
            Files.copy(in, savePath);

            System.out.println("O vídeo foi baixado com sucesso para: " + saveFilePath);
        } catch (IOException e) {
            System.err.println("Erro ao baixar o vídeo: " + e.getMessage());
            throw e;
        }
    }

    public static void main(String[] args) {
        String downloadUrl = "https://us06web.zoom.us/rec/webhook_download/LKA3uQmsdHzedGEo8zLMJdBkU5HmunLaWBe9YrJ5SwnL4qxXRcT6dVAvMDSBMry0Sd2ppsVw1CeEqmxt.DtadLRERFg8oz4Iz/vNTbM2k4aUrXB1b4EbfH2a1O46GnoVllIqyZFC9n-PkDdbTCpflByNFQD7_V8PkMLA.E7ENhwJrTVUOXIs-";
        String saveFilePath = "video.mp4";

        try {
            downloadVideo(downloadUrl, saveFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
