package com.matchme.security;

import com.matchme.entity.User;
import com.matchme.service.UserService;
import com.matchme.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Skip JWT authentication for auth endpoints (login/register)
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        //if we have  auth Header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }else if(request.getCookies() != null){
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                jwt = cookie.getValue();
            break;
            }   
        }
    }
        
       if(jwt != null){   
        try {
            // Use the new single-parameter validation
            if (jwtUtil.validateToken(jwt)) {
                email = jwtUtil.extractUsername(jwt);
            } else {
                throw new Exception("Invalid token");
            }
        } catch (Exception e) {
            logger.warn("JWT token validation failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Invalid JWT token");
            return;
        }
    }

        // If no valid JWT token is provided for protected endpoints, block the request
        if (email == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Authentication required");
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null && email != null) {
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                    email, 
                    null, 
                    java.util.Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        chain.doFilter(request, response);
    }
}