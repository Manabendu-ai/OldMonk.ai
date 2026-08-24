package oldmonk.backend.service;

import lombok.RequiredArgsConstructor;
import oldmonk.backend.entity.User;
import oldmonk.backend.repository.UserRepository;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

