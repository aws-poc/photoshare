package com.aws.photosharing.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

  private final JwtHelper jwtHelper;

  public AuthInterceptor(@Autowired JwtHelper jwtHelper) {
    this.jwtHelper = jwtHelper;
  }

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
    if (request.getRequestURI().equals("/v1/authenticate")) {
      return true;
    }
    // Check to see if authorization header is set
    if (hasAuthorizationHeader(request)) {
      log.debug("Authorization header found.");
      try {
        String userName = jwtHelper.getUserName(getHeader(request, AUTHORIZATION));
        request.setAttribute("userName", userName);
        return true;
      } catch (Exception e) {
        log.error("Forbidden: request has an invalid JWT");
      }
    } else {
      log.error("Forbidden: request has an no JWT");
    }
    response.setStatus(403);
    return false;
  }

  private static boolean hasAuthorizationHeader(final HttpServletRequest request) {
    final String authorization = getHeader(request, AUTHORIZATION);
    return !StringUtils.isEmpty(authorization);
  }

  private static String getHeader(final HttpServletRequest request, final String headerName) {
    final String value = request.getHeader(headerName);
    return value == null ? null : value.trim();
  }
}
