package com.example.auth_service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_savesUserInRepo() {
        String username = "user";
        String password = "pass";

        authService.register(username, password);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(username, savedUser.getUsername());
        assertEquals(password, savedUser.getPassword());
    }
    @Test
    void register_failsIfUsernameExists() {
        String username = "user";
        String password = "pass";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User(username, password)));
        boolean exists = authService.usernameExists(username);
        assertTrue(exists);
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void login_returnsTokenIfCredentialsValid() {
        String username = "user";
        String password = "pass";
        User user = new User(username, password);
        UUID token = UUID.randomUUID();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(tokenService.generateToken(user)).thenReturn(String.valueOf(token));
        String result = authService.login(username, password);
        assertNotNull(result);
        assertEquals(String.valueOf(token), result);
    }

    @Test
    void login_returnsNullIfWrongPassword() {
        String username = "user";
        String correctPassword = "password";
        String wrongPassword = "password123";
        User user = new User(username, correctPassword);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        String result = authService.login(username, wrongPassword);
        assertNull(result);
    }
}
