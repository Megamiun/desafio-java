package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.dto.LoginDto;
import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.dto.UserTokenDto;
import br.com.gabryel.logineer.entities.Phone;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.exceptions.LogineerException;
import br.com.gabryel.logineer.service.PhoneService;
import br.com.gabryel.logineer.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("api")
public class LoginController {

    private static final String AUTH_HEADER = "Authorization";

    private final PhoneService phoneService;

    private final UserService userService;

    public LoginController(PhoneService phoneService, UserService userService) {
        this.phoneService = phoneService;
        this.userService = userService;
    }

    @PutMapping("user")
    public ResponseEntity<UserTokenDto> createUser(@RequestBody UserDto userDto) throws LogineerException {
        User user = userService.register(userDto);

        return ResponseEntity.ok(UserTokenDto.of(user, userDto));
    }

    @PostMapping("login")
    public ResponseEntity<UserTokenDto> login(@RequestBody LoginDto loginDto) throws LogineerException {
        User user = userService.login(loginDto.getEmail(), loginDto.getPassword());
        List<Phone> phones = phoneService.getPhones(user);

        return ResponseEntity.ok(UserTokenDto.of(user, phones));
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserTokenDto> getUser(@PathVariable String id, HttpServletRequest request) throws LogineerException {
        User user = userService.getUser(id, request.getHeader(AUTH_HEADER));
        List<Phone> phones = phoneService.getPhones(user);

        return ResponseEntity.ok(UserTokenDto.of(user, phones));
    }
}
