package com.projectlyrics.server.domain.auth.dto.response;

import java.util.Date;

public record UserTokenValidationResponse(
    boolean isValid,
    Date expirationDate
) {

  public static UserTokenValidationResponse of(boolean isValid, Date expirationDate) {
    return new UserTokenValidationResponse(isValid, expirationDate);
  }

  public static UserTokenValidationResponse of(boolean isValid) {
    return new UserTokenValidationResponse(isValid, null);
  }
}
