package backend.security;

import backend.entity.User;
import backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Value("${app.oauth2.success-redirect}")
  private String successRedirect;

  public OAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {

    OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

    String sub = oauthUser.getAttribute("sub");
    String email = oauthUser.getAttribute("email");
    Boolean emailVerified = oauthUser.getAttribute("email_verified");
    String givenName = oauthUser.getAttribute("given_name");
    String familyName = oauthUser.getAttribute("family_name");
    String picture = oauthUser.getAttribute("picture");

    if (sub == null || email == null) {
      response.sendError(400, "Google login missing sub/email");
      return;
    }

    // 1) retrouver par provider+providerId (GOOGLE + sub)
    Optional<User> byGoogle = userRepository.findByProviderAndProviderId("GOOGLE", sub);

    User user = byGoogle.orElseGet(() -> {
      // 2) sinon retrouver par email (link compte local existant)
      Optional<User> byEmail = userRepository.findByEmail(email);
      if (byEmail.isPresent()) {
        User u = byEmail.get();
        u.setProvider("GOOGLE");
        u.setProviderId(sub);
        u.setEmailVerified(Boolean.TRUE.equals(emailVerified));
        if (u.getFirstName() == null) u.setFirstName(givenName != null ? givenName : ""); // adapte si nullable
        if (u.getLastName() == null) u.setLastName(familyName != null ? familyName : "");
        if (picture != null) u.setPictureUrl(picture);
        return userRepository.save(u);
      }

      // 3) sinon créer user
      User u = new User();
      u.setEmail(email);
      u.setFirstName(givenName != null ? givenName : "");
      u.setLastName(familyName != null ? familyName : "");
      u.setProvider("GOOGLE");
      u.setProviderId(sub);
      u.setEmailVerified(Boolean.TRUE.equals(emailVerified));
      u.setPictureUrl(picture);
      u.setPassword(null);
      return userRepository.save(u);
    });

    String jwt = jwtService.generateToken(user.getId());

    // ✅ renvoyer au front via fragment (#token=...) : pas dans les logs serveur
    String token = URLEncoder.encode(jwt, StandardCharsets.UTF_8);
    response.sendRedirect(successRedirect + "#token=" + token);
  }
}
