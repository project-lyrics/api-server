package com.projectlyrics.server.global.dev.cron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectlyrics.server.global.dev.cron.dto.AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
public class AccessTokenProvider {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<List<String>> clients = List.of(
            List.of("eca1c80ae65f48d79242eeca56fec1b4", "c227f04fbccc46e6a828600ae301bfc1"),
            List.of("a3192d4c8a3d4f55a41fca4a85cd528f", "02d6de87c507475d82e5257d019e50e9"),
            List.of("9e31198370b148af914a1613ecd76c03", "d9f5c815851c45fe9b768e80e14381cc"),
            List.of("916497d2cb2443af82c15c77d24ff278", "dbc8b0b1256b422faccfdbe52abd04ec")
    );
    private static int index = 0;

    public static String get() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://accounts.spotify.com/api/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&client_id=" + clients.get(index).get(0) + "&client_secret=" + clients.get(index).get(1)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return objectMapper.readValue(response.body(), AccessTokenResponse.class)
                    .getAccess_token();
        } catch (Exception e) {
            log.error("failed to get access token from spotify");
            return null;
        }
    }

    public static void change() {
        ++index;

        if (index == clients.size()) {
            index = 0;
        }
    }
}
