package com.projectlyrics.server.domain.auth.authentication;

import java.util.Collection;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

  public UserAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }

  public static UserAuthentication of(Long id) {
    return new UserAuthentication(id, null, null);
  }

  public static UserAuthentication of(Long id, String authority) {
    return new UserAuthentication(id, null, List.of(new SimpleGrantedAuthority(authority)));
  }
}
