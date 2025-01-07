package com.zerobase.plistbackend.module.user.jwt;

import com.zerobase.plistbackend.module.refresh.entity.Refresh;
import com.zerobase.plistbackend.module.refresh.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

  private final JwtUtil jwtUtil;
  private final RefreshRepository refreshRepository;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
  }

  @Transactional
  protected void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain)
      throws IOException, ServletException {

    String requestUri = request.getRequestURI();
    if (!requestUri.matches("^\\/logout$")) {

      chain.doFilter(request, response);
      return;
    }
    String requestMethod = request.getMethod();
    if (!requestMethod.equals("POST")) {

      chain.doFilter(request, response);
      return;
    }

    //get refresh token
    String refresh = jwtUtil.findToken(request, "refresh");

    //refresh null check
    if (refresh == null) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    //expired check
    try {
      jwtUtil.isExpired(refresh);
    } catch (ExpiredJwtException e) {
      //response status code
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
    String category = jwtUtil.findCategory(refresh);
    if (!category.equals("refresh")) {
      //response status code
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    //DB에 저장되어 있는지 확인
    Optional<Refresh> refreshEntity = refreshRepository.findByRefreshToken(refresh);

    if (refreshEntity.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    //로그아웃 진행
    //Refresh 토큰 DB에서 제거
    refreshRepository.deleteByRefreshToken(refresh);

    //Refresh 토큰 Cookie 값 0
    Cookie cookie = new Cookie("refresh", null);
    cookie.setMaxAge(0);
    cookie.setPath("/");

    response.addCookie(cookie);
    response.setStatus(HttpServletResponse.SC_OK);
  }
}

