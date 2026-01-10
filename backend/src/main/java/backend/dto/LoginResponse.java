package backend.dto;

public class LoginResponse {
  private UserMeResponse user;
  private String token;

  public LoginResponse(UserMeResponse user, String token) {
    this.user = user;
    this.token = token;
  }

  public UserMeResponse getUser() { return user; }
  public String getToken() { return token; }
}
