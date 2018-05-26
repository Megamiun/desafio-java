package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.dto.LoginDto;
import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.dto.UserTokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("api")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @PutMapping("user")
    public ResponseEntity<UserTokenDto> createUser(@RequestBody UserDto userDto) {
        log.info(userDto.toString());
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        String uuid = UUID.randomUUID().toString();
        return ResponseEntity.ok(new UserTokenDto(uuid, today, today, now, uuid, userDto));
    }

    @PostMapping("login")
    public ResponseEntity<UserTokenDto> login(@RequestBody LoginDto loginDto) {
        log.info(loginDto.toString());

        UserDto userDto = new UserDto("unnamed", loginDto.getEmail(), loginDto.getPassword());
        return createUser(userDto);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserTokenDto> getUser(@PathVariable String id) {
        log.info(id);

        UserDto userDto = new UserDto("unnamed", "anon@gmail.com", "anon");
        return createUser(userDto);
    }
}
