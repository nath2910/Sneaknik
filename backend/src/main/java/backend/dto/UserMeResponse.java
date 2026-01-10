package backend.dto;

public record UserMeResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String pictureUrl,
    String provider,
    boolean emailVerified
) {}
