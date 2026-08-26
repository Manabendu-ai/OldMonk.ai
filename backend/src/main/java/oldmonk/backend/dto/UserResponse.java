package oldmonk.backend.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String githubId,
        String username,
        String displayName,
        String avatarUrl
) {
}
