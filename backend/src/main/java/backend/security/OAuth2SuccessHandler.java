package backend.security;

import backend.entity.User;
import backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

  private static String norm(String s) {
    return s == null ? "" : s.trim();
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {

    OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

    String sub = oidcUser.getSubject();
    String email = oidcUser.getEmail();
    String givenName = oidcUser.getGivenName();
    String familyName = oidcUser.getFamilyName();
    String picture = oidcUser.getPicture();
    Boolean emailVerified = oidcUser.getEmailVerified();

    if (sub == null || email == null) {
      response.sendError(400, "Google login missing sub/email");
      return;
    }

    String emailNorm = email.toLowerCase().trim();

    // 1) retrouver par provider+providerId (GOOGLE + sub)
    Optional<User> byGoogle = userRepository.findByProviderAndProviderId("GOOGLE", sub);

    User user = byGoogle.orElseGet(() -> {
      // 2) sinon retrouver par email (link compte local existant)
      Optional<User> byEmail = userRepository.findByEmail(emailNorm);
      if (byEmail.isPresent()) {
        User u = byEmail.get();
        u.setProvider("GOOGLE");
        u.setProviderId(sub);
        u.setEmailVerified(Boolean.TRUE.equals(emailVerified));

        if (!isBlank(givenName)) u.setFirstName(norm(givenName));
        if (!isBlank(familyName)) u.setLastName(norm(familyName));
        if (!isBlank(picture)) u.setPictureUrl(norm(picture));

        // email normalisé au passage
        u.setEmail(emailNorm);

        return userRepository.save(u);
      }

      // 3) sinon créer user
      User u = new User();
      u.setEmail(emailNorm);
      u.setFirstName(!isBlank(givenName) ? norm(givenName) : "");
      u.setLastName(!isBlank(familyName) ? norm(familyName) : "");
      u.setProvider("GOOGLE");
      u.setProviderId(sub);
      u.setEmailVerified(Boolean.TRUE.equals(emailVerified));
      if (!isBlank(picture)) u.setPictureUrl(norm(picture));
      u.setPassword(null);

      return userRepository.save(u);
    });

    // ✅ PRO : même si on retrouve byGoogle, on met à jour les infos Google
    boolean changed = false;
    if (!emailNorm.equals(user.getEmail())) { user.setEmail(emailNorm); changed = true; }
    if (!isBlank(givenName) && !norm(givenName).equals(user.getFirstName())) { user.setFirstName(norm(givenName)); changed = true; }
    if (!isBlank(familyName) && !norm(familyName).equals(user.getLastName())) { user.setLastName(norm(familyName)); changed = true; }
    if (!isBlank(picture) && (user.getPictureUrl() == null || !norm(picture).equals(user.getPictureUrl()))) { user.setPictureUrl(norm(picture)); changed = true; }
    user.setEmailVerified(Boolean.TRUE.equals(emailVerified)); // tu peux choisir de le forcer à true si tu veux
    if (changed) user = userRepository.save(user);

    String jwt = jwtService.generateToken(user.getId());

    // ✅ renvoyer au front via fragment (#token=...) : pas dans les logs serveur
    String token = URLEncoder.encode(jwt, StandardCharsets.UTF_8);
    response.sendRedirect(successRedirect + "#token=" + token);
  }
}
