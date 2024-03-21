package com.example.zoomtovimeo.controller;

import com.example.zoomtovimeo.utils.HMACUtil;
import com.example.zoomtovimeo.utils.VideoDeleter;
import com.example.zoomtovimeo.utils.VideoDownloader;
import com.example.zoomtovimeo.utils.VimeoUploader;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class WebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest req) {
        String timestamp = req.getHeader("x-zm-request-timestamp");
        String xZmSignature = req.getHeader("x-zm-signature");

        StringBuilder bodyBuilder = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                bodyBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao ler o corpo da solicitação.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Payload payload;
        try {
            payload = objectMapper.readValue(bodyBuilder.toString(), Payload.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao fazer o parsing do JSON.");
        }    

        String event = payload.getEvent();
        System.out.println("EVENTO: " +event);

       
      
        if(event.equals("endpoint.url_validation")) {

        String plainToken = payload.getPayload().getPlainToken();
        
        String message = "v0:" + timestamp + ":" + bodyBuilder.toString();
        String secretKey = "raPylG4GQfKmdKuYofmMHA";
        String hashForVerify = HMACUtil.generateHMAC(message, secretKey);
        
        String signature = "v0="+hashForVerify;
        
        if(signature.equals(xZmSignature)) {
                String hashForValidate = HMACUtil.generateHMAC(plainToken, secretKey);
                    
                Map<String, Object> responseMap = new LinkedHashMap<>();
                responseMap.put("plainToken", plainToken);
                responseMap.put("encryptedToken", hashForValidate);

                try {
                    String responseBody = objectMapper.writeValueAsString(responseMap);
                    System.out.println("Resposta JSON: " + responseBody);
                    return ResponseEntity.ok().body(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body("Erro ao gerar a resposta JSON.");
                }
           
        }else {
            return ResponseEntity.ok().body("teste!");
        }
        } else   if(event.equals("recording.completed")) {
            List<RecordingFile> recordingFiles = payload.getPayload().getObject().getRecording_files();

               for (RecordingFile recordingFile : recordingFiles) {
                   if ("shared_screen_with_speaker_view".equals(recordingFile.getRecording_type())) {
                       String downloadUrl = recordingFile.getDownload_url();
                       String saveFilePath = "video.mp4";

                       try {
                        
                           VideoDownloader.downloadVideo(downloadUrl, saveFilePath);
                           System.out.println("Download completo!");

                           try {
                               String url = "https://send-video-to-vimeo.vercel.app/upload";
                                String videoFilePath = "video.mp4"; // Substitua pelo caminho do seu vídeo

                                    byte[] videoBytes = Files.readAllBytes(Paths.get(videoFilePath));
                                    VideoUploader.uploadVideo(url, videoBytes);
                                    VideoDeleter.deleteVideo(videoFilePath);
                                return ResponseEntity.ok("Vídeo enviado com sucesso para o servidor.");
                           } catch (IOException e){
                               e.printStackTrace();
                           }


                       } catch (IOException e) {
                           e.printStackTrace();
                           return ResponseEntity.badRequest().body("Erro ao baixar o vídeo.");
                       }
                       return ResponseEntity.ok().body("Download URL do recording_type desejado: " + downloadUrl);
                   }
               }

           System.out.println("Nenhum arquivo de gravação com o recording_type desejado foi encontrado.");
           
           return ResponseEntity.ok().body("Nenhum arquivo de gravação com o recording_type desejado foi encontrado.");
       } else {
            return ResponseEntity.ok().body("!");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Payload {
        @JsonProperty("event")
        private String event;

        @JsonProperty("payload")
        private PayloadContent payload;
  
        @JsonProperty("duration")
        private PayloadContent duration;
        
       

        public PayloadContent getDuration() {
            return duration;
        }

        public void setDuration(PayloadContent duration) {
            this.duration = duration;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public PayloadContent getPayload() {
            return payload;
        }

        public void setPayload(PayloadContent payload) {
            this.payload = payload;
        }
    }

    static class PayloadContent {
        @JsonProperty("plainToken")
        private String plainToken;
        @JsonProperty("event")
        private String event;
        

        @JsonProperty("object")
        private PayloadObject object;

        
        public String getAccount_id() {
            return account_id;
        }

        @JsonProperty("account_id")
        private String account_id;

        
        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getPlainToken() {
            return plainToken;
        }

        public void setPlainToken(String plainToken) {
            this.plainToken = plainToken;
        }

        public PayloadObject getObject() {
            return object;
        }

        public void setObject(PayloadObject object) {
            this.object = object;
        }
    }

    static class PayloadObject {
        @JsonProperty("event")
        private String event;

        
        @JsonProperty("duration")
        private float duration;

        @JsonProperty("type")
        private int type;

        
        @JsonProperty("start_time")
        private String start_time;
        
        
        @JsonProperty("topic")
        private String topic;

        
        @JsonProperty("timezone")
        private String timezone;

        
        @JsonProperty("id")
        private String id;

        @JsonProperty("uuid")
        private String uuid;

        @JsonProperty("host_id")
        private String host_id ;

        
        @JsonProperty("recording_file")
        private RecordingFile recording_file;

        
    @JsonProperty("account_id")
    private String account_id; // Adicionar esta linha

        
        @JsonProperty("account_file")
        private RecordingFile account_file;

        
    @JsonProperty("host_email")
    private String host_email;

    
    @JsonProperty("recording_count")
    private Float recording_count;

    
    @JsonProperty("share_url")
    private String share_url;

 @JsonProperty("recording_files")
    private List<RecordingFile> recording_files;


    public List<RecordingFile> getRecording_files() {
    return recording_files;
}

public void setRecording_files(List<RecordingFile> recording_files) {
    this.recording_files = recording_files;
}

    @JsonProperty("total_size")
    private Float total_size;

    @JsonProperty("play_url")
private String play_url;


@JsonProperty("password")
private String password;




@JsonProperty("on_prem")
private Boolean on_prem;

@JsonProperty("recording_play_passcode")
private String recording_play_passcode;

        
        
        public float getDuration() {
            return duration;
        }

        public void setDuration(float duration) {
            this.duration = duration;
        }
        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }
    }
    static class RecordingFile {
        @JsonProperty("recording_start")
        private String recording_start;
        
        @JsonProperty("recording_end")
        private String recording_end;
        
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("meeting_id")
        private String meeting_id;
    
        @JsonProperty("file_type")
        private String file_type;
        
        @JsonProperty("file_extension")
        private String file_extension;
    
        @JsonProperty("password")
        private String password;
    
        @JsonProperty("file_size")
        private String file_size;
        
        @JsonProperty("play_url")
        private String play_url;
    
        @JsonProperty("download_url")
        private String download_url;

        public String getDownload_url() {
            return download_url;
        }

        @JsonProperty("status")
        private String status;

        @JsonProperty("recording_type")
        private String recording_type;

        public String getRecording_type() {
            return recording_type;
        }
    }
    

}
