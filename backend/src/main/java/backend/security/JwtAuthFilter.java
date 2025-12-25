package backend.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import backend.entity.User;
import backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    } 

    @Override
    protected void doFilterInternal(
      
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
      System.out.println("[JWT FILTER] path=" + request.getRequestURI()
  + " auth=" + (request.getHeader("Authorization") != null));


        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);
        if (!jwtService.isValid(token)) {
          System.out.println("[JWT FILTER] INVALID token path=" + request.getRequestURI());

            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtService.extractUserId(token);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // principal = user (comme ça tu peux récupérer user.getId() facilement)
        var authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.emptyList()
        );
        System.out.println("JWT OK userId=" + userId + " -----------------------------------------------------------------path=" + request.getRequestURI());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("[JWT FILTER] AUTH OK userId=" + userId + " path=" + request.getRequestURI());


        filterChain.doFilter(request, response);
    }
}
