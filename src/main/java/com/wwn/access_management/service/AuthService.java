package com.wwn.access_management.service;

import com.wwn.access_management.dto.auth.*;
import com.wwn.access_management.entity.User;
import com.wwn.access_management.repository.UserRepository;
import com.wwn.access_management.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsernameOrEmail(),
                                request.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        User user =
                userRepository
                        .findByUsername(
                                userDetails.getUsername()
                        )
                        .orElseThrow(() ->
                                new BadCredentialsException(
                                        "User not found"
                                )
                        );

        String token =
                jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(
                        jwtService.getExpiration() / 1000
                )
                .user(
                        LoginResponse.UserInfo.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .roles(
                                        user.getRoles()
                                                .stream()
                                                .map(role ->
                                                        role.getName()
                                                )
                                                .toList()
                                )
                                .build()
                )
                .build();
    }
}
