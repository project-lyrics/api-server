package com.projectlyrics.server.domain.auth.service.apple;

import com.google.gson.*;
import com.projectlyrics.server.domain.auth.dto.response.UserInfoResponse;
import com.projectlyrics.server.domain.auth.service.SocialService;
import com.projectlyrics.server.domain.auth.service.apple.dto.AppleUserInfoResponse;
import com.projectlyrics.server.domain.common.message.ErrorCode;
import com.projectlyrics.server.global.exception.JwtValidationException;
import io.jsonwebtoken.Jwts;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppleSocialService implements SocialService {

  private static final String JSON_KEY_KEYS = "keys";
  private static final String TOKEN_DELIMITER = "\\.";
  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String KEY_HEADER_KID = "kid";
  private static final String KEY_HEADER_ALG = "alg";

  private final ApplePublicKeysApiClient appleApiClient;

  @Override
  public UserInfoResponse getSocialData(String socialAccessToken) {
    AppleUserInfoResponse appleUserInfo = getUserInfo(socialAccessToken);

    return appleUserInfo.toUserInfo();
  }

  private AppleUserInfoResponse getUserInfo(String accessToken) {
    // https://appleid.apple.com/auth/keys로 HTTP GET 요청을 보내 Apple이 제공하는 Public Key 리스트를 받음
    val publicKeyList = getApplePublicKeys();

    // Public Key 리스트 중 클라이언트 측에서 보낸 것과 일치하는 키를 찾음
    // 찾은 키를 Json에서 Key 객체로 변환
    val publicKey = makePublicKey(accessToken, publicKeyList);

    // Key 객체를 복호화에 집어넣어
    val userInfo = Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .build()
        .parseClaimsJws(getTokenFromBearerString(accessToken))
        .getBody();

    JsonObject userInfoObject = (JsonObject) JsonParser.parseString(new Gson().toJson(userInfo));

    return new AppleUserInfoResponse(userInfoObject.get("sub").getAsString());
  }

  private JsonArray getApplePublicKeys() {
    String result = appleApiClient.getPublicKeys();
    val keys = (JsonObject) JsonParser.parseString(result);
    return (JsonArray) keys.get(JSON_KEY_KEYS);
  }

  // Public key 리스트 각각과 액세스 토큰을 대조해서 kid와 alg 필드가 서로 맞으면
  // 해당 키를 기준으로 Json에서 Key 객체로 변환해 반환하는 작업을 거친다.
  private PublicKey makePublicKey(String accessToken, JsonArray publicKeyList) {
    val decodeArray = accessToken.split(TOKEN_DELIMITER);
    val header = new String(Base64.getDecoder().decode(getTokenFromBearerString(decodeArray[0])));

    val kid = ((JsonObject) JsonParser.parseString(header)).get(KEY_HEADER_KID);
    val alg = ((JsonObject) JsonParser.parseString(header)).get(KEY_HEADER_ALG);
    val matchingPublicKey = findMatchingPublicKey(publicKeyList, kid, alg);

    if (Objects.isNull(matchingPublicKey)) {
      throw new JwtValidationException(ErrorCode.INVALID_KEY);
    }

    return getPublicKey(matchingPublicKey);
  }

  private String getTokenFromBearerString(String token) {
    return token.substring(TOKEN_PREFIX.length());
  }

  private JsonObject findMatchingPublicKey(JsonArray publicKeyList, JsonElement kid, JsonElement alg) {
    for (JsonElement publicKey : publicKeyList) {
      val publicKeyObject = publicKey.getAsJsonObject();
      val publicKid = publicKeyObject.get(KEY_HEADER_KID);
      val publicAlg = publicKeyObject.get(KEY_HEADER_ALG);

      if (Objects.equals(kid, publicKid) && Objects.equals(alg, publicAlg)) {
        return publicKeyObject;
      }
    }

    return null;
  }

  private PublicKey getPublicKey(JsonObject object) {
    try {
      val modulus = object.get("n").toString();
      val exponent = object.get("e").toString();

      val quotes = 1;
      val modulusBytes = Base64.getUrlDecoder().decode(modulus.substring(quotes, modulus.length() - quotes));
      val exponentBytes = Base64.getUrlDecoder().decode(exponent.substring(quotes, exponent.length() - quotes));

      val positiveNumber = 1;
      val modulusValue = new BigInteger(positiveNumber, modulusBytes);
      val exponentValue = new BigInteger(positiveNumber, exponentBytes);

      val publicKeySpec = new RSAPublicKeySpec(modulusValue, exponentValue);
      val keyFactory = KeyFactory.getInstance("RSA");

      return keyFactory.generatePublic(publicKeySpec);
    } catch (InvalidKeySpecException | NoSuchAlgorithmException exception) {
      throw new JwtValidationException(ErrorCode.INVALID_KEY);
    }
  }
}
