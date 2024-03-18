package com.example.zoomtovimeo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        // Aqui você pode implementar a lógica para processar o payload recebido do webhook
        System.out.println("Payload recebido: " + payload);

        // Você pode retornar uma resposta adequada ao chamador do webhook
        return ResponseEntity.ok("Webhook recebido com sucesso!");
    }
}
