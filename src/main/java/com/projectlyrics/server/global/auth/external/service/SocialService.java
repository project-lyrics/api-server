package com.projectlyrics.server.global.auth.external.service;

import com.projectlyrics.server.global.auth.external.dto.request.UserLoginRequest;
import com.projectlyrics.server.global.auth.external.dto.response.UserInfoResponse;

public abstract class SocialService {

    protected abstract UserInfoResponse login(String authorizationCode, UserLoginRequest loginRequest);
}
