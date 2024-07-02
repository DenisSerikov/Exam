package com.dataPg.rest.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JWTAuthorizationFilter implements Filter {

    private static final String SECRET = "yourSecretKey"; // Ваш секретный ключ
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Инициализация, если необходимо
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        String user = getAuthentication(req);

        if (user != null) {
            // Устанавливаем пользователя в сессию или контекст безопасности, если необходимо
            HttpSession session = req.getSession();
            session.setAttribute("user", user);
        }

        chain.doFilter(req, res);
    }

    private String getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // Парсим токен
            return JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();
        }
        return null;
    }

    @Override
    public void destroy() {
        // Очистка, если необходимо
    }
}
