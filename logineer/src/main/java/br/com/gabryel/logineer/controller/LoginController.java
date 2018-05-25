package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.controller.vo.LoginVO;
import br.com.gabryel.logineer.controller.vo.UserTokenVO;
import br.com.gabryel.logineer.controller.vo.UserVO;
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
    public ResponseEntity<UserTokenVO> createUser(UserVO userVO) {
        log.info(userVO.toString());
        LocalDate now = LocalDate.now();
        String uuid = UUID.randomUUID().toString();
        return ResponseEntity.ok(new UserTokenVO(uuid, now, now, now, uuid, userVO));
    }

    @PostMapping("login")
    public ResponseEntity<UserTokenVO> login(LoginVO loginVO) {
        log.info(loginVO.toString());

        UserVO userVO = new UserVO("unnamed", loginVO.getEmail(), loginVO.getPassword());
        return createUser(userVO);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<UserTokenVO> getUser(@PathVariable String id) {
        log.info(id);

        UserVO userVO = new UserVO("unnamed", "anon@gmail.com", "anon");
        return createUser(userVO);
    }
}
