package com.kotkina.taskManagementSystemApi.services;

import com.kotkina.taskManagementSystemApi.entities.User;
import com.kotkina.taskManagementSystemApi.exceptions.EntityNotFoundException;
import com.kotkina.taskManagementSystemApi.repositories.UserRepository;
import com.kotkina.taskManagementSystemApi.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AppUserDetails getCurrentUser() throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new UsernameNotFoundException("Необходимо войти в систему со своим логином и паролем");
        } else {
            return (AppUserDetails) authentication.getPrincipal();
        }
    }

    public User getUserByEmail(String email) throws EntityNotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким email не найден: " + email));
    }
}
