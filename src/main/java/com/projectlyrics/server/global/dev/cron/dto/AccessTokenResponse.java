package com.projectlyrics.server.global.dev.cron.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResponse {

    private String access_token;

    public String getAccess_token() {
        return access_token;
    }
}
