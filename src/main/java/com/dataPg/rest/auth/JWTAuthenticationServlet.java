package com.dataPg.rest.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.dataPg.rest.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTAuthenticationServlet extends HttpServlet {

    private static final String SECRET = "yourSecretKey"; 
    private static final long EXPIRATION_TIME = 864_000_000; 
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    private final Map<String, String> userStore = new HashMap<>(); 

    public JWTAuthenticationServlet() {
        userStore.put("user", "password");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        Person creds = new ObjectMapper().readValue(req.getInputStream(), Person.class);

        if (authenticate(creds.getLogin(), creds.getPassword())) {
            String token = JWT.create()
                    .withSubject(creds.getLogin())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(Algorithm.HMAC512(SECRET.getBytes()));
            res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean authenticate(String username, String password) {
        return userStore.containsKey(username) && userStore.get(username).equals(password);
    }
}
