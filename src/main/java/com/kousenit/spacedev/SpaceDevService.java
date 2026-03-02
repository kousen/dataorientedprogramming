package com.kousenit.spacedev;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.kousenit.spacedev.SpaceDevRecords.*;

/**
 * Fetches active space station expeditions from the Launch Library 2 API.
 * Returns a Result (sealed interface) that the caller pattern-matches on.
 */
public class SpaceDevService {
    private static final String BASE_URL = "https://ll.thespacedevs.com";
    private static final String EXPEDITIONS_PATH = "/2.3.0/expeditions/?is_active=true&mode=detailed";

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    enum StatusCategory {
        SUCCESS, RATE_LIMITED, CLIENT_ERROR, SERVER_ERROR;

        static StatusCategory of(int statusCode) {
            return switch (statusCode) {
                case 200 -> SUCCESS;
                case 429 -> RATE_LIMITED;
                default -> statusCode >= 500 ? SERVER_ERROR : CLIENT_ERROR;
            };
        }
    }

    public Result fetchExpeditions() {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + EXPEDITIONS_PATH))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return switch (StatusCategory.of(response.statusCode())) {
                case SUCCESS -> {
                    var expeditionResponse = gson.fromJson(response.body(), ExpeditionResponse.class);
                    yield new Result.Success(expeditionResponse.results());
                }
                case RATE_LIMITED -> new Result.RateLimited(
                        response.headers().firstValue("Retry-After").orElse("unknown"));
                case CLIENT_ERROR ->
                        new Result.ClientError(response.statusCode(), response.body());
                case SERVER_ERROR ->
                        new Result.ServerError(response.statusCode());
            };
        } catch (Exception e) {
            return new Result.NetworkError(e.getMessage());
        }
    }
}
