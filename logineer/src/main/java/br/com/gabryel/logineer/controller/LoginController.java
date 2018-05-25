package br.com.gabryel.logineer.controller;

import br.com.gabryel.logineer.controller.vo.LoginVO;
import br.com.gabryel.logineer.controller.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @PutMapping("user")
    public ResponseEntity<UserVO> createUser(UserVO userVO) {
        log.debug(userVO.toString());
        return ResponseEntity.ok(userVO);
    }


    @PostMapping("login")
    public ResponseEntity<LoginVO> login(LoginVO loginVO) {
        log.debug(loginVO.toString());
        return ResponseEntity.ok(loginVO);
    }
}
