package ru.loper.suncore.security;

import ru.loper.suncore.config.settings.SecuritySettings;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TelegramNotifier {
    private final HttpClient httpClient;
    private final SecuritySettings settings;

    public TelegramNotifier(SecuritySettings settings) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.settings = settings;
    }

    public void sendMessage(String text) {
        if (settings.getTelegramToken() == null || settings.getTelegramToken().isBlank()) {
            return;
        }

        String url = "https://api.telegram.org/bot" + settings.getTelegramToken() + "/sendMessage";
        
        String safeText = text.replace("\"", "\\\"").replace("\n", "\\n");
        String payload = "{\"chat_id\":\"" + settings.getTelegramChatId() + "\",\"text\":\"" + safeText + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding());
    }
}