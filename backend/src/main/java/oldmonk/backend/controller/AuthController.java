package oldmonk.backend.controller;

import lombok.RequiredArgsConstructor;
import oldmonk.backend.entity.User;
import oldmonk.backend.security.AppUserPrincipal;
import oldmonk.backend.security.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final CurrentUser currentUser;

    @GetMapping("/login-url")
    public Map<String, String> login_url(){
        return Map.of("url","/oauth2/authorization/github");
    }

    @GetMapping("/user")
    public ResponseEntity<UserResponse> getUser(){
        AppUserPrincipal principal = currentUser.require();
        User user = principal.getUser();
        return ResponseEntity.ok(
                new UserResponse().builder()
                        .userId(user.getId())
                        .githubId(user.getGithubId())
                        .username(user.getUsername())
                        .displayName(user.getDisplayName())
                        .avatarUrl(user.getAvatarUrl())

        );
    }
}
