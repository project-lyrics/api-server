package com.projectlyrics.server.global.dev.cron;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRequestClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String accessToken = AccessTokenProvider.get();

    public static HttpResponse<String> send(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}