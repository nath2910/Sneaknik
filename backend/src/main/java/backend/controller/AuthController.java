package backend.controller;

import backend.dto.LoginResponse;
import backend.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import backend.dto.LoginRequest;
import backend.dto.ChangePasswordRequest;
import backend.dto.RegisterRequest;
import backend.dto.UserMapper;
import backend.dto.UserMeResponse;
import backend.entity.User;
import backend.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
}


    @PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED)
public UserMeResponse register(@RequestBody RegisterRequest request) {
  User user = userService.register(request);
  return UserMapper.toMe(user);
}

@PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {
  User user = userService.login(request);
  String token = jwtService.generateToken(user.getId());
  return new LoginResponse(UserMapper.toMe(user), token);
}

    @PostMapping("/change-password")
public String changePassword(
        @AuthenticationPrincipal User currentUser,
        @RequestBody ChangePasswordRequest request
) {
    userService.changePassword(currentUser.getId(), request);
    return "Mot de passe modifié";
}

@GetMapping("/me")
public UserMeResponse me(@AuthenticationPrincipal User currentUser) {
    return new UserMeResponse(
        currentUser.getId(),
        currentUser.getEmail(),
        currentUser.getFirstName(),
        currentUser.getLastName(),
        currentUser.getPictureUrl(),
        currentUser.getProvider(),
        currentUser.isEmailVerified()
    );
}

    
}
