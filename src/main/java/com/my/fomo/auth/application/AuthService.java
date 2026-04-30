package com.my.fomo.auth.application;

import com.my.fomo.auth.application.dto.*;
import com.my.fomo.auth.domain.User;
import com.my.fomo.auth.domain.UserRepository;
import com.my.fomo.global.exception.BusinessException;
import com.my.fomo.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenService tokenService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("이미 사용 중인 이메일입니다.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPasswd()))
                .username(request.getUsername())
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("존재하지 않는 사용자입니다.", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(request.getPasswd(), user.getPassword())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = jwtProvider.generate(user.getId(), user.getEmail(), user.getUsername());
        String refreshToken = tokenService.createRefreshToken(user.getId());

        return new LoginResponse(UserResponse.from(user), accessToken, refreshToken);
    }

    public UserResponse getUserInfo(String userId) {
        return UserResponse.from(
                userRepository.findById(userId)
                        .orElseThrow(() -> new BusinessException("존재하지 않는 사용자입니다.", HttpStatus.UNAUTHORIZED))
        );
    }

    public void logout(String userId, String accessToken) {
        tokenService.deleteRefreshToken(userId);
        long remaining = jwtProvider.getRemainingMs(accessToken);
        if (remaining > 0) {
            tokenService.blacklistAccessToken(accessToken, remaining);
        }
    }

    public LoginResponse refresh(String userId, String refreshToken) {
        if (!tokenService.isValidRefreshToken(userId, refreshToken)) {
            throw new BusinessException("유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 사용자입니다.", HttpStatus.UNAUTHORIZED));

        String newAccessToken = jwtProvider.generate(user.getId(), user.getEmail(), user.getUsername());
        String newRefreshToken = tokenService.createRefreshToken(user.getId());

        return new LoginResponse(UserResponse.from(user), newAccessToken, newRefreshToken);
    }
}
