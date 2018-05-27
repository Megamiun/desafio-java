package br.com.gabryel.logineer.service.impl;

import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.repository.UserRepository;
import br.com.gabryel.logineer.service.PhoneService;
import br.com.gabryel.logineer.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PhoneService phoneService;

    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PhoneService phoneService, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.phoneService = phoneService;
        this.encoder = encoder;
    }

    @Override
    public User register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("E-mail já existente");
        }

        User user = userRepository.save(convertToUser(userDto));
        userDto.getPhones()
            .forEach(phoneDto -> phoneService.register(user, phoneDto));

        return user;
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.getByEmail(email);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Usuário e/ou senha inválidos");
        }

        return user;
    }

    @Override
    public User getUser(Long id, String token) {
        return null;
    }

    @Override
    public User convertToUser(UserDto userDto) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        String uuid = UUID.randomUUID().toString();
        String password = encoder.encode(userDto.getPassword());

        return new User(uuid, today, today, now, userDto.getName(),
            userDto.getEmail(), password, uuid);
    }
}
