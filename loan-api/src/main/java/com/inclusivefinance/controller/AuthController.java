package com.inclusivefinance.controller;

import com.inclusivefinance.common.Result;
import com.inclusivefinance.dto.LoginRequest;
import com.inclusivefinance.dto.LoginResponse;
import com.inclusivefinance.dto.RegisterRequest;
import com.inclusivefinance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success(null);
    }
}
