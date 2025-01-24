package com.kousenit.anthropic;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.kousenit.anthropic.ClaudeRecords.ClaudeMessageRequest;
import static com.kousenit.anthropic.ClaudeRecords.ClaudeMessageResponse;

public class ClaudeService {
    private static final String API_KEY = System.getenv("ANTHROPIC_API_KEY");

    public final static String CLAUDE_35_HAIKU = "claude-3-5-haiku-latest";
    public final static String CLAUDE_35_SONNET = "claude-3-5-sonnet-latest";
    public final static String CLAUDE_3_OPUS = "claude-3-opus-latest";


    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    public ClaudeMessageResponse chat(ClaudeMessageRequest chatRequest) {
        String json = gson.toJson(chatRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("anthropic-version", "2023-06-01")
                .header("x-api-key", API_KEY)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try (var client = HttpClient.newHttpClient()) {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), ClaudeMessageResponse.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
