package com.projectlyrics.server.domain.auth.service.apple;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "applePublicKeysApiClient", url = "https://appleid.apple.com")
public interface ApplePublicKeysApiClient {

  @GetMapping(value = "/auth/keys")
  String getPublicKeys();
}
