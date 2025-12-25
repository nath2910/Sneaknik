package backend.controller;

import backend.dto.LoginResponse;
import backend.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import backend.dto.LoginRequest;
import backend.dto.ChangePasswordRequest;
import backend.dto.RegisterRequest;
import backend.entity.User;
import backend.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://sneaknik-2.onrender.com"
})
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
}


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
public LoginResponse login(@RequestBody LoginRequest request) {
    User user = userService.login(request);
    String token = jwtService.generateToken(user.getId());
    return new LoginResponse(user, token);
}

    @PostMapping("/change-password")
public String changePassword(
        @AuthenticationPrincipal User currentUser,
        @RequestBody ChangePasswordRequest request
) {
    userService.changePassword(currentUser.getId(), request);
    return "Mot de passe modifié";
}

    
}
