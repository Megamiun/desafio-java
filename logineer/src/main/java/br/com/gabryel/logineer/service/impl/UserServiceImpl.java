package br.com.gabryel.logineer.service.impl;

import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.repository.UserRepository;
import br.com.gabryel.logineer.service.PhoneService;
import br.com.gabryel.logineer.service.TimeProvider;
import br.com.gabryel.logineer.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PhoneService phoneService;

    private final TimeProvider timeProvider;

    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, PhoneService phoneService, TimeProvider timeProvider, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.phoneService = phoneService;
        this.timeProvider = timeProvider;
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
        Optional<User> optionalUser = userRepository.getByEmail(email);
        if (!optionalUser.isPresent() || !encoder.matches(password, optionalUser.get().getPassword())) {
            throw new IllegalArgumentException("Usuário e/ou senha inválidos");
        }

        User user = optionalUser.get();
        user.setLastLogin(timeProvider.now());
        return user;
    }

    @Override
    public User getUser(String id, String token) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Não autorizado"));

        if (!isTokenValid(token, user)) {
            throw new IllegalArgumentException("Não autorizado");
        }

        if (isSessionExpired(user)) {
            throw new IllegalArgumentException("Sessão inválida");
        }

        return user;
    }

    private boolean isTokenValid(String token, User user) {
        if (token == null) {
            return false;
        }

        return token.equals("Bearer " + user.getToken());
    }

    /**
     * Says if the session from the user is expired, based on the last login time
     *
     * @param user User to validate
     * @return If the session can still be used
     */
    private boolean isSessionExpired(User user) {
        LocalDateTime minimumLastLoginTime = timeProvider.now().minusMinutes(30);
        LocalDateTime lastLogin = Objects.requireNonNull(user.getLastLogin());
        return lastLogin.isBefore(minimumLastLoginTime);
    }

    @Override
    public User convertToUser(UserDto userDto) {
        LocalDateTime now = timeProvider.now();
        LocalDate today = now.toLocalDate();

        String uuid = UUID.randomUUID().toString();
        String password = encoder.encode(userDto.getPassword());

        return new User(uuid, today, today, now, userDto.getName(),
            userDto.getEmail(), password, uuid);
    }
}
