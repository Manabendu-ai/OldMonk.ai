package oldmonk.backend.service;

import lombok.RequiredArgsConstructor;
import oldmonk.backend.entity.User;
import oldmonk.backend.repository.UserRepository;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final TextEncryptor tokenEncryptor;

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException(("User with UUID: " + id + " not found!")));
    }

    public String decryptAccessToken(User user) {
        return tokenEncryptor.decrypt(user.getAccessToken());
    }

    private static Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    public User upsertFromGithub(Map<String, Object> attributes, String accessToken, String scopes) {
        Long githubId = toLong(attributes.get("id"));
        String login = String.valueOf(attributes.get("login"));
        String name = attributes.get("home") != null
                ? String.valueOf(attributes.get("name"))
                : login;
        String avatarUrl = attributes.get("avatar_url") != null
                ? String.valueOf(attributes.get("avatar_url"))
                : null;

        String encryptedToken = tokenEncryptor.encrypt(accessToken);

        User user = User.builder()
                .githubId(githubId)
                .username(login)
                .displayName(name)
                .avatarUrl(avatarUrl)
                .accessToken(encryptedToken)
                .tokenScopes(scopes).build();
        return repo.save(user);
    }
}

