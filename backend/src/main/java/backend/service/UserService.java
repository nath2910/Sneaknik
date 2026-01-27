package backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import backend.dto.LoginRequest;
import backend.dto.RegisterRequest;
import backend.dto.ChangePasswordRequest;

import backend.entity.User;
import backend.repository.UserRepository;
import backend.repository.EmailVerificationTokenRepository;
import backend.repository.PasswordResetTokenRepository;
import backend.repository.UserStatsLayoutRepository;
import backend.repository.SnkVenteRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserStatsLayoutRepository userStatsLayoutRepository;
    private final SnkVenteRepository snkVenteRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailVerificationService emailVerificationService,
                       EmailVerificationTokenRepository emailVerificationTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       UserStatsLayoutRepository userStatsLayoutRepository,
                       SnkVenteRepository snkVenteRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userStatsLayoutRepository = userStatsLayoutRepository;
        this.snkVenteRepository = snkVenteRepository;
    }

    public User register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("Email déjà utilisé");
                });

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(hashedPassword);

        User savedUser = userRepository.save(user);
        emailVerificationService.sendVerification(savedUser);
        return savedUser;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe invalide"));

        boolean ok = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!ok) {
            throw new RuntimeException("Email ou mot de passe invalide");
        }

        // Pour l’instant, on renvoie juste le user (sans le hash dans la version front)
        return user;
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
    if (request.getCurrentPassword() == null || request.getNewPassword() == null) {
        throw new RuntimeException("Champs manquants");
    }

   if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
      "Le nouveau mot de passe doit faire au moins 6 caractères");
}

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    boolean ok = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());
    if (!ok) {
        throw new RuntimeException("Mot de passe actuel incorrect");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
}

    @org.springframework.transaction.annotation.Transactional
    public void deleteAccount(Long userId) {
        if (userId == null) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        passwordResetTokenRepository.deleteByUser_Id(userId);
        emailVerificationTokenRepository.deleteByUserId(userId);
        userStatsLayoutRepository.deleteByUserId(userId);
        snkVenteRepository.deleteByUser_Id(userId);
        userRepository.deleteById(userId);
    }
}
