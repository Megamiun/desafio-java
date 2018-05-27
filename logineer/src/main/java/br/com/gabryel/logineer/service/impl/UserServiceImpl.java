package br.com.gabryel.logineer.service.impl;

import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.exceptions.LogineerException;
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
    public User register(UserDto userDto) throws LogineerException {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw unacceptableException("E-mail já existente");
        }

        User user = userRepository.save(convertToUser(userDto));
        userDto.getPhones()
            .forEach(phoneDto -> phoneService.register(user, phoneDto));

        return user;
    }

    @Override
    public User login(String email, String password) throws LogineerException {
        Optional<User> optionalUser = userRepository.getByEmail(email);
        if (!optionalUser.isPresent() || !encoder.matches(password, optionalUser.get().getPassword())) {
            throw authenticationException("Usuário e/ou senha inválidos");
        }

        User user = optionalUser.get();
        user.setLastLogin(timeProvider.now());
        return user;
    }

    @Override
    public User getUser(String id, String token) throws LogineerException {
        User user = userRepository.findById(id)
            .orElseThrow(() -> authenticationException("Não autorizado"));

        if (!isTokenValid(token, user)) {
            throw authenticationException("Não autorizado");
        }

        if (isSessionExpired(user)) {
            throw authenticationException("Sessão inválida");
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

    private LogineerException unacceptableException(String message) {
        return new LogineerException(LogineerException.ErrorType.UNACCEPTABLE, message);
    }

    private LogineerException authenticationException(String message) {
        return new LogineerException(LogineerException.ErrorType.AUTHENTICATION, message);
    }
}
