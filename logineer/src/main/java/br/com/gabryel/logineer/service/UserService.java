package br.com.gabryel.logineer.service;

import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.entities.User;

public interface UserService {

    User register(UserDto user);

    User login(String email, String password);

    User getUser(String id, String token);

    User convertToUser(UserDto userDto);
}