package backend.dto;

import backend.entity.User;

public final class UserMapper {
  private UserMapper() {}

  public static UserMeResponse toMe(User u) {
    return new UserMeResponse(
      u.getId(),
      u.getEmail(),
      u.getFirstName(),
      u.getLastName(),
      u.getPictureUrl(),
      u.getProvider(),
      u.isEmailVerified()
    );
  }
}
