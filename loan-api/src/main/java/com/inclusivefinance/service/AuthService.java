package com.inclusivefinance.service;

import com.inclusivefinance.common.BusinessException;
import com.inclusivefinance.dto.LoginRequest;
import com.inclusivefinance.dto.LoginResponse;
import com.inclusivefinance.dto.RegisterRequest;
import com.inclusivefinance.entity.UserInfo;
import com.inclusivefinance.repository.UserInfoRepository;
import com.inclusivefinance.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthService {

    private final UserInfoRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserInfoRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest request) {
        UserInfo user = userRepo.findByUsername(request.username())
            .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        Map<String, Object> claims = Map.of(
            "userId", user.getId(),
            "realName", user.getRealName() != null ? user.getRealName() : "",
            "role", user.getRole(),
            "enterpriseId", user.getEnterpriseId() != null ? user.getEnterpriseId() : 0L
        );

        String token = jwtTokenProvider.generateToken(user.getUsername(), claims);

        return new LoginResponse(
            token,
            86400,
            new LoginResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole(),
                user.getEnterpriseId()
            )
        );
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepo.existsByUsername(request.username())) {
            throw new BusinessException("用户名已存在");
        }

        UserInfo user = new UserInfo();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRealName(request.realName());
        user.setPhone(request.phone());
        user.setRole("ENTERPRISE");
        user.setEnterpriseId(request.enterpriseId());
        userRepo.save(user);
    }
}
