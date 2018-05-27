package br.com.gabryel.logineer.service;

import br.com.gabryel.logineer.dto.UserDto;
import br.com.gabryel.logineer.entities.User;
import br.com.gabryel.logineer.exceptions.LogineerException;

public interface UserService {

    User register(UserDto user) throws LogineerException;

    User login(String email, String password) throws LogineerException;

    User getUser(String id, String token) throws LogineerException;

    User convertToUser(UserDto userDto);
}