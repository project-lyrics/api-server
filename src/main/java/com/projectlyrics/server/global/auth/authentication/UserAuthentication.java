package com.projectlyrics.server.global.auth.authentication;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

  public UserAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }

  public static UserAuthentication of(Long id) {
    return new UserAuthentication(id, null, null);
  }
}
