package com.noah.jwt.web;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.noah.jwt.security.KeyUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwksController {

  private final KeyUtils keyUtils;

  public JwksController(KeyUtils keyUtils) {
    this.keyUtils = keyUtils;
  }

  @GetMapping(value = "/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, Object> jwks() {
    // Expose only the access token public key for downstream services to validate access tokens
    JWK jwk = new RSAKey.Builder(keyUtils.getAccessTokenPublicKey()).build();
    JWKSet jwkSet = new JWKSet(jwk);
    return jwkSet.toJSONObject();
  }
}
