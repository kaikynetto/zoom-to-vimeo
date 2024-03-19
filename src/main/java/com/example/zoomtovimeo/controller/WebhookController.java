package com.example.zoomtovimeo.controller;import com.example.zoomtovimeo.utils.HMACUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class WebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest req) {
        String timestamp = req.getHeader("x-zm-request-timestamp");
        String xZmSignature = req.getHeader("x-zm-signature");

        // Lendo o corpo da solicitação e convertendo para String
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

        // Convertendo o corpo da solicitação para JSON
        ObjectMapper objectMapper = new ObjectMapper();
        Payload payload;
        try {
            payload = objectMapper.readValue(bodyBuilder.toString(), Payload.class);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao fazer o parsing do JSON.");
        }
        System.out.println(bodyBuilder.toString());

        // Validando o campo 'event'
        String event = payload.getEvent();
        

        // Extraindo o valor de plainToken do objeto Payload
        String plainToken = payload.getPayload().getPlainToken();
        
        // Construindo a mensagem no formato especificado
        String message = "v0:" + timestamp + ":" + bodyBuilder.toString();
        String secretKey = "raPylG4GQfKmdKuYofmMHA";
        String hashForVerify = HMACUtil.generateHMAC(message, secretKey);
        
        String signature = "v0="+hashForVerify;

        if(signature.equals(xZmSignature)) {
            if (!"endpoint.url_validation".equals(event)) {
                System.out.println("INVALID");
                return ResponseEntity.badRequest().body("Evento inválido: " + event);
            } else {

                String hashForValidate = HMACUtil.generateHMAC(plainToken, secretKey);
                
                // Construindo o corpo de resposta JSON
                Map<String, Object> responseMap = new LinkedHashMap<>();
                Map<String, String> messageMap = new LinkedHashMap<>();
                responseMap.put("plainToken", plainToken);
                responseMap.put("encryptedToken", hashForValidate);
                // responseMap.put("message", messageMap);
    
                // Convertendo o objeto JSON para uma string
                try {
                    String responseBody = objectMapper.writeValueAsString(responseMap);
                    System.out.println(responseBody);
                    return ResponseEntity.ok().body(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body("Erro ao gerar a resposta JSON.");
                }
            }
        } else {
            return ResponseEntity.ok().body("Unauthorized!");
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Payload {
        @JsonProperty("event")
        private String event;

        @JsonProperty("payload")
        private PayloadContent payload;

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

        public String getPlainToken() {
            return plainToken;
        }

        public void setPlainToken(String plainToken) {
            this.plainToken = plainToken;
        }
    }
}
