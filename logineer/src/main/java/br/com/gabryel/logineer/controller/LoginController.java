package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.dto.LoginDto;
import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.dto.UserTokenDto;
import br.com.gabryel.logineer.entities.Phone;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.service.PhoneService;
import br.com.gabryel.logineer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    private final PhoneService phoneService;

    private final UserService userService;

    public LoginController(PhoneService phoneService, UserService userService) {
        this.phoneService = phoneService;
        this.userService = userService;
    }

    @PutMapping("user")
    public ResponseEntity<UserTokenDto> createUser(@RequestBody UserDto userDto) {
        User user = userService.register(userDto);

        return ResponseEntity.ok(UserTokenDto.of(user, userDto));
    }

    @PostMapping("login")
    public ResponseEntity<UserTokenDto> login(@RequestBody LoginDto loginDto) {
        User user = userService.login(loginDto.getEmail(), loginDto.getPassword());
        List<Phone> phones = phoneService.getPhones(user);

        return ResponseEntity.ok(UserTokenDto.of(user, phones));
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserTokenDto> getUser(@PathVariable String id) {
        log.info(id);

        UserDto userDto = new UserDto("unnamed", "anon@gmail.com", "anon");
        return createUser(userDto);
    }
}
