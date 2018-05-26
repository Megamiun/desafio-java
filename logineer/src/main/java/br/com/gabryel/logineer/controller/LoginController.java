package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.dto.LoginDTO;
import br.com.gabryel.logineer.dto.UserDTO;
import br.com.gabryel.logineer.dto.UserTokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @PutMapping("user")
    public ResponseEntity<UserTokenDTO> createUser(UserDTO userDTO) {
        log.info(userDTO.toString());
        LocalDate now = LocalDate.now();
        String uuid = UUID.randomUUID().toString();
        return ResponseEntity.ok(new UserTokenDTO(uuid, now, now, now, uuid, userDTO));
    }

    @PostMapping("login")
    public ResponseEntity<UserTokenDTO> login(LoginDTO loginDTO) {
        log.info(loginDTO.toString());

        UserDTO userDTO = new UserDTO("unnamed", loginDTO.getEmail(), loginDTO.getPassword());
        return createUser(userDTO);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserTokenDTO> getUser(@PathVariable String id) {
        log.info(id);

        UserDTO userDTO = new UserDTO("unnamed", "anon@gmail.com", "anon");
        return createUser(userDTO);
    }
}
