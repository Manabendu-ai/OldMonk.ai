package oldmonk.backend.dto;


public record UserResponse(
        Long id,
        String githubId,
        String username,
        String displayName,
        String avatarUrl
) {
}
